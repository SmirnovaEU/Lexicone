package com.example.emailnotification.message_mappers;

import com.example.lettermodels.RepeatWordsLetter;
import org.springframework.mail.SimpleMailMessage;

public interface RepeatWordsLetterToMessage {
    SimpleMailMessage fromRepeatWordsLetterToMessage(RepeatWordsLetter letter);
}
