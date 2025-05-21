package com.example.trainingsystem.controller;

import com.example.trainingsystem.model.Dictionary;
import com.example.trainingsystem.model.Training;
import com.example.trainingsystem.model.Word;
import com.example.trainingsystem.repository.DictionaryRepository;
import com.example.trainingsystem.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class TrainingController {
    private final TrainService service;
    private final DictionaryRepository dictRepository;

    @Autowired
    public TrainingController(TrainService service, DictionaryRepository dictRepository) {
        this.service = service;
        this.dictRepository = dictRepository;
    }

    //формируется тренировка новых слов
    //открывается форма со сформированным списком слов
    @GetMapping("/trainings/new")
    public String getNewTraining(@RequestParam("dictId") long dictId, Model model) {
        Training training = service.newTraining(dictId);
        Dictionary dict = dictRepository.findById(dictId).orElseThrow();
        model.addAttribute("training", training);
        model.addAttribute("dictionary", dict);
        if (training.getWords().isEmpty())
            return "nowords";
        return "training";
    }

    //формируется тренировка слов на повтор
    //открывается форма со сформированным списком слов
    @GetMapping("/trainings/repeat")
    public String getRepeatTraining(@RequestParam("dictId") long dictId, Model model) {
        Training training = service.repeatTraining(dictId);
        Dictionary dict = dictRepository.findById(dictId).orElseThrow();
        model.addAttribute("training", training);
        model.addAttribute("dictionary", dict);
        if (training.getWords().isEmpty()) return "nowords";
        return "training";
    }

    //открывается форма слова
    @GetMapping("/trainings/{trainingId}/word")
    public String showNextWord(@PathVariable("trainingId") long id,
                               @RequestParam(name = "isPrevious", required = false, defaultValue = "false") boolean isPrevious,
                               Model model) {
        Training training = service.getTraining(id);//
        Optional<Word> nextWord = isPrevious ? service.getPreviousWord(true) : service.getNextWord(training);
        model.addAttribute("dictionary", training.getDictionary());
        model.addAttribute("training", training);
        if (nextWord.isEmpty()) {
            service.saveTrainResults(training);
            service.formSchedule(training);
            model.addAttribute("results", service.getExtendedResults(training));
            return "training-result";
        }
        model.addAttribute("stats", service.getStats());
        model.addAttribute("word", nextWord.get());
        model.addAttribute("previousWord", service.getPreviousWord(false).orElse(null));

        return "training-word";
    }

    //обрабатываем результат повторения слова
    @PostMapping("/trainings/{trainingId}/word/{wordId}/answer")
    public String answerWord(@PathVariable("trainingId") long id,
                             @PathVariable("wordId") long wordId,
                             @RequestParam("success") boolean success,
                             RedirectAttributes redirectAttributes) {

        service.saveWordResult(service.getWord(wordId), success);
        redirectAttributes.addAttribute("trainingId", id);
        return "redirect:/trainings/{trainingId}/word";
    }

    //открывается форма с результатами тренировки
    @GetMapping("/trainings/{trainingId}/result")
    public String showResults(@PathVariable("trainingId") long id, Model model) {
        Training training = service.getTraining(id);
        model.addAttribute("training", training);
        model.addAttribute("dictionary", training.getDictionary());
        model.addAttribute("results", service.getExtendedResults(training));
        return "training-result";
    }

}
