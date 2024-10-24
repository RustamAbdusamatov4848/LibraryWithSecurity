package ru.abdusamatov.librarywithsecurity.util.mappers;

import org.mapstruct.Mapper;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.models.User;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface UserMapper {

    UserDto userToDto(User user);

    User dtoToUser(UserDto userDto);
}
