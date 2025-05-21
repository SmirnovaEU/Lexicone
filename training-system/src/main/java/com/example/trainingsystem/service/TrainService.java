package com.example.trainingsystem.service;

import com.example.trainingsystem.dto.ExtendedResultDto;
import com.example.trainingsystem.exception.NotFoundException;
import com.example.trainingsystem.model.*;
import com.example.trainingsystem.model.Dictionary;
import com.example.trainingsystem.repository.*;
import com.example.trainingsystem.security.SecurityUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления тренировками слов в пользовательских словарях.
 * Обеспечивает создание тренировок, отслеживание результатов
 * и составление расписаний тренировок на основе успехов пользователя.
 */
@Service
public class TrainService {
    private final ScheduleRepository scheduleRepository;
    private final ResultRepository resultRepository;
    private final DictionaryRepository dictRepository;
    private final WordRepository wordRepository;
    private final SettingsRepository setRepository;
    private final TrainingRepository trainingRepository;

    /** Текущая статистика тренировки,
     * обновляется после повторения каждого слова
     * -- GETTER --
     *  Возвращает текущую статистику тренировки.
     *
     * @return объект {@link TrainingStats}
     */
    @Getter
    private final TrainingStats stats;

    /** Список слов, которые уже были пройдены в текущей тренировке */
    private List<Word> trainedWords;

    /** Карта результатов: слово → успешно ли запомнено */
    private Map<Word, Boolean> wordResults;

    /** Текущий индекс слова в списке пройденных слов */
    private int currentWordIndex;

    @Autowired
    public TrainService(ScheduleRepository scheduleRepository, ResultRepository resultRepository, DictionaryRepository dictRepository, WordRepository wordRepository, SettingsRepository setRepository, TrainingRepository trainingRepository, List<Word> trainedWords) {
        this.scheduleRepository = scheduleRepository;
        this.resultRepository = resultRepository;
        this.dictRepository = dictRepository;
        this.wordRepository = wordRepository;
        this.setRepository = setRepository;
        this.trainingRepository = trainingRepository;
        this.stats = new TrainingStats();
    }

    /**
     * Создаёт тренировку с новыми словами из словаря.
     *
     * @param dictId идентификатор словаря
     * @return созданная тренировка
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Training newTraining(long dictId) {
        Dictionary dict = dictRepository.findById(dictId).orElseThrow(NotFoundException::new);
        User user = SecurityUtils.getCurrentUser();
        //максимальное количество слов в тренировке, берем из настроек пользователя
        int newWordsQuantity = setRepository.findFirstByUser(user).getNewWordsInTrain();
        //отбираем список новых слов
        List<Schedule> schedules = scheduleRepository.findWordsForNewTraining(dict, WordStatus.NEW);
        List<Word> wordList = schedules.stream().map(Schedule::getWord).limit(newWordsQuantity).collect(Collectors.toList());
        //создаем тренировку
        Training training = createTraining(dict, wordList, false);
        return training;
    }

    /**
     * Создаёт тренировку по словам, запланированным к повторению.
     *
     * @param dictId идентификатор словаря
     * @return повторная тренировка
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Training repeatTraining(long dictId) {
        Dictionary dict = dictRepository.findById(dictId).orElseThrow(NotFoundException::new);
        User user = SecurityUtils.getCurrentUser();
        int repeatWordsQuantity = setRepository.findFirstByUser(user).getRepeatWordsInTrain();
        List<Schedule> schedules = scheduleRepository.findWordsForRepeat(dict, WordStatus.NEW, LocalDate.now());
        List<Word> wordList = schedules.stream().map(Schedule::getWord).limit(repeatWordsQuantity).collect(Collectors.toList());
        Training training = createTraining(dict, wordList, true);
        return training;
    }

    /**
     * Сохраняет результаты тренировки (успешность по каждому слову).
     *
     * @param training объект текущей тренировки
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveTrainResults(Training training) {
        List<Result> results = wordResults.entrySet().stream()
                .map(entry -> new Result(training, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        resultRepository.saveAll(results);
    }


    private Training createTraining(Dictionary dict, List<Word> wordList, boolean isRepeat) {
        Training training = new Training(dict, isRepeat, wordList, LocalDate.now());
        Training saved = trainingRepository.save(training);
        setStats(wordList.size());
        currentWordIndex = 0;
        trainedWords = new ArrayList<>();
        wordResults = new HashMap<>();
        return saved;
    }

    /**
     * Возвращает следующее слово для показа пользователю.
     *
     * @param training текущая тренировка
     * @return следующее слово, если оно есть
     */
    public Optional<Word> getNextWord(Training training) {
        if (!trainedWords.isEmpty()){
            currentWordIndex++;
        }
        if (currentWordIndex < trainedWords.size()) {
            return Optional.ofNullable(trainedWords.get(currentWordIndex));
        }
        return training.getWords().stream()
                .filter(word -> !wordResults.containsKey(word))
                .findFirst();
    }

