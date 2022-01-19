package com.laba.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "children")
@Getter
@Setter
public class Child  extends Users  implements Serializable {

    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String lastName;

    @Column(nullable = false)
    int age;

    @ManyToOne(fetch = FetchType.EAGER)
    EducationalInstitution educationalInstitution;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CHILDREN_PARENTS", joinColumns = @JoinColumn(name = "PARENTS_ID"),
            inverseJoinColumns = @JoinColumn(name = "child_id"))
    Set<Parent> parents = new HashSet<>();

    public Child() {
    }

    public Child(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }


    @Override
    public String toString() {
        return String.format("Ребенок login %s, %s,%s, %d (лет), учится: %s",login,firstName,lastName, age,educationalInstitution);
    }
}
