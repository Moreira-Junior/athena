package br.edu.ifpb.pweb2.kawa.Athena.services;

import br.edu.ifpb.pweb2.kawa.Athena.models.Document;
import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.DocumentRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.EnrollmentStatusRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private EnrollmentStatusRepository enrollmentStatusRepository;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save() throws IOException {
        documentService.save(new EnrollmentStatus(), "fileName", new byte[0]);
        verify(documentRepository, times(1)).save(Mockito.any(Document.class));
    }

    @Test
    void findById() {
        documentService.findById(1L);
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void findByEnrollmentStatus() {
        documentService.findByEnrollmentStatus(1L);
        verify(documentRepository, times(1)).findByEnrollmentStatus(1L);
    }

    @Test
    public void testDeleteById() {
        documentService.deleteById(1L);
        verify(documentRepository, times(1)).deleteById(1L);
    }
}