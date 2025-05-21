package com.example.trainingsystem.mapper;

import com.example.trainingsystem.dto.NewWordDto;
import com.example.trainingsystem.model.Word;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Маппер для преобразования объектов {@link NewWordDto} в сущности {@link Word}.
 * Используется библиотека MapStruct для автоматической генерации кода маппинга.
 */
@Mapper
public interface WordMapper {
    /**
     * Статическая точка доступа к экземпляру маппера.
     * Инициализируется с помощью MapStruct.
     */
    WordMapper MAPPER = Mappers.getMapper( WordMapper.class );

    /**
     * Преобразует DTO нового слова в сущность {@link Word},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO нового слова
     * @return объект {@link Word} без идентификатора
     */
    @Mapping(target = "id", ignore = true)
    Word dtoToWord(NewWordDto dto);

    /**
     * Обновляет существующий объект {@link Word} на основе данных из DTO.
     * Идентификатор слова не изменяется.
     *
     * @param dto DTO с новыми данными
     * @param word существующая сущность слова, которую нужно обновить
     */
    @Mapping(target = "id", ignore = true)
    void updateWordFromDto(NewWordDto dto, @MappingTarget Word word);
}
