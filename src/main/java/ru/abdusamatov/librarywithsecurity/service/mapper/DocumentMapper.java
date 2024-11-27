package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.model.Document;
import ru.abdusamatov.librarywithsecurity.model.User;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "bucketName", target = "bucketName")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "userId", target = "userId")
    DocumentDto documentToDto(Document user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "bucketName", target = "bucketName")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "userId", target = "userId")
    User dtoToDocument(UserDto userDto);
}
