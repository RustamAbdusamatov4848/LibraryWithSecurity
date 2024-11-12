package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.model.Librarian;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class LibrarianMapperTest {

    private final LibrarianMapper mapper = new LibrarianMapperImpl();

    @Test
    void shouldMapLibrarianToLibrarianDto() {
        Librarian librarian = TestDataProvider.createLibrarian();

        LibrarianDto librarianDto = mapper.librarianToLibrarianDto(librarian);

        assertThat(librarianDto).isNotNull();
        assertLibrariansAreEqual(librarianDto, librarian);
    }

    @Test
    void shouldMapLibrarianDtoToLibrarian() {
        LibrarianDto librarianDto = TestDataProvider.createLibrarianDto();
        librarianDto.setId(1L);

        Librarian librarian = mapper.librarianDtoToLibrarian(librarianDto);

        assertThat(librarian).isNotNull();
        assertLibrariansAreEqual(librarianDto, librarian);
    }

    @Test
    void shouldReturnNull_whenLibrarianIsNullInLibrarianToDto() {
        LibrarianDto librarianDto = mapper.librarianToLibrarianDto(null);
        assertThat(librarianDto).isNull();
    }

    @Test
    void shouldReturnNull_whenLibrarianDtoIsNullInDtoToLibrarian() {
        Librarian librarian = mapper.librarianDtoToLibrarian(null);
        assertThat(librarian).isNull();
    }

    private static void assertLibrariansAreEqual(LibrarianDto librarianDto, Librarian librarian) {
        assertThat(librarian)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(librarianDto);
    }
}
