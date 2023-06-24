package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.models.Institution;
import br.edu.ifpb.pweb2.kawa.Athena.models.Semester;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.EnrollmentStatusRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.InstitutionRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/institutions")
@PreAuthorize("hasRole('ADMIN')")
public class InstitutionController {

    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private EnrollmentStatusRepository enrollmentStatusRepository;

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
        redirectAttributes.addFlashAttribute("message", "Instituição cadastrada com sucesso!");
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
            List<EnrollmentStatus> enrollments = this.enrollmentStatusRepository.findByInstitution(id);
            enrollments.forEach(enrollmentStatus -> {
                enrollmentStatus.getStudent().setCurrentEnrollmentStatus(null);
                this.enrollmentStatusRepository.deleteById(enrollmentStatus.getId());
            });
            List<Semester> semesters = this.semesterRepository.findSemestersByInstitutionId(institution.get().getId());
            semesters.forEach(semester -> {
                semester.getInstitution().setCurrentSemester(null);
                this.semesterRepository.deleteById(semester.getId());
            });
            institution.get().detachStudents();
            this.institutionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Instituição deletada com sucesso!");
            modelAndView.setViewName("redirect:/institutions/list");
        }
        redirectAttributes.addFlashAttribute("message", "Instituição não encontrada!");
        modelAndView.setViewName("redirect:/institutions/list");
        return modelAndView;
    }
}
