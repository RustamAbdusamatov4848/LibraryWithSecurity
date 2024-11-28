package ru.abdusamatov.librarywithsecurity.util.annotation;

import jakarta.validation.Constraint;
import ru.abdusamatov.librarywithsecurity.util.validators.BucketNameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex.BUCKET_NAME_REGEX;

@Constraint(validatedBy = BucketNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBucketName {

    String message() default "Invalid bucket name";

    String regexp() default BUCKET_NAME_REGEX;
}
