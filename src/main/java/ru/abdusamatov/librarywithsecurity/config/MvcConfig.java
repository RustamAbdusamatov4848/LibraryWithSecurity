package ru.abdusamatov.librarywithsecurity.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Date;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @Bean
    public ApplicationRunner tacoDataLoader(BookRepository bookRepository, UserRepository userRepository,
                                            PasswordEncoder passwordEncoder, LibrarianRepository librarianRepository){
        return args -> {
            Librarian librarian = new Librarian();
            librarian.setId(1L);
            librarian.setFullName("Rustam");
            librarian.setEmail("rustam@yandex.ru");
            librarian.setPassword(passwordEncoder.encode("1234"));
            librarian.setDateOfCreated(LocalDateTime.now());
            librarianRepository.save(librarian);

            User fedor = new User();
            fedor.setId(1L);
            fedor.setFullName("Fedor");
            fedor.setEmail("fedor@yandex.ru");
            fedor.setYearOfBirth(new Date(94,1,15));
            fedor.setDateOfCreated(LocalDateTime.now());
            userRepository.save(fedor);

            Book book  = new Book();
            book.setBookId(1L);
            book.setTitle("Хижина Дяди Тома");
            book.setAuthorName("Гарриет");
            book.setAuthorSurname("Стоу");
            book.setYear(1882);
            book.setOwner(fedor);
            book.setExpired(false);
            book.setTakenAt(new Date());
            bookRepository.save(book);
        };
    }
}
