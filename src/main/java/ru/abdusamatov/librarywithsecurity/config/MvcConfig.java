package ru.abdusamatov.librarywithsecurity.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
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
                                            PasswordEncoder passwordEncoder){
        return args -> {
            User user = new User();
            user.setId(1L);
            user.setFullName("Abdusamatov Rustam");
            user.setEmail("rustam@yandex.ru");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setYearOfBirth(new Date(94,1,15));
            user.setDateOfCreated(LocalDateTime.now());
            userRepository.save(user);

            Book book  = new Book();
            book.setBookId(1L);
            book.setTitle("Хижина Дяди Тома");
            book.setAuthorName("Гарриет");
            book.setAuthorSurname("Стоу");
            book.setYear(1882);
            book.setOwner(user);
            book.setExpired(false);
            book.setTakenAt(new Date());
            bookRepository.save(book);
        };
    }
}
