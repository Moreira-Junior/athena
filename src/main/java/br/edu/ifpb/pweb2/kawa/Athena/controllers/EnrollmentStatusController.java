package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.models.Semester;
import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.EnrollmentStatusRepository;
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
@RequestMapping("/enrollments")
public class EnrollmentStatusController {

    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EnrollmentStatusRepository enrollmentStatusRepository;

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
        modelAndView.setViewName("enrollments/form");
        modelAndView.addObject("enrollmentStatus", new EnrollmentStatus());
        return modelAndView;
    }

    @ModelAttribute("studentsItems")
    public List<Student> getInstitutions(){
        return this.studentRepository.findAll();
    }

    @ModelAttribute("semestersItems")
    public List<Semester> getSemesters(){
        return this.semesterRepository.findAll();
    }

    @PostMapping
    public ModelAndView save(EnrollmentStatus enrollmentStatus, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        enrollmentStatus.setSemester(enrollmentStatus.getStudent().getInstitution().getCurrentSemester());
        enrollmentStatus.getStudent().setCurrentEnrollmentStatus(enrollmentStatus);
        this.enrollmentStatusRepository.save(enrollmentStatus);
        modelAndView.setViewName("redirect:enrollments/list");
        redirectAttributes.addFlashAttribute("message", "Enrollment Status saved!");
        return modelAndView;
    }

    @GetMapping("list")
    public ModelAndView listAll(ModelAndView modelAndView){
        modelAndView.addObject("enrollmentStatusList", this.enrollmentStatusRepository.findAll());
        modelAndView.setViewName("enrollments/list");
        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editForm(@PathVariable Long id, ModelAndView modelAndView){
        Optional<EnrollmentStatus> enrollmentStatus = enrollmentStatusRepository.findById(id);
        if (enrollmentStatus.isPresent()) {
            modelAndView.addObject("enrollmentStatus", enrollmentStatus.get());
            modelAndView.setViewName("enrollments/form");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/enrollments/list");
            return modelAndView;
        }
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id")Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        Optional<EnrollmentStatus> enrollmentStatus = this.enrollmentStatusRepository.findById(id);
        if(enrollmentStatus.isPresent()){
            Student student = enrollmentStatus.get().getStudent();
            student.setCurrentEnrollmentStatus(null);
            this.enrollmentStatusRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Enrollment Status removed!");
            modelAndView.setViewName("redirect:/enrollments/list");
        } else{
            redirectAttributes.addFlashAttribute("message", "Enrollment Status not found!");
            modelAndView.setViewName("redirect:/enrollments/list");
        }
        return modelAndView;
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteByIdTest(Long id, ModelAndView modelAndView, RedirectAttributes redirectAttributes){
        Optional<EnrollmentStatus> enrollmentStatus = this.enrollmentStatusRepository.findById(id);
        if(enrollmentStatus.isPresent()){
            Student student = enrollmentStatus.get().getStudent();
            student.setCurrentEnrollmentStatus(null);
            this.studentRepository.save(student);
            this.enrollmentStatusRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Enrollment Status removed!");
            modelAndView.setViewName("redirect:/enrollments/list");
        }
        redirectAttributes.addFlashAttribute("message", "Enrollment Status not found!");
        modelAndView.setViewName("redirect:/enrollments/list");
        return modelAndView;
    }
}
