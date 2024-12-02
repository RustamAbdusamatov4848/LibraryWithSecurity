package ru.abdusamatov.librarywithsecurity.util.annotation;

import ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex;

public class AnnotationConstants {
    public static final String BUCKET_DEFAULT_NAME = "Invalid bucket name";
    public static final String BUCKET_DEFAULT_NAME_REGEX = ValidationRegex.BUCKET_NAME_REGEX;
    public static final String BUCKET_DEFAULT_EMAIL_REGEX = ValidationRegex.EMAIL_REGEX;
}
