package ru.abdusamatov.librarywithsecurity.util.annotation;

import jakarta.validation.Constraint;
import ru.abdusamatov.librarywithsecurity.util.validators.BucketNameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BucketNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBucketName {
    String message() default AnnotationConstants.BUCKET_DEFAULT_NAME;

    String regexp() default AnnotationConstants.BUCKET_DEFAULT_NAME_REGEX;
}
