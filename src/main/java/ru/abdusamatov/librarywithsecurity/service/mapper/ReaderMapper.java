package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.model.Reader;


@Mapper(config = MapperConfiguration.class,
        uses = {DocumentMapper.class, BookMapper.class})
public interface ReaderMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "books", target = "books")
    @Mapping(source = "document", target = "documentDto")
    ReaderDto readerToDto(Reader reader);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "books", target = "books")
    @Mapping(source = "documentDto", target = "document")
    Reader dtoToReader(ReaderDto readerDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "books", target = "books")
    @Mapping(source = "documentDto", target = "document")
    Reader updateReaderFromDto(ReaderDto readerDto, @MappingTarget Reader reader);
}
