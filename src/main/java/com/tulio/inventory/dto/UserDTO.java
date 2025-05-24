package com.tulio.inventory.dto;

import java.time.LocalDate;

public class UserDTO {
    private Long id;
    private String name;
    private Integer age;
    private String position;
    private LocalDate hireDate;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, Integer age, String position, LocalDate hireDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
        this.hireDate = hireDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
