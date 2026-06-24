package com.subastas.tpi.validation;

import jakarta.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = FechaValidator.class)
@Target({ElementType.RECORD_COMPONENT, TYPE})
@Retention(RUNTIME)
public @interface FechaCierreValida {

    String message() default "La fecha de cierre no puede ser anterior o igual a la fecha de inicio";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
