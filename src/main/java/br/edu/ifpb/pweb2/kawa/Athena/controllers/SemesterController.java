package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import br.edu.ifpb.pweb2.kawa.Athena.models.Semester;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.EnrollmentStatusRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.InstitutionRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.SemesterRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.StudentRepository;
import br.edu.ifpb.pweb2.kawa.Athena.ui.NavPage;
import br.edu.ifpb.pweb2.kawa.Athena.ui.NavePageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/semesters")
@PreAuthorize("hasRole('ADMIN')")
public class SemesterController {

    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private EnrollmentStatusRepository enrollmentStatusRepository;
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("semesters/form");
        String title = "Cadastro de período";
        modelAndView.addObject("title", title);
        modelAndView.addObject("semester", new Semester());
        return modelAndView;
    }

    @ModelAttribute("institutionsItems")
    public List<Institution> getInstitutions(){
        return this.institutionRepository.findAll();
    }

    @PostMapping
    public ModelAndView save(@Valid Semester semester, BindingResult validation, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        if(validation.hasErrors()) {
            modelAndView.setViewName("/semesters/form");
            return modelAndView;
        }
        semester.getInstitution().setCurrentSemester(semester);
        this.semesterRepository.save(semester);
        modelAndView.setViewName("redirect:semesters/list");
        redirectAttributes.addFlashAttribute("message", "Período cadastrado com sucesso!");
        return modelAndView;
    }

    @GetMapping("list")
    public ModelAndView listAll(ModelAndView modelAndView, @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size){
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Semester> pageSemesters = semesterRepository.findAll(paging);
        NavPage navPage = NavePageBuilder.newNavPage(pageSemesters.getNumber() + 1,
                pageSemesters.getTotalElements(), pageSemesters.getTotalPages(), size);
        modelAndView.addObject("semesters", pageSemesters);
        modelAndView.addObject("navPage", navPage);
        String title = "Lista de períodos";
        modelAndView.addObject("title", title);
        modelAndView.setViewName("semesters/list");
        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editForm(@PathVariable Long id, ModelAndView modelAndView){
        Optional<Semester> optionalSemester = semesterRepository.findById(id);
        if (optionalSemester.isPresent()) {
            modelAndView.addObject("semester", optionalSemester.get());
            modelAndView.setViewName("semesters/form");
            String title = "Edição de período";
            modelAndView.addObject("title", title);
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/semesters/list");
            return modelAndView;
        }
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id")Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        Optional<Semester> semester = this.semesterRepository.findById(id);
        if(semester.isPresent()){
            List<EnrollmentStatus> enrollments = this.enrollmentStatusRepository.findBySemesterId(id);
            enrollments.forEach( enrollmentStatus -> {
                enrollmentStatus.detachObjects();
                this.enrollmentStatusRepository.deleteById(enrollmentStatus.getId());
            });
            semester.get().getInstitution().setCurrentSemester(null);
            this.semesterRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Período deletado com sucesso!");
            modelAndView.setViewName("redirect:/semesters/list");
        } else {
            redirectAttributes.addFlashAttribute("warning", "Período não encontrado!");
            modelAndView.setViewName("redirect:/semesters/list");
        }
        return modelAndView;
    }
}
