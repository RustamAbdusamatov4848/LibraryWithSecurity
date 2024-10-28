package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;

@Service
@RequiredArgsConstructor
public class LibrarianDetailsService implements UserDetailsService {
    private final LibrarianRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Librarian librarian = repository.findByEmail(email);
        if (librarian == null) {
            throw new UsernameNotFoundException("Librarian wasn't found with email: " + email);
        }
        return librarian;
    }
}
