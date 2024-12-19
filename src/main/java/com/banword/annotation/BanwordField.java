package com.banword.annotation;

import com.banword.enums.BanwordFilterPolicy;
import com.banword.service.BanwordFieldValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = BanwordFieldValidator.class)
public @interface BanwordField {
    BanwordFilterPolicy[] policies() default {};
    String message() default "Forbidden words found: %s";
    Class<?>[] groups() default {};
    java.lang.Class<? extends javax.validation.Payload>[] payload() default {};
}
