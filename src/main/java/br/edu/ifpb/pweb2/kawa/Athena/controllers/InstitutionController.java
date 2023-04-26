package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import br.edu.ifpb.pweb2.kawa.Athena.models.Semester;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/institutions")
public class InstitutionController {

    @Autowired
    private InstitutionRepository institutionRepository;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("institutions/form");
        modelAndView.addObject("institution", new Institution());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView save(Institution institution, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        this.institutionRepository.save(institution);
        modelAndView.setViewName("redirect:institutions/list");
        redirectAttributes.addFlashAttribute("message", "Institution saved!");
        return modelAndView;
    }

    @GetMapping("list")
    public ModelAndView listAll(ModelAndView modelAndView){
        modelAndView.addObject("institutions", this.institutionRepository.findAll());
        modelAndView.setViewName("institutions/list");
        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editForm(@PathVariable Long id, ModelAndView modelAndView){
        Optional<Institution> optionalInstitution = institutionRepository.findById(id);
        if (optionalInstitution.isPresent()) {
            modelAndView.addObject("institution", optionalInstitution.get());
            modelAndView.setViewName("institutions/form");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/institutions/list");
            return modelAndView;
        }
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id")Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        Optional<Institution> institution = this.institutionRepository.findById(id);
        if(institution.isPresent()){
            institution.get().detachStudents();
            this.institutionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Institution removed!");
            modelAndView.setViewName("redirect:/institutions/list");
        }
        redirectAttributes.addFlashAttribute("message", "Institution not found!");
        modelAndView.setViewName("redirect:/institutions/list");
        return modelAndView;
    }
}
