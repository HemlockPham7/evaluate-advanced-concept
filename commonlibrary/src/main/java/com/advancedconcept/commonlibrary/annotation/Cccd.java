package com.advancedconcept.commonlibrary.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CccdValidator.class)
public @interface Cccd {

    String message() default "person id must need exactly 12 characters";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
