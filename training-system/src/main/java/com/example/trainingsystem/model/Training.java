package com.example.trainingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(targetEntity = Dictionary.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "dict_id", nullable = false)
    private Dictionary dictionary;

    @Column(name = "repeat", nullable = false)
    private boolean isRepeat;

    @ManyToMany(targetEntity = Word.class, fetch = FetchType.LAZY)
    @JoinTable(name = "trainings_words", joinColumns = @JoinColumn(name = "training_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id"))
    private List<Word> words;

    @Column(name = "train_date", nullable = false)
    private LocalDate trainingDate;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results;

    public Training(Dictionary dictionary, boolean isRepeat, List<Word> words, LocalDate trainingDate) {
        this.dictionary = dictionary;
        this.isRepeat = isRepeat;
        this.words = words;
        this.trainingDate = trainingDate;
    }
}
