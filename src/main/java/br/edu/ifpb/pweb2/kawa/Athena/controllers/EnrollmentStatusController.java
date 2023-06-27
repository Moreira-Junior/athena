package br.edu.ifpb.pweb2.kawa.Athena.controllers;

import br.edu.ifpb.pweb2.kawa.Athena.models.Document;
import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.models.Semester;
import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.EnrollmentStatusRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.SemesterRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.StudentRepository;
import br.edu.ifpb.pweb2.kawa.Athena.services.DocumentService;
import br.edu.ifpb.pweb2.kawa.Athena.ui.NavPage;
import br.edu.ifpb.pweb2.kawa.Athena.ui.NavePageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/enrollments")
@PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
public class EnrollmentStatusController {
    @Autowired
    private DocumentService documentService;

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
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.equals("[ROLE_ALUNO]")) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return this.studentRepository.findByUserUsername(username);
        }
        return this.studentRepository.findAll();
    }

    @ModelAttribute("semestersItems")
    public List<Semester> getSemesters(){
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.equals("[ROLE_ALUNO]")) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return this.semesterRepository.findSemestersByInstitutionStudentsUserUsername(username);
        }
        return this.semesterRepository.findAll();
    }

    @PostMapping
    public ModelAndView save(@Valid EnrollmentStatus enrollmentStatus, BindingResult validation,
                             @RequestParam("file") MultipartFile file,
                             ModelAndView modelAndView,
                             RedirectAttributes redirectAttributes
                             ){
        if(validation.hasErrors()) {
            modelAndView.setViewName("/enrollments/form");
            return modelAndView;
        }
        String msg = "Declaração emitida com sucesso!";
        String nextPage= "enrollments/list";
        enrollmentStatus.setSemester(enrollmentStatus.getStudent().getInstitution().getCurrentSemester());
        enrollmentStatus.getStudent().setCurrentEnrollmentStatus(enrollmentStatus);
        this.enrollmentStatusRepository.save(enrollmentStatus);

        if ( !file.isEmpty()) {
            try {
                String fileName = "enrollment-status-" + enrollmentStatus.getId() + ".pdf";
                Document document = documentService.save(enrollmentStatus, fileName, file.getBytes());
                document.setUrl(buildUrl(enrollmentStatus.getId(), document.getId()));
                this.enrollmentStatusRepository.save(enrollmentStatus);
            } catch (Exception e) {
                msg = "Erro ao salvar o arquivo: " + e.getMessage();
                nextPage = "enrollments/form";
            }
        }

        modelAndView.setViewName("redirect:"+nextPage);
        redirectAttributes.addFlashAttribute("message", msg);
        return modelAndView;
    }

    @GetMapping("list")
    public ModelAndView listAll(ModelAndView modelAndView, @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size){
        Pageable paging = PageRequest.of(page - 1, size);
        Page<EnrollmentStatus> pageEnrollments;
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        if(role.equals("[ROLE_ALUNO]")) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            pageEnrollments = enrollmentStatusRepository.findByStudentUserUsername(username, paging);
        } else {
            pageEnrollments = enrollmentStatusRepository.findAll(paging);
        }
        NavPage navPage = NavePageBuilder.newNavPage(pageEnrollments.getNumber() + 1,
                pageEnrollments.getTotalElements(), pageEnrollments.getTotalPages(), size);
        modelAndView.addObject("enrollmentStatusList", pageEnrollments);
        modelAndView.addObject("navPage", navPage);
        modelAndView.setViewName("enrollments/list");
        return modelAndView;
    }

    @GetMapping("list/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView listOverdue(ModelAndView modelAndView, @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "5") int size) {
        LocalDate currentDate = LocalDate.now();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<EnrollmentStatus> enrollmentStatusPage = enrollmentStatusRepository.findBySemesterEndsAtBefore(currentDate, pageable);
        NavPage navPage = NavePageBuilder.newNavPage(enrollmentStatusPage.getNumber() + 1,
                enrollmentStatusPage.getTotalElements(), enrollmentStatusPage.getTotalPages(), size);

        modelAndView.addObject("enrollmentStatusList", enrollmentStatusPage);
        modelAndView.addObject("navPage", navPage);
        modelAndView.setViewName("enrollments/list");
        return modelAndView;
    }

    @GetMapping("list/overdue/{nDaysAfter}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView listOverdue(ModelAndView modelAndView, @PathVariable(value = "nDaysAfter") Integer nDaysAfter,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "5") int size) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nDaysAfterDate = currentDate.plusDays(nDaysAfter);
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<EnrollmentStatus> enrollmentStatusPage = enrollmentStatusRepository.findBySemesterEndsAtBetween(currentDate, nDaysAfterDate, pageable);
        NavPage navPage = NavePageBuilder.newNavPage(enrollmentStatusPage.getNumber() + 1,
                enrollmentStatusPage.getTotalElements(), enrollmentStatusPage.getTotalPages(), size);

        modelAndView.addObject("enrollmentStatusList", enrollmentStatusPage);
        modelAndView.addObject("navPage", navPage);
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
            redirectAttributes.addFlashAttribute("message", "Declaração deletada com sucesso!");
            modelAndView.setViewName("redirect:/enrollments/list");
        } else{
            redirectAttributes.addFlashAttribute("warning", "Declaração não encontrada!");
            modelAndView.setViewName("redirect:/enrollments/list");
        }
        return modelAndView;
    }

    @RequestMapping("/{id}/documents/{idDoc}")
    public ResponseEntity<byte[]> getDocumento(@PathVariable("idDoc") Long idDoc) {
        Document document = documentService.findById(idDoc);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                .body(document.getFile());
    }


    private String buildUrl(Long enrollmentStatusId, Long documentId) {
        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/enrollments/")
                .path(String.valueOf(enrollmentStatusId))
                .path("/documents/")
                .path(String.valueOf(documentId))
                .toUriString();
        return fileDownloadUri;
    }
}
