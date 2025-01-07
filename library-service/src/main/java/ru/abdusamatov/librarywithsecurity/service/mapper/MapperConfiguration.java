package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;

@MapperConfig(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD
)
public interface MapperConfiguration {
}
