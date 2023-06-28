package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.Authority;
import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import br.edu.ifpb.pweb2.kawa.Athena.models.User;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.InstitutionRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.StudentRepository;
import br.edu.ifpb.pweb2.kawa.Athena.ui.NavPage;
import br.edu.ifpb.pweb2.kawa.Athena.ui.NavePageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("students/form");
        String title = "Cadastro de estudante";
        modelAndView.addObject("title", title);
        modelAndView.addObject("student", new Student());
        return modelAndView;
    }

    @ModelAttribute("institutionsItems")
    public List<Institution> getInstitutions(){
        return this.institutionRepository.findAll();
    }

    @PostMapping
    public ModelAndView save(@Valid Student student, BindingResult validation, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        if(validation.hasErrors()) {
            modelAndView.setViewName("/students/form");
            return modelAndView;
        }
        if (student.getId() == null && !studentRepository.findByUserUsername(student.getUser().getUsername()).isEmpty()) {
            modelAndView.setViewName("/students/form");
            modelAndView.addObject("warning", "Usuário já existe");
            return modelAndView;
        }
        if (student.getUser() != null) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validate(student.getUser());

            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> violation : violations) {
                    validation.rejectValue("user." + violation.getPropertyPath().toString(), null, violation.getMessage());
                }
                modelAndView.setViewName("/students/form");
                return modelAndView;
            }
        }
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
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
    public Object listAll(ModelAndView modelAndView, @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size){
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Student> pageStudents;
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.equals("[ROLE_ALUNO]")) {
            return "redirect:/home/";
        } else {
            pageStudents = studentRepository.findAll(paging);
        }
        NavPage navPage = NavePageBuilder.newNavPage(pageStudents.getNumber() + 1,
                pageStudents.getTotalElements(), pageStudents.getTotalPages(), size);
        modelAndView.addObject("students", pageStudents);
        modelAndView.addObject("navPage", navPage);
        String title = "Lista de estudantes";
        modelAndView.addObject("title", title);
        modelAndView.setViewName("students/list");
        return modelAndView;
    }

    @GetMapping("list/withoutEnrollment")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView listAllWithoutEnrollment(ModelAndView modelAndView, @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Student> studentPage = studentRepository.findByEnrollmentStatusesIsNull(pageable);
        NavPage navPage = NavePageBuilder.newNavPage(studentPage.getNumber() + 1,
                studentPage.getTotalElements(), studentPage.getTotalPages(), size);

        modelAndView.addObject("students", studentPage);
        modelAndView.addObject("navPage", navPage);
        String title = "Cadastro de estudantes sem declaração";
        modelAndView.addObject("title", title);
        modelAndView.setViewName("students/list");
        return modelAndView;
    }

    @GetMapping("profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
    public String profile(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Student> students = studentRepository.findByUserUsername(username);
        Long studentId = students.get(0).getId();
        return "redirect:/students/" + studentId + "/edit";
    }


    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
    public ModelAndView editForm(@PathVariable("id") Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            modelAndView.addObject("student", student);
            String title = "Edição de estudante";
            modelAndView.addObject("title", title);
            modelAndView.setViewName("students/form");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/students/list");
            redirectAttributes.addFlashAttribute("warning", "Estudante não encontrado!");
            return modelAndView;
        }
    }

    @RequestMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView deleteById(@PathVariable(value = "id")Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        try {
            this.studentRepository.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("warning", "Estudante não existe!");
            modelAndView.setViewName("redirect:/students/list");
            return modelAndView;
        }
        redirectAttributes.addFlashAttribute("message", "Estudante deletado com sucesso!");
        modelAndView.setViewName("redirect:/students/list");
        return modelAndView;
    }
}
