package com.example.trainingsystem.dto;

import com.example.trainingsystem.model.Dictionary;
import lombok.*;

@Data
public class NewWordDto {

    private String name;
    private String translation;
    private String context;
    private String example;
    private Dictionary dictionary;

    public NewWordDto() {
    }

}
