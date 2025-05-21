package com.example.trainingsystem.model;

import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Entity(name = "schedule")
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(targetEntity = Word.class, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "next_train_date", nullable = false)
    private LocalDate nextTrainDate;

    @Column(name = "last_train_date")
    private LocalDate lastTrainDate;

    @Column(name = "total_number")
    private int totalTrainNumber;

    @Column(name = "stage", nullable = false)
    private LearningStage stage;

    @Column(name = "status")
    private WordStatus status;

    @Column(name = "learnt_date")
    private LocalDate learntDate;

    @ManyToOne(targetEntity = Dictionary.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "dict_id", nullable = false)
    private Dictionary dictionary;


    public Schedule() {
        nextTrainDate = LocalDate.now();
        lastTrainDate = null;
        totalTrainNumber = 0;
        stage = LearningStage.STAGE1;
        status = WordStatus.NEW;
        learntDate = null;
    }

    public Schedule(Word word) {
        this();
        this.word = word;
        this.dictionary = word.getDictionary();
    }
}
