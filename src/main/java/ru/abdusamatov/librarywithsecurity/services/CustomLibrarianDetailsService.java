package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;

@Service
@RequiredArgsConstructor
public class CustomLibrarianDetailsService implements UserDetailsService {
    private final LibrarianRepository repository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email);
    }
}
