package com.bw.petclinic.controller;

import com.bw.petclinic.exception.OopsException;
import com.bw.petclinic.exception.PetClinicServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(PetClinicServiceException.class)
    public String handleServiceException(PetClinicServiceException ex, Model model) {
        log.info("Handle Service Exception");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(OopsException.class)
    public String handleOops(OopsException ex, Model model) {
        log.info("Handle oops");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Exception ex, Model model) {
        log.error("Handle unexpected exception", ex);
        model.addAttribute("error", "Internal Error [" + ex.getMessage() + "]");
        return "error";
    }

}
