package com.laba.repository;

import com.laba.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region,Long> {
   @Query("select r from Region r order by r.name")
   List<Region> findAll();
}
