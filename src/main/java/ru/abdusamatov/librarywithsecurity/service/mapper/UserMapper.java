package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.model.User;


@Mapper(config = MapperConfiguration.class,
        uses = {DocumentMapper.class, BookMapper.class})
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "books", target = "books")
    @Mapping(source = "document", target = "documentDto")
    UserDto userToDto(User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "books", target = "books")
    @Mapping(source = "documentDto", target = "document")
    User dtoToUser(UserDto userDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "books", target = "books")
    @Mapping(source = "documentDto", target = "document")
    User updateUserFromDto(UserDto userDto, @MappingTarget User user);
}
