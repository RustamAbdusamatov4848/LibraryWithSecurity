package ru.abdusamatov.librarywithsecurity.util.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.abdusamatov.librarywithsecurity.util.annotation.ValidBucketName;

public class BucketNameValidator implements ConstraintValidator<ValidBucketName, String> {

    private String regexp;

    @Override
    public void initialize(final ValidBucketName constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.matches(regexp);
    }
}
