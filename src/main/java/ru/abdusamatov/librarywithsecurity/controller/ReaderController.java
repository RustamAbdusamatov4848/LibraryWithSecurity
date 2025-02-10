package ru.abdusamatov.librarywithsecurity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.service.handler.ReaderHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/readers")
@Tag(
        name = "Reader Management",
        description = "APIs for managing library readers"
)
public class ReaderController {
    private final ReaderHandler readerHandler;

    @Operation(summary = "Method for getting all registered readers")
    @GetMapping
    public Mono<Response<List<ReaderDto>>> getReaderList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size) {

        return readerHandler
                .getReaderList(page, size)
                .map(readers -> Response.buildResponse(
                        Result.success(OK, "List of readers"),
                        readers));
    }

    @Operation(summary = "Method for retrieving a reader by reader's ID")
    @GetMapping(value = "/{id}")
    public Mono<Response<ReaderDto>> getReaderById(@PathVariable("id") final Long id) {
        return readerHandler
                .getReaderById(id)
                .map(reader -> Response.buildResponse(
                        Result.success(OK, "Reader successfully found"),
                        reader));
    }

    @Operation(summary = "Method for retrieving a reader's document by reader's ID")
    @GetMapping(value = "/{id}/document")
    public Mono<Response<FileDto>> getReaderDocument(@PathVariable("id") final Long id) {
        return readerHandler
                .getDocument(id)
                .map(document -> Response.buildResponse(
                        Result.success(OK, "Reader document successfully found"),
                        document));
    }

    @Operation(summary = "Method for creating a new reader")
    @PostMapping
    public Mono<Response<ReaderDto>> createReader(@RequestPart("file") final MultipartFile file,
                                                  @RequestPart("readerDto") @Valid final ReaderDto readerDto) {
        return readerHandler
                .createReader(file, readerDto)
                .map(reader -> Response.buildResponse(
                        Result.success(CREATED, "Reader successfully saved"),
                        reader));
    }

    @Operation(summary = "Method for updating an existing reader")
    @PutMapping
    public Mono<Response<ReaderDto>> updateReader(@Valid @RequestBody final ReaderDto readerDto) {
        return readerHandler
                .updateReader(readerDto)
                .map(updatedReader -> Response.buildResponse(
                        Result.success(OK, "Reader successfully updated"),
                        updatedReader));
    }

    @Operation(summary = "Method for deleting a reader by reader's ID")
    @DeleteMapping(value = "/{id}")
    public Mono<Response<Void>> deleteReaderByID(@PathVariable("id") final Long id) {
        return readerHandler
                .deleteReaderById(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Successfully deleted"))));
    }
}