    /**
     * Сохраняет результат по конкретному слову (успешно или нет).
     *
     * @param word слово
     * @param success успешно ли пользователь его запомнил
     */
    public void saveWordResult(Word word, boolean success) {
        boolean isPresent = wordResults.containsKey(word);
        wordResults.put(word, success);
        refreshStats(word, isPresent);
    }

    /**
     * Проверяет, завершена ли текущая тренировка.
     *
     * @param training объект тренировки
     * @return true, если все слова пройдены
     */
    public boolean isTrainingCompleted(Training training) {
        return wordResults.size() == training.getWords().size();
    }

    /**
     * Обновляет статистику после прохождения слова.
     *
     * @param word слово, прошедшее тренировку
     * @param isPresent было ли оно уже в списке пройденных
     */
    public void refreshStats(Word word, boolean isPresent) {
        if (!isPresent) {
            trainedWords.add(word);
        }
        int trainedWordsNumber = wordResults.size();
        stats.setTrainedWordsNumber(trainedWordsNumber);
        int rememberedWords = (int) wordResults.values().stream().filter(res -> res).count();
        stats.setRememberedWordsNumber(rememberedWords);
        stats.setFailedWordsNumber(trainedWordsNumber - rememberedWords);
    }

    /**
     * Возвращает предыдущее слово в тренировке.
     *
     * @param isPrevious если true — перейти назад, если false — просто получить текущее
     * @return слово, если оно доступно
     */
    public Optional<Word> getPreviousWord(boolean isPrevious) {
        if (currentWordIndex < 1) return Optional.empty();
        if (isPrevious) {
            currentWordIndex--;
            return Optional.of(trainedWords.get(currentWordIndex));
        } else {
            return Optional.of(trainedWords.get(currentWordIndex-1));
        }
    }

    /**
     * Сбрасывает статистику и устанавливает общее количество слов.
     *
     * @param wordsNumber общее количество слов в тренировке
     */
    private void setStats(int wordsNumber) {
        stats.setAllWordsNumber(wordsNumber);
        stats.setTrainedWordsNumber(0);
        stats.setFailedWordsNumber(0);
        stats.setRememberedWordsNumber(0);
    }

    /**
     * Загружает тренировку по её идентификатору.
     *
     * @param id идентификатор тренировки
     * @return объект {@link Training}
     */
    public Training getTraining(long id) {
        return trainingRepository.findById(id).orElseThrow();
    }

    /**
     * Получает слово по идентификатору.
     *
     * @param id идентификатор слова
     * @return объект {@link Word}
     */
    public Word getWord(long id) {
        return wordRepository.findById(id).orElseThrow();
    }

    /**
     * Возвращает расширенные результаты по тренировке.
     *
     * @param training объект тренировки
     * @return список детализированных результатов
     */
    public List<ExtendedResultDto> getExtendedResults(Training training) {
        return resultRepository.findExtendedResultByWords(training.getId(), training.getWords());
    }

    /**
     * Обновляет расписание повторений слов по результатам тренировки.
     *
     * @param training объект тренировки
     */
    public void formSchedule(Training training) {
        List<Result> results = resultRepository.findAllByTraining(training);
        for (Result result : results) {
            Word word = result.getWord();
            Schedule schedule = scheduleRepository.findByWord(word).orElse(new Schedule(word));
            //обновить статус и стадию изучения слова
            updateWordStatus(schedule, result, training.isRepeat());
            //обновить даты и количество тренировок
            updateSchedule(schedule, training);
            scheduleRepository.save(schedule);
        }
    }

    /**
     * Обновляет статус слова в зависимости от результата.
     *
     * @param schedule объект расписания
     * @param result результат тренировки по слову
     * @param isRepeat является ли тренировка повторной
     */
    private void updateWordStatus(Schedule schedule, Result result, Boolean isRepeat) {
        if (isRepeat) {
            if (result.isSuccess()) {

                if (schedule.getStage().equals(LearningStage.STAGE6)) {
                    //если этап последний, то переводим в статус "выучено"
                    schedule.setStatus(WordStatus.LEARNT);
                    schedule.setLearntDate(LocalDate.now());
                } else {
                    //если этап не последний, переводим на следующий этап
                    int nextStageIndex = schedule.getStage().ordinal() + 2;
                    LearningStage nextStage = LearningStage.valueOf("STAGE" + nextStageIndex);
                    schedule.setStage(nextStage);
                }
            }
        } else {
            //если новая тренировка, меняем статус
            schedule.setStatus(WordStatus.IS_LEARNING);
        }
    }

    /**
     * Пересчитывает даты повторения слов.
     *
     * @param schedule объект расписания
     * @param training текущая тренировка
     * @return обновлённый объект {@link Schedule}
     */
    private Schedule updateSchedule(Schedule schedule, Training training) {
        schedule.setTotalTrainNumber(schedule.getTotalTrainNumber() + 1);
        schedule.setLastTrainDate(training.getTrainingDate());
        int daysTillNextTrain = schedule.getStage().getDaysTillNextTrain();
        schedule.setNextTrainDate(training.getTrainingDate().plusDays(daysTillNextTrain));
        return schedule;
    }
}
