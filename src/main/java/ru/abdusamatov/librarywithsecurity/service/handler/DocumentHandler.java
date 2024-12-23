package ru.abdusamatov.librarywithsecurity.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.service.TopPdfConverterClientService;

@Service
@RequiredArgsConstructor
public class DocumentHandler {
    private final TopPdfConverterClientService clientService;

}
