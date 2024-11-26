package ru.abdusamatov.librarywithsecurity.util.validators;

public class ValidationRegex {
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    public static final String BUCKET_NAME_REGEX = """
            ^                       # Start of the string
            (?!xn--)                # Does not start with xn--
            (?!sthree-)             # Does not start with sthree-
            (?!sthree-configurator) # Does not start with sthree-configurator
            (?!amzn-s3-demo-)       # Does not start with amzn-s3-demo-
            (?!.*--)                # Does not contain consecutive hyphens --
            (?!.*\\.\\.)            # Does not contain consecutive dots ..
            (?!.*-s3alias$)         # Does not end with -s3alias
            (?!.*--ol-s3$)          # Does not end with --ol-s3
            (?!.*\\.mrap$)          # Does not end with .mrap
            (?!.*--x-s3$)           # Does not end with --x-s3
            (?![0-9]{1,3}(\\.[0-9]{1,3}){3}$)  # Is not an IP address
            [a-z0-9]                # Starts with a letter or digit
            ([a-z0-9.-]*[a-z0-9])?  # Valid characters within the name
            $                       # End of the string
            """;
}
