package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.AddressDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.UserDto;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Address;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.User;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.AddressRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.UserRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final AddressRepository addressRepository;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper , AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    //-----  address part ---

    public Address addAddress(Long id, Address address) {

        log.info("Add address request received for userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found while adding address. userId={}", id);
                    return new RuntimeException("User not found");
                });

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        log.info("Address added successfully. addressId={}, userId={}",
                savedAddress.getId(), id);

        return savedAddress;
    }


    public List<Address> getAddress(long id) {

        log.info("Fetching addresses for userId={}", id);

        List<Address> addresses = addressRepository.getAddressByUserId(id);

        log.debug("Total addresses found for userId={} count={}", id, addresses.size());

        return addresses;
    }


    @Transactional
    public void deleteAddress(Long id) {

        log.warn("Delete address request received for userId={}", id);

        addressRepository.deleteAddressByUserId(id);

        log.info("Addresses deleted successfully for userId={}", id);
    }


    public Address updateAddress(long id, Address address) {

        log.info("Update address request received. addressId={}", id);

        Address updateAddress = addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Address not found for update. addressId={}", id);
                    return new RuntimeException("Address not found!");
                });

        updateAddress.setAddressLine(address.getAddressLine());
        updateAddress.setCity(address.getCity());
        updateAddress.setPostalCode(address.getPostalCode());
        updateAddress.setState(address.getState());
        updateAddress.setCountry(address.getCountry());

        Address savedAddress = addressRepository.save(updateAddress);

        log.info("Address updated successfully. addressId={}", savedAddress.getId());

        return savedAddress;
    }


    //------below are user part------


    @Override
    public UserDto registerUser(UserDto userDto) {

        log.info("User registration request received. email={}", userDto.getEmail());

        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully. userId={}", savedUser.getId());

        return modelMapper.map(savedUser, UserDto.class);
    }


    @Override
    public UserDto getUserById(Long id) {

        log.info("Fetching user details. userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found. userId={}", id);
                    return new RuntimeException("User not found!");
                });

        return modelMapper.map(user, UserDto.class);
    }




    @Override
    public void deleteUser(Long id) {

        log.warn("Delete user request received. userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found while deleting. userId={}", id);
                    return new RuntimeException("User not found");
                });

        userRepository.delete(user);

        log.info("User deleted successfully. userId={}", id);
    }


    @Override
    public UserDto loginUser(String email, String password) {

        log.info("Login attempt for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Invalid login attempt. email={}", email);
                    return new RuntimeException("Invalid email or password");
                });

        if (!user.getPassword().equals(password)) {
            log.warn("Invalid password attempt. email={}", email);
            throw new RuntimeException("Invalid email or password");
        }

        log.info("User logged in successfully. userId={}", user.getId());

        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public UserDto findByEmail(String email) {
        return null;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {

        log.info("Fetching user details. userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Update basic fields
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        // 3. Update addresses if provided
        if (userDto.getAddresses() != null) {
            List<Address> addressList = new ArrayList<>();
            for (AddressDto addrDto : userDto.getAddresses()) {
                Address addr = new Address();
                addr.setId(addrDto.getId()); // optional: if you want to update existing addresses
                addr.setAddressLine(addrDto.getAddressLine());
                addr.setCity(addrDto.getCity());
                addr.setState(addrDto.getState());
                addr.setPostalCode(addrDto.getPostalCode());
                addr.setCountry(addrDto.getCountry());
                addr.setUser(user); // important: set the relation
                addressList.add(addr);
            }
            user.setAddresses(addressList);
        }

        // 4. Save updated user
        User updatedUser = userRepository.save(user);

        // 5. Map back to UserDto
        UserDto updatedDto = new UserDto();
        updatedDto.setId(updatedUser.getId());
        updatedDto.setName(updatedUser.getName());
        updatedDto.setEmail(updatedUser.getEmail());
        updatedDto.setPassword(updatedUser.getPassword());

        List<AddressDto> updatedAddresses = new ArrayList<>();
        if (updatedUser.getAddresses() != null) {
            for (Address addr : updatedUser.getAddresses()) {
                AddressDto dto = new AddressDto();
                dto.setId(addr.getId());
                dto.setAddressLine(addr.getAddressLine());
                dto.setCity(addr.getCity());
                dto.setState(addr.getState());
                dto.setPostalCode(addr.getPostalCode());
                dto.setCountry(addr.getCountry());
                updatedAddresses.add(dto);
            }
        }
        updatedDto.setAddresses(updatedAddresses);

        return updatedDto;
    }

}
