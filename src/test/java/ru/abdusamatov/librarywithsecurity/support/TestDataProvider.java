package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDataProvider {

    public static UserDto createSampleUserDto() {
        return UserDto.builder()
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    public static BookDto createSampleBookDto() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID(15))
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(2020)
                .build();
    }

    public static BookDto createSampleBookDtoWithOwner(Long userId) {
        BookDto book = createSampleBookDto();
        book.setUserId(userId);
        return book;
    }

    public static List<BookDto> createSampleListBookDto(int size) {
        List<BookDto> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(createSampleBookDto());
        }

        return list;
    }

    private static String getLimitUUID(int limit) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").substring(0, limit + 1);
    }
}
