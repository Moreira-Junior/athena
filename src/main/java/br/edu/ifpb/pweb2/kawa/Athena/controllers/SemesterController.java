package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import br.edu.ifpb.pweb2.kawa.Athena.models.Semester;
import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.InstitutionRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.SemesterRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/semesters")
public class SemesterController {

    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("semesters/form");
        modelAndView.addObject("semester", new Semester());
        return modelAndView;
    }

    @ModelAttribute("institutionsItems")
    public List<Institution> getInstitutions(){
        return this.institutionRepository.findAll();
    }

    @PostMapping
    public ModelAndView save(Semester semester, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        semester.getInstitution().setCurrentSemester(semester);
        this.semesterRepository.save(semester);
        modelAndView.setViewName("redirect:semesters/list");
        redirectAttributes.addFlashAttribute("message", "Semester saved!");
        return modelAndView;
    }

    @GetMapping("list")
    public ModelAndView listAll(ModelAndView modelAndView){
        modelAndView.addObject("semesters", this.semesterRepository.findAll());
        modelAndView.setViewName("semesters/list");
        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editForm(@PathVariable Long id, ModelAndView modelAndView){
        Optional<Semester> optionalSemester = semesterRepository.findById(id);
        if (optionalSemester.isPresent()) {
            modelAndView.addObject("semester", optionalSemester.get());
            modelAndView.setViewName("semesters/form");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/semesters/list");
            return modelAndView;
        }
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id")Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        this.semesterRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Semester removed!");
        modelAndView.setViewName("redirect:/semesters/list");
        return modelAndView;
    }
}
