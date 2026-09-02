package com.tranquility.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//  Creating Custom Annotation
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProfilePictureValidator.class)
public @interface ValidProfilePicture {

    String message() default "Invalid profile picture";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}