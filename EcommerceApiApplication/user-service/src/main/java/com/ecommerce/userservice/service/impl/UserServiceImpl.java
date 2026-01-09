package com.ecommerce.userservice.service.impl;

import com.ecommerce.common.dto.AddressDto;
import com.ecommerce.common.dto.UserDto;
import com.ecommerce.common.exception.AddressNotFoundException;
import com.ecommerce.common.exception.EmailAlreadyExistsException;
import com.ecommerce.common.exception.UserNotFoundException;
import com.ecommerce.userservice.entity.Address;
import com.ecommerce.userservice.entity.Role;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.repository.AddressRepository;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        log.info("Attempting to register user with email: {}", userDto.getEmail());
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Registration failed: Email {} already exists", userDto.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "USER")));
        user.setRoles(Collections.singleton(userRole));

        if (user.getAddresses() != null) {
            for (Address address : user.getAddresses()) {
                address.setUser(user);
            }
        }

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getUserId());
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto createAdminUser(UserDto userDto) {
        log.info("Attempting to create admin user with email: {}", userDto.getEmail());
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Admin creation failed: Email {} already exists", userDto.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "USER")));
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);
        user.setRoles(roles);

        if (user.getAddresses() != null) {
            for (Address address : user.getAddresses()) {
                address.setUser(user);
            }
        }

        User savedUser = userRepository.save(user);
        log.info("Admin user created successfully with ID: {}", savedUser.getUserId());
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (userDto.getEmail() != null && !Objects.equals(user.getEmail(), userDto.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                log.error("Update failed: Email {} already exists", userDto.getEmail());
                throw new EmailAlreadyExistsException("Email already exists: " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }

        user.setName(userDto.getName());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", id);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    @Transactional
    public AddressDto addAddress(Long userId, AddressDto addressDto) {
        log.info("Adding address for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Address address = modelMapper.map(addressDto, Address.class);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        log.info("Address added successfully with ID: {}", savedAddress.getAddressId());
        return modelMapper.map(savedAddress, AddressDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByUserId(Long userId) {
        log.info("Fetching addresses for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return user.getAddresses().stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        log.info("Updating address with ID: {}", addressId);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setZipCode(addressDto.getZipCode());

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully");
        return modelMapper.map(updatedAddress, AddressDto.class);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        log.info("Deleting address with ID: {}", addressId);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));
        addressRepository.delete(address);
        log.info("Address deleted successfully");
    }
}
