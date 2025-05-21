package com.example.emailnotification.message_mappers;

import com.example.emailnotification.config.AppConfig;
import com.example.lettermodels.DictionaryForRepeat;
import com.example.lettermodels.RepeatWordsLetter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepeatWordsLetterToMessageImpl implements RepeatWordsLetterToMessage{
    private final AppConfig appConfig;

    @Override
    public SimpleMailMessage fromRepeatWordsLetterToMessage(RepeatWordsLetter letter) {
        String newLine ="\n";
        String tab = "\t";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(letter.getEmail());
        mailMessage.setFrom(appConfig.getServerEmail());
        mailMessage.setSubject("Пора повторить слова");
        StringBuilder messageText = new StringBuilder("Пора повторить слова в словарях:");
        messageText.append(newLine);
        for (DictionaryForRepeat dict: letter.getRepeatDictionaries()) {
            messageText.append(dict.getName()).append(tab).append(dict.getWordsCount()).append(newLine);
        }
        mailMessage.setText(messageText.toString());
        return mailMessage;
    }
}
