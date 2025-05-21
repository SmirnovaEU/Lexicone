package com.example.emailnotification.services;

import com.example.emailnotification.message_mappers.RepeatWordsLetterToMessage;
import com.example.lettermodels.RepeatWordsLetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RabbitMqListener {
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;
    private final RepeatWordsLetterToMessage messageMapper;

    @RabbitListener(queues = "repeat-notifications-queue")
    public void processNotifications(String message) throws JsonProcessingException {
        RepeatWordsLetter letter = objectMapper.readValue(message, RepeatWordsLetter.class);
        SimpleMailMessage mailMessage = messageMapper.fromRepeatWordsLetterToMessage(letter);

        log.info("Текст письма " + mailMessage.getText());
     //   mailSender.send(mailMessage);

    }
}
