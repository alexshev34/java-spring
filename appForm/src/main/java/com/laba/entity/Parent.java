package com.laba.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parents")
@Getter
@Setter

public class Parent  extends Users  implements Serializable {

    String firstName;
    String lastName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id",nullable = false)
    Address address;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "parents")
    @OrderBy("firstName ASC,age asc")
    Set<Child> children = new HashSet<>();

    public Parent() {
    }

    public Parent(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }




    @Override
    public String toString() {
        return String.format("Родитель login %s,  %s %s, дети: %s, %s",login,firstName,lastName,
                (children==null?"нет":children),address);
    }
}
