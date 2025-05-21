package com.example.trainingsystem.repository;

import com.example.trainingsystem.model.Dictionary;
import com.example.trainingsystem.model.Schedule;
import com.example.trainingsystem.model.Word;
import com.example.trainingsystem.model.WordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления расписаниями повторений слов {@link Schedule}.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByDictionary(Dictionary dictionary);

    /**
     * Ищет слова, подлежащие повторной тренировке, из заданного словаря.
     * <p>
     * Возвращает все слова, у которых:
     * <ul>
     *   <li>статус не равен {@code newStatus} (NEW)</li>
     *   <li>дата следующей тренировки уже наступила</li>
     * </ul>
     * Сортировка по дате следующей тренировки.
     * </p>
     *
     * @param dict словарь пользователя
     * @param newStatus статус, который нужно исключить
     * @param currentDate текущая дата
     * @return список расписаний слов, подлежащих повтору
     */
    @Query(value = "select s from schedule s where s.dictionary = ?1 and " +
            "s.status <> ?2 and s.nextTrainDate < ?3 order by s.nextTrainDate") //and s.nextTrainDate < ?3
    List<Schedule> findWordsForRepeat(Dictionary dict, WordStatus newStatus, LocalDate currentDate);

    @Query(value = "select s from schedule s where s.dictionary = ?1 and " +
            "s.status = ?2 order by s.nextTrainDate")
    List<Schedule> findWordsForNewTraining(Dictionary dict, WordStatus newStatus);

    Optional<Schedule> findByWord(Word word);

}
