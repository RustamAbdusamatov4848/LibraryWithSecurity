package ru.abdusamatov.librarywithsecurity.util.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.models.Librarian;

@Mapper(componentModel = "spring")
public interface LibrarianMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "password", ignore = true)
    LibrarianDto librarianToLibrarianDto(Librarian librarian);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    Librarian librarianDtoToLibrarian(LibrarianDto librarianDto);
}
