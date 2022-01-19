package com.laba.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "regions")
@Getter
@Setter
public class Region implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String name;

    @OneToMany(mappedBy = "region",fetch = FetchType.EAGER)
    @OrderBy("street ASC,house asc")
    List<Address> addresses = new ArrayList<>();

    public Region(String name) {
        this.name = name;
    }

    public Region() {
    }

    @Override
    public String toString() {
        return String.format("Регион: id %d, %s",id,name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;
        Region region = (Region) o;
        return Objects.equals(id, region.id) && Objects.equals(name, region.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
