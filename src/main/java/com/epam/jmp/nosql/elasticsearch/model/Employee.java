package com.epam.jmp.nosql.elasticsearch.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class Employee {

    private String name;
    private LocalDate dob;
    private Address address;
    private String email;
    private List<String> skills;
    private Integer experience;
    private Double rating;
    private String description;
    private Boolean verified;
    private Integer salary;
}
