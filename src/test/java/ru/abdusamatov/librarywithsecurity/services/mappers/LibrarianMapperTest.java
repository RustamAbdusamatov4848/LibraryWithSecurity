package ru.abdusamatov.librarywithsecurity.services.mappers;

import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class LibrarianMapperTest {

    private final LibrarianMapper mapper = new LibrarianMapperImpl();

    @Test
    void shouldMapLibrarianToLibrarianDto() {
        final var librarian = TestDataProvider.createLibrarian().build();

        final var librarianDto = mapper.librarianToLibrarianDto(librarian);

        assertThat(librarianDto).isNotNull();
        assertLibrariansAreEqual(librarianDto, librarian);
    }

    @Test
    void shouldMapLibrarianDtoToLibrarian() {
        final var librarianDto = TestDataProvider.createLibrarianDto().id(1L).build();

        final var librarian = mapper.librarianDtoToLibrarian(librarianDto);

        assertThat(librarian).isNotNull();
        assertLibrariansAreEqual(librarianDto, librarian);
    }

    @Test
    void shouldReturnNull_whenLibrarianIsNullInLibrarianToDto() {
        final var librarianDto = mapper.librarianToLibrarianDto(null);
        assertThat(librarianDto).isNull();
    }

    @Test
    void shouldReturnNull_whenLibrarianDtoIsNullInDtoToLibrarian() {
        final var librarian = mapper.librarianDtoToLibrarian(null);
        assertThat(librarian).isNull();
    }

    private static void assertLibrariansAreEqual(final LibrarianDto librarianDto, final Librarian librarian) {
        assertThat(librarian)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(librarianDto);
    }
}
