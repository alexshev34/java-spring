package com.laba.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@Inheritance(
        strategy = InheritanceType.JOINED
)
public class Users {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @Column(unique = true)
    String login;

    String password;
    @Enumerated(EnumType.STRING)
    ROLE role;

    public Users() {
    }
}
