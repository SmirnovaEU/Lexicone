package com.example.trainingsystem.controller;


import com.example.trainingsystem.model.Dictionary;
import com.example.trainingsystem.security.AuthenticationInfo;
import com.example.trainingsystem.security.SecurityUtils;
import com.example.trainingsystem.security.UserDetailSecurityService;
import com.example.trainingsystem.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DictController {

    private final DictService service;
    private final UserDetailSecurityService securityService;

    @Autowired
    public DictController(DictService service, UserDetailSecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
    }

    @GetMapping("/")
    public String indexPage(Model model, @AuthenticationPrincipal UserDetails user) {
        AuthenticationInfo info = SecurityUtils.getAuthenticationInfo();
        model.addAttribute("logInfo", info);
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/dicts/all")
    public String dictionaryListPage(Model model) {
        List<Dictionary> dicts = service.getAll();
        model.addAttribute("dicts", dicts);
        return "dict-list";
    }
}
