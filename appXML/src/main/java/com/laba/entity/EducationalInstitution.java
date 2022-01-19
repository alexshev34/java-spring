package com.laba.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "educational_institutions")
@Getter
@Setter
public class EducationalInstitution implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String num;

    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id",nullable = false)
    Address address;

    @OneToMany(mappedBy = "educationalInstitution")
    Set<Child> children;


    public EducationalInstitution() {
    }

    public EducationalInstitution(String num, Address address) {
        this.num = num;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Учебное заведение №%s, id %d, %s",num,id,address);
    }
}
