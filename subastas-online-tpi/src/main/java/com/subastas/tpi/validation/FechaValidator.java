package com.subastas.tpi.validation;

import com.subastas.tpi.dto.request.SubastaRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechaValidator implements ConstraintValidator<FechaCierreValida, SubastaRequestDTO> {

    @Override
    public boolean isValid(SubastaRequestDTO dto, ConstraintValidatorContext constraintValidatorContext){
        if (dto.fechaInicio() == null || dto.fechaCierre() == null) return true;

        return dto.fechaCierre().isAfter(dto.fechaInicio());
    }
}
