package com.house.demo.classes;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    @OneToMany(mappedBy="department")
    private Collection<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
}