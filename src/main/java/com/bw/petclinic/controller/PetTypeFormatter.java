package com.bw.petclinic.controller;

import com.bw.petclinic.domain.PetType;
import com.bw.petclinic.service.PetService;
import org.springframework.format.Formatter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class PetTypeFormatter implements Formatter<PetType> {

    private final PetService petService;

    private List<PetType> petTypes;

    private final PetType unknownPetType;

    public PetTypeFormatter(PetService petService) {
        this.petService = petService;
        this.unknownPetType = new PetType();
        this.unknownPetType.setId(-1);
        this.unknownPetType.setName("Unknown");
    }

    @Override
    @NonNull
    public PetType parse(@NonNull String text, @NonNull Locale locale) {
        if (petTypes == null) {
            petTypes = petService.findAllPetTypes();
        }
        for (PetType petType : petTypes) {
            if (petType.getName().equals(text)) {
                return petType;
            }
        }
        return unknownPetType;
    }

    @Override
    @NonNull
    public String print(PetType object, @NonNull Locale locale) {
        return object.getName();
    }
}
