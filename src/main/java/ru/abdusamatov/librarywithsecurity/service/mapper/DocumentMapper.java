package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.model.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "bucketName", target = "bucketName")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "owner.id", target = "userId")
    DocumentDto documentToDto(Document user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "bucketName", target = "bucketName")
    @Mapping(source = "fileName", target = "fileName")
    Document dtoToDocument(DocumentDto userDto);
}
