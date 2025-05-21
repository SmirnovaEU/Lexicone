package com.example.trainingsystem.repository;

import com.example.trainingsystem.dto.ExtendedResultDto;
import com.example.trainingsystem.model.Result;
import com.example.trainingsystem.model.Training;
import com.example.trainingsystem.model.Word;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с результатами тренировок ({@link Result}).
 * и предоставляет расширенную информацию для анализа результатов.
 */
@Repository
public interface ResultRepository extends CrudRepository<Result, Long> {

    List<Result> findAllByTraining(Training training);

    @Query(value = "select new com.example.trainingsystem.dto.ExtendedResultDto(" +
            "w.name, w.translation, r.success, s.status, s.nextTrainDate)" +
            " from Result r" +
            " join r.word w" +
            " join schedule s on s.word = w" +
            " where r.training.id = :trainingId" +
            " and w in :words order by s.nextTrainDate")
    List<ExtendedResultDto> findExtendedResultByWords(@Param("trainingId") Long trainingId,
                                                      @Param("words") List<Word> words);

    Optional<Result> findByTrainingAndWord(Training training, Word word);
}
