package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.model.Librarian;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LibrarianMapperTest {

    private final LibrarianMapper mapper = new LibrarianMapperImpl();

    @ParameterizedTest
    @MethodSource("shouldMapLibrarianToDto")
    void shouldMapLibrarianToDto(Librarian toBeMapped, LibrarianDto expected) {
        final var actual = mapper.librarianToLibrarianDto(toBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldMapDtoToLibrarian")
    void shouldMapDtoToLibrarian(LibrarianDto dtoToBeUpdated, Librarian expected) {
        final var actual = mapper.librarianDtoToLibrarian(dtoToBeUpdated);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnNull_whenLibrarianIsNullInLibrarianToDto() {
        final var librarianDto = mapper.librarianToLibrarianDto(null);

        assertThat(librarianDto).isNull();
    }

    @Test
    void shouldReturnNull_whenDtoIsNullInDtoToLibrarian() {
        final var librarian = mapper.librarianDtoToLibrarian(null);

        assertThat(librarian).isNull();
    }

    public static Stream<Arguments> shouldMapLibrarianToDto() {
        final var librarian = TestDataProvider
                .createLibrarian()
                .build();

        LibrarianDto expected = TestDataProvider
                .createLibrarianDto()
                .id(librarian.getId())
                .fullName(librarian.getFullName())
                .email(librarian.getEmail())
                .build();

        return Stream.of(Arguments.arguments(librarian, expected));
    }

    public static Stream<Arguments> shouldMapDtoToLibrarian() {
        LibrarianDto dtoToBeMapped = TestDataProvider
                .createLibrarianDto()
                .build();

        Librarian expected = TestDataProvider
                .createLibrarian()
                .id(dtoToBeMapped.getId())
                .fullName(dtoToBeMapped.getFullName())
                .email(dtoToBeMapped.getEmail())
                .build();

        return Stream.of(Arguments.arguments(dtoToBeMapped, expected));
    }
}
