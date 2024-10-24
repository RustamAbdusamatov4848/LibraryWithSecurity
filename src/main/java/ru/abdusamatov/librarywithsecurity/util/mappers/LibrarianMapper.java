package ru.abdusamatov.librarywithsecurity.util.mappers;

import org.mapstruct.Mapper;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.models.Librarian;

@Mapper(componentModel = "spring")
public interface LibrarianMapper {
    LibrarianDto librarianToLibrarianDto(Librarian librarian);

    Librarian librarianDtoToLibrarian(LibrarianDto librarianDto);
}
