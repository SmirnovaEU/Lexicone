package com.example.lettermodels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "repeat_letter-dictionaries-graph",
        attributeNodes = {
                @NamedAttributeNode("repeatDictionaries")
        }
)
@Entity
@Table(name = "repeat_letter")
public class RepeatWordsLetter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "email")
    private String email;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "repeat_dictionary_id", nullable = false)
    private List<DictionaryForRepeat> repeatDictionaries;

    public RepeatWordsLetter(String email, List<DictionaryForRepeat> repeatDictionaries) {
        this.email = email;
        this.repeatDictionaries = repeatDictionaries;
    }
}
