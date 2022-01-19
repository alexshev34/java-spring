package com.laba.repository;

import com.laba.entity.Child;
import com.laba.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<Child,Object> {

    @Query("select c from Child c where c.login=:login")
    Child findByLogin(String login);

}
