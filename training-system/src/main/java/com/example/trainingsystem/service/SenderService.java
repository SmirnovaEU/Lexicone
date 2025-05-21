package com.example.trainingsystem.service;


import com.example.lettermodels.DictionaryForRepeat;
import com.example.lettermodels.RepeatWordsLetter;
import com.example.trainingsystem.model.*;
import com.example.trainingsystem.repository.DictionaryRepository;
import com.example.trainingsystem.repository.ScheduleRepository;
import com.example.trainingsystem.repository.SettingsRepository;
import com.example.trainingsystem.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class SenderService {
    private static final String MAIN_EXCHANGE_NAME = "main-exchange";
    private static final String REPEAT_LETTER_KEY = "notification.repeat";
    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final DictionaryRepository dictionaryRepository;
    private final ScheduleRepository scheduleRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public SenderService(RabbitTemplate rabbitTemplate, UserRepository userRepository, SettingsRepository settingsRepository, DictionaryRepository dictionaryRepository, ScheduleRepository scheduleRepository, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
        this.dictionaryRepository = dictionaryRepository;
        this.scheduleRepository = scheduleRepository;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 0 12 * * ?")  //(initialDelay = 2000, fixedRate = 3000)
    public void sendLetters() throws JsonProcessingException {

        List<User> userList = userRepository.findAll();
        for (User user: userList) {
            Settings userSettings = settingsRepository.findFirstByUser(user);
            if (userSettings == null) continue;
            String email = userSettings.getEmail();
            if (email == null) continue;
            log.info("User email: " + email);

            List<Dictionary> dictionaries = dictionaryRepository.findByUser(user);
            if (dictionaries.isEmpty()) continue;
            List<DictionaryForRepeat> repeatList = new ArrayList<>();
            log.info("Dictionaries: " + dictionaries.toString());
            for (Dictionary dict: dictionaries) {
                List<Schedule> schedules = scheduleRepository.findWordsForRepeat(dict, WordStatus.NEW, LocalDate.now());
                if (schedules.isEmpty()) continue;
                DictionaryForRepeat repeatDict = new DictionaryForRepeat(dict.getName(), schedules.size());
                log.info("Words for repeat: " + repeatDict.toString());
                repeatList.add(repeatDict);
            }
            if (repeatList.isEmpty()) continue;
            RepeatWordsLetter letter = new RepeatWordsLetter(email, repeatList);
            rabbitTemplate.convertAndSend(MAIN_EXCHANGE_NAME, REPEAT_LETTER_KEY, objectMapper.writeValueAsString(letter));
        }

    }
}
