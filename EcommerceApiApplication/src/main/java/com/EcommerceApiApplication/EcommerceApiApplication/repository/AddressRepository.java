package com.EcommerceApiApplication.EcommerceApiApplication.repository;

import com.EcommerceApiApplication.EcommerceApiApplication.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> getAddressByUserId(Long id);

    void deleteAddressByUserId(Long userId);
//
}
