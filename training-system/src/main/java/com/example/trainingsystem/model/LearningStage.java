package com.example.trainingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum LearningStage {
    STAGE1(1, 0),
    STAGE2(3, 20),
    STAGE3(7, 40),
    STAGE4(30, 60),
    STAGE5(90, 80),
    STAGE6(0, 100);

    private int daysTillNextTrain;
    private int learntPercent;

}
