package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.entity.Address;
import com.ecommerce.hyperlocaldelivery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUser(User user);
    Optional<Address> findFirstByUserOrderByAddressId(User user);
}