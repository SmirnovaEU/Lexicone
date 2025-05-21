package com.example.trainingsystem.controller;

import com.example.trainingsystem.dto.NewWordDto;
import com.example.trainingsystem.exception.NotFoundException;
import com.example.trainingsystem.model.Dictionary;
import com.example.trainingsystem.model.Schedule;
import com.example.trainingsystem.model.Word;
import com.example.trainingsystem.repository.DictionaryRepository;
import com.example.trainingsystem.repository.ScheduleRepository;
import com.example.trainingsystem.repository.WordRepository;
import com.example.trainingsystem.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class WordController {

    private final WordRepository repository;
    private final DictionaryRepository dictRepository;
    private final WordService service;
    private final ScheduleRepository scheduleRepository;


    @Autowired
    public WordController(WordRepository repository, DictionaryRepository dictRepository, WordService service, ScheduleRepository scheduleRepository) {
        this.repository = repository;
        this.dictRepository = dictRepository;
        this.service = service;
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/words/all")
    public String dictionaryPage(@RequestParam("dictId") long dictId, Model model) {
        Dictionary dict = dictRepository.findById(dictId).orElseThrow(NotFoundException::new);
        List<Word> dictWords = repository.findAllByDictionary(dict);
        model.addAttribute("dictionary", dict);
        model.addAttribute("words", dictWords);
        return "dict";
    }

    @GetMapping("words/edit")
    public String editPage(@RequestParam("id") long id, Model model) {
        Word word = repository.findById(id).orElseThrow(NotFoundException::new);
        model.addAttribute("word", word);
        return "edit-word";
    }

    @GetMapping("/words/addWord")
    public String editNewPage(@RequestParam("id") long dictId, Model model) {
        NewWordDto wordForm = new NewWordDto();
        Dictionary dictionary = dictRepository.findById(dictId).orElseThrow(NotFoundException::new);
        model.addAttribute("wordForm", wordForm);
        model.addAttribute("dictionary", dictionary);
        return "add-word";
    }

    @PostMapping("/words/edit")
    public String editWord(Word wordForm, Model model, RedirectAttributes redirectAttributes) {
        Word saved = service.editWord(wordForm);
        model.addAttribute(saved);
        redirectAttributes.addAttribute("dictId", saved.getDictionary().getId());
        return "redirect:/words/all";
    }

    @PostMapping("/words/addWord")
    public String createWord(@RequestParam("dictId") long dictId, NewWordDto wordForm, RedirectAttributes redirectAttributes) {
        service.createWord(wordForm, dictId);
        redirectAttributes.addAttribute("dictId", dictId);
        return "redirect:/words/all";
    }

    @GetMapping("/words/delete")
    public String deleteWord(@RequestParam Long id, @RequestParam long dictId, RedirectAttributes redirectAttributes) {
        repository.deleteById(id);
        redirectAttributes.addAttribute("dictId", dictId);
        return "redirect:/words/all";
    }

    @GetMapping("/words/schedule")
    public String schedulePage(@RequestParam("dictId") long dictId, Model model, RedirectAttributes redirectAttributes) {
        Dictionary dict = dictRepository.findById(dictId).orElseThrow(NotFoundException::new);
        List<Schedule> dictSchedule = scheduleRepository.findAllByDictionary(dict);
        model.addAttribute("dictionary", dict);
        model.addAttribute("schedules", dictSchedule);
        redirectAttributes.addAttribute("dictId", dictId);
        return "schedule";
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNFE(NotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

}
