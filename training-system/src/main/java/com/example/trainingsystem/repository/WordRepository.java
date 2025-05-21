package com.example.trainingsystem.repository;

import com.example.trainingsystem.model.Dictionary;
import com.example.trainingsystem.model.Word;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends CrudRepository<Word, Long> {
    List<Word> findAllByDictionary(Dictionary dictionary);

    List<Word> findAll();

}
