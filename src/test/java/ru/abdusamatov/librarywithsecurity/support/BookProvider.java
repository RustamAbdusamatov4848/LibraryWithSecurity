package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.models.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookProvider {
    private static final Random RANDOM = new Random();

    public static List<Book> createListBookDto(int size) {
        List<Book> list = new ArrayList<>();
        int count = 0;
        while (count < size) {
            list.add(Book.builder()
                    .title("Book " + count)
                    .authorName("Author name " + count)
                    .authorSurname("Author surname " + count)
                    .yearOfPublication(RANDOM.nextInt(1500, 2024))
                    .build());
            count++;
        }
        return list;
    }

}
