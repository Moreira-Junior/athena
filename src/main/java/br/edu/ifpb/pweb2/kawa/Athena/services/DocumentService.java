package br.edu.ifpb.pweb2.kawa.Athena.services;

import br.edu.ifpb.pweb2.kawa.Athena.models.Document;
import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.DocumentRepository;
import br.edu.ifpb.pweb2.kawa.Athena.repositories.EnrollmentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EnrollmentStatusRepository enrollmentStatusRepository;

    public Document save(EnrollmentStatus enrollmentStatus, String fileName, byte[] bytes) throws IOException {
        Document document = new Document(fileName, bytes);
        enrollmentStatus.setDocument(document);
        documentRepository.save(document);
        return document;
    }

    public Document findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    public Optional<Document> findByEnrollmentStatus(Long enrollmentStatusId) {
        return Optional.ofNullable(documentRepository.findByEnrollmentStatus(enrollmentStatusId));
    }

    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }
}
