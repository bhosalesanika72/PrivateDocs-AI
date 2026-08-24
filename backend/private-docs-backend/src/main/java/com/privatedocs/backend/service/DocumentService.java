package com.privatedocs.backend.service;

import com.privatedocs.backend.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    Document uploadDocument(MultipartFile file);

    List<Document> getAllDocuments();

    Document getDocumentById(Long id);

    void deleteDocument(Long id);
}