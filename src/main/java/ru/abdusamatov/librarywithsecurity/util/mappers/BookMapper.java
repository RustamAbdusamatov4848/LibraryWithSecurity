package ru.abdusamatov.librarywithsecurity.util.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.models.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorSurname", target = "authorSurname")
    @Mapping(source = "yearOfPublication", target = "yearOfPublication")
    @Mapping(source = "takenAt", target = "takenAt")
    @Mapping(source = "owner.id", target = "userId")
    @Mapping(source = "expired", target = "expired")
    BookDto bookToBookDto(Book book);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorSurname", target = "authorSurname")
    @Mapping(source = "yearOfPublication", target = "yearOfPublication")
    @Mapping(source = "takenAt", target = "takenAt")
    @Mapping(source = "userId", target = "owner.id")
    @Mapping(source = "expired", target = "expired")
    Book bookDtoToBook(BookDto bookDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "title", target = "title")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorSurname", target = "authorSurname")
    @Mapping(source = "yearOfPublication", target = "yearOfPublication")
    @Mapping(source = "takenAt", target = "takenAt")
    @Mapping(source = "userId", target = "owner.id")
    @Mapping(source = "expired", target = "expired")
    Book updateBookFromDto(BookDto bookDto, @MappingTarget Book book);
}
