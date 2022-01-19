package com.laba.repository;

import com.laba.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent,Object> {
    @Query("select count(p) from Parent p")
    long countAll();

    @Query("select p from Parent p where p.login=:login")
    Parent findByLogin(String login);
}
