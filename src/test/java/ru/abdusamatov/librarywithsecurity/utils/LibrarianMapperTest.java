package ru.abdusamatov.librarywithsecurity.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.services.mappers.LibrarianMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class LibrarianMapperTest {
    private LibrarianMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LibrarianMapper.class);
    }

    @Test
    void shouldMapLibrarianToLibrarianDto() {
        Librarian librarian = TestDataProvider.createLibrarian();

        LibrarianDto librarianDto = mapper.librarianToLibrarianDto(librarian);

        assertThat(librarianDto).isNotNull();
        assertEquals(librarianDto, librarian);
    }

    @Test
    void shouldMapLibrarianDtoToLibrarian() {
        LibrarianDto librarianDto = TestDataProvider.createLibrarianDto();
        librarianDto.setId(1L);

        Librarian librarian = mapper.librarianDtoToLibrarian(librarianDto);

        assertThat(librarian).isNotNull();
        assertEquals(librarianDto, librarian);
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

    private static void assertEquals(LibrarianDto librarianDto, Librarian librarian) {
        assertThat(librarianDto).extracting(
                LibrarianDto::getId,
                LibrarianDto::getFullName,
                LibrarianDto::getEmail
        ).containsExactly(
                librarian.getId(),
                librarian.getFullName(),
                librarian.getEmail()
        );
    }
}
