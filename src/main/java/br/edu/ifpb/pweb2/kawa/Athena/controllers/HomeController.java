package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
@Controller
public class HomeController {

    @RequestMapping("/home")
    public String showHomePage(Model m) {
        m.addAttribute("menu", "home");
        return "home";
    }

}

