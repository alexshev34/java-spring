package com.laba.repository;

import com.laba.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long> {
    @Query("select u from Users u where u.login = :login")
    Optional<Users> findByLogin(String login);
}
