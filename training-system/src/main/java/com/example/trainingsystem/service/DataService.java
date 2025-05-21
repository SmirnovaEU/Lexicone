package com.example.trainingsystem.service;

import com.example.trainingsystem.model.Schedule;
import com.example.trainingsystem.model.Word;
import com.example.trainingsystem.repository.ScheduleRepository;
import com.example.trainingsystem.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервисный компонент для автоматизации внесения изменений в БД.
 */
@Service
public class DataService {
    private final WordRepository wordRepository;
    private final ScheduleRepository scheduleRepository;

    public DataService(WordRepository repository, ScheduleRepository scheduleRepository) {
        this.wordRepository = repository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Проходит по всем словам в базе и создаёт новое расписание
     * для каждого слова, у которого оно ещё не задано.
     *
     * <p>Метод обёрнут в новую транзакцию, чтобы обеспечить атомарность.</p>
     *
     * @throws RuntimeException если сохранение расписания завершится ошибкой
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void fillScheduleWithNewWords() {
        List<Word> wordsList = wordRepository.findAll();
        for (Word word : wordsList) {
            if (scheduleRepository.findByWord(word).isEmpty()) {
                Schedule schedule = new Schedule(word);
                scheduleRepository.save(schedule);
            }
        }

    }
}
