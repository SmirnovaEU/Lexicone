package com.example.trainingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStats {
    private int allWordsNumber;
    private int trainedWordsNumber;
    private int rememberedWordsNumber;
    private int failedWordsNumber;
}
