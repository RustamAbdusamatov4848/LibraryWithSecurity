package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorSurname", target = "authorSurname")
    @Mapping(source = "yearOfPublication", target = "yearOfPublication")
    @Mapping(source = "takenAt", target = "takenAt")
    @Mapping(source = "owner.id", target = "readerId")
    BookDto bookToBookDto(Book book);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorSurname", target = "authorSurname")
    @Mapping(source = "yearOfPublication", target = "yearOfPublication")
    @Mapping(source = "takenAt", target = "takenAt")
    @Mapping(source = "readerId", target = "owner.id")
    Book bookDtoToBook(BookDto bookDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "title", target = "title")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorSurname", target = "authorSurname")
    @Mapping(source = "yearOfPublication", target = "yearOfPublication")
    @Mapping(source = "takenAt", target = "takenAt")
    @Mapping(source = "readerId", target = "owner.id")
    Book updateBookFromDto(BookDto bookDto, @MappingTarget Book book);
}
