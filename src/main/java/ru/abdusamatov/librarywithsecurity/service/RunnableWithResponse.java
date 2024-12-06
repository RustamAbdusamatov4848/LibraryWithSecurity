package ru.abdusamatov.librarywithsecurity.service;

import ru.abdusamatov.librarywithsecurity.dto.response.Response;

@FunctionalInterface
public interface RunnableWithResponse {
    Response<Void> execute();
}
