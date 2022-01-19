package com.laba.repository;

import com.laba.entity.Address;
import com.laba.entity.EducationalInstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitution,Long> {
    @Query("select e from EducationalInstitution e join e.address a join a.region r join Address a2 " +
            "on a2.region = a.region where a2 =:address")
    List<EducationalInstitution> searchByAddress(Address address);
}
