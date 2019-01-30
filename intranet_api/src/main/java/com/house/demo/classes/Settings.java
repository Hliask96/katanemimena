package com.house.demo.classes;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private Integer house_limit;

    public Integer getHouse_limit() {
        return house_limit;
    }

    public void setHouse_limit(Integer house_limit) {
        this.house_limit = house_limit;
    }
}