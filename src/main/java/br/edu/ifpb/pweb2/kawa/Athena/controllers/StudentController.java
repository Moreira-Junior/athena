package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.Authority;
import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.InstitutionRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
@PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
public class StudentController {

    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("students/form");
        modelAndView.addObject("student", new Student());
        return modelAndView;
    }

    @ModelAttribute("institutionsItems")
    public List<Institution> getInstitutions(){
        return this.institutionRepository.findAll();
    }

    @PostMapping
    public ModelAndView save(Student student, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        if(student.getId() == null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            student.getUser().setPassword(encoder.encode(student.getUser().getPassword()));
            Authority authority = new Authority();
            Authority.AuthorityId authorityId = new Authority.AuthorityId(student.getUser().getUsername(), "ROLE_ALUNO");
            authority.setId(authorityId);
            student.getUser().setAuthorities(Arrays.asList(authority));
        }
        this.studentRepository.save(student);
        modelAndView.setViewName("redirect:students/list");
        redirectAttributes.addFlashAttribute("message", "Estudante cadastrado com sucesso!");
        return modelAndView;
    }

    @GetMapping("list")
    public ModelAndView listAll(ModelAndView modelAndView){
        modelAndView.addObject("students", this.studentRepository.findAll());
        modelAndView.setViewName("students/list");
        return modelAndView;
    }

    @GetMapping("list/withoutEnrollment")
    public ModelAndView listAllWithoutEnrollment(ModelAndView modelAndView){
        modelAndView.addObject("students", this.studentRepository.findByEnrollmentStatusesIsNull());
        modelAndView.setViewName("students/list");
        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editForm(@PathVariable("id") Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            modelAndView.addObject("student", student);
            modelAndView.setViewName("students/form");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/students/list");
            redirectAttributes.addFlashAttribute("message", "Student not found!");
            return modelAndView;
        }
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id")Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        this.studentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Estudante deletado com sucesso!");
        modelAndView.setViewName("redirect:/students/list");
        return modelAndView;
    }
}
