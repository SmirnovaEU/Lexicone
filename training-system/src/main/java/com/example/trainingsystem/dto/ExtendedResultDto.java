package com.example.trainingsystem.dto;

import com.example.trainingsystem.model.WordStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExtendedResultDto {
    private String word;
    private String translation;
    private Boolean success;
    private WordStatus status;
    private LocalDate nextTrainDate;

    public ExtendedResultDto(String word, String translation, Boolean success, WordStatus status, LocalDate nextTrainDate) {
        this.word = word;
        this.translation = translation;
        this.success = success;
        this.status = status;
        this.nextTrainDate = nextTrainDate;
    }
}
