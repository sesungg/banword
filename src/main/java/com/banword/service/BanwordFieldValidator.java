package com.banword.service;

import com.banword.annotation.BanwordField;
import com.banword.core.BanwordDetection;
import com.banword.enums.BanwordFilterPolicy;
import com.banword.model.BanwordValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BanwordFieldValidator implements ConstraintValidator<BanwordField, String> {

    @Autowired
    private BanwordValidator banwordValidator;
    private Set<BanwordFilterPolicy> policies;

    @Override
    public void initialize(BanwordField constraintAnnotation) {
        this.policies = new HashSet<>(Arrays.asList(constraintAnnotation.policies()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        BanwordValidationResult validationResult = banwordValidator.validate(value, policies);

        if (validationResult.isFoundBanword()) {
            List<String> foundWords = validationResult.getDetectedBanwords().stream()
                    .map(BanwordDetection::getBanword)
                    .collect(Collectors.toList());

            String message = String.format(
                    context.getDefaultConstraintMessageTemplate(),
                    String.join(", ", foundWords)
            );

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message);

            return false;
        }

        return true;
    }
}
