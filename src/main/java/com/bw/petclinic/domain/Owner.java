package com.bw.petclinic.domain;

import lombok.Data;

@Data
public class Owner {

    private Integer id;

    private String firstName;

    private String lastName;

    private String address;

    private String city;

    private String telephone;

    private String petNames;

}
