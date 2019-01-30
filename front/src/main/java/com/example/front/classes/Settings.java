package com.example.front.classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {

    private Long id;

    private Integer house_limit;

    public Integer getHouse_limit() {
        return house_limit;
    }

    public void setHouse_limit(Integer house_limit) {
        this.house_limit = house_limit;
    }
}
