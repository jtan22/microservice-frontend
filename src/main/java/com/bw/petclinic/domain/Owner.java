package com.bw.petclinic.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class Owner {

    private Integer id;

    @NotBlank(message = "Owner first name must not be blank")
    private String firstName;

    @NotBlank(message = "Owner last name must not be blank")
    private String lastName;

    @NotBlank(message = "Owner address must not be blank")
    private String address;

    @NotBlank(message = "Owner city must not be blank")
    private String city;

    @NotBlank(message = "Owner telephone must not be blank")
    private String telephone;

    private String petNames;

    private List<Pet> pets;

}
