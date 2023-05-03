package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/layout")
public class HomeController {
    @GetMapping("/home")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("layout/home");
        return modelAndView;
    }
}
