package com.laba.repository;

import com.laba.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  AddressRepository extends JpaRepository<Address,Long> {
    Address findByStreetAndHouse(String street,String house);
}
