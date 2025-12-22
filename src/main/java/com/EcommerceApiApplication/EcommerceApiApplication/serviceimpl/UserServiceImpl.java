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

    //-----  address part ---

    public Address addAddress(Long id, Address address) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user);
        return addressRepository.save(address);
    }

    public List<Address> getAddress(long id){
       return  addressRepository.getAddressByUserId(id);
    }

    @Transactional
    public void deleteAddress( Long id) {
        addressRepository.deleteAddressByUserId(id);
    }

    public Address updateAddress(long id , Address address){
        Address updateAddress =  addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        updateAddress.setAddressLine(address.getAddressLine());
        updateAddress.setCity(address.getCity());
        updateAddress.setPostalCode(address.getPostalCode());
        updateAddress.setState(address.getState());
        updateAddress.setCountry(address.getCountry());
        Address SavedAddress = addressRepository.save(updateAddress);
        return null;
    }

    //------below are user part------


    @Override
    public UserDto registerUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return modelMapper.map(user, UserDto.class);
    }

//    @Override
//    public UserDto updateUser(Long id, UserDto userDto) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
//        user.setName(userDto.getName());
//
//
//        User UpdateUser = userRepository.save(user);
//        return null;
//    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("error"));
        userRepository.delete(user);

    }

    @Override
    public UserDto loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }
        return null;
    }

    @Override
    public UserDto findByEmail(String email) {
        return null;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        // 1. Fetch user from DB
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
