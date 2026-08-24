package com.privatedocs.backend.service;

import com.privatedocs.backend.model.Document;
import com.privatedocs.backend.repository.DocumentRepository;
import com.privatedocs.backend.util.DocumentTextExtractor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkService documentChunkService;

    private final String uploadDirectory = "uploads";

    // Constructor
    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            DocumentChunkService documentChunkService) {

        this.documentRepository = documentRepository;
        this.documentChunkService = documentChunkService;
    }

    @Override
    public Document uploadDocument(MultipartFile file) {

        try {

            // 1. Create uploads folder
            Path uploadPath = Paths.get(uploadDirectory);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Get original file name
            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.isEmpty()) {
                throw new RuntimeException("File name is empty");
            }

            // 3. Save physical file
            Path filePath = uploadPath.resolve(fileName);

            Files.write(filePath, file.getBytes());

            // 4. Extract text from file
            String extractedText =
                    DocumentTextExtractor.extractText(file);
            System.out.println("================================="); 
            System.out.println("EXTRACTED TEXT LENGTH: " +
            (extractedText == null ? "NULL" : extractedText.length()));

            System.out.println("EXTRACTED TEXT:");
            System.out.println(extractedText);
            System.out.println("=================================");

            // 5. Create Document object
            Document document = new Document();

            document.setFileName(fileName);
            document.setFilePath(filePath.toString());
            document.setFileType(file.getContentType());
            document.setExtractedText(extractedText);
            document.setUploadedAt(LocalDateTime.now());

            // 6. Save document in Supabase
            Document savedDocument =
                    documentRepository.save(document);

            // 7. Split extracted text into chunks
            documentChunkService.createChunks(
                    savedDocument.getId(),
                    extractedText
            );

            return savedDocument;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not upload document: " + e.getMessage()
            );
        }
    }

    @Override
    public List<Document> getAllDocuments() {

        return documentRepository.findAll();
    }

    @Override
    public Document getDocumentById(Long id) {

        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found with id: " + id
                        )
                );
    }

    @Override
    public void deleteDocument(Long id) {

        Document document = getDocumentById(id);

        // Delete physical file
        try {

            if (document.getFilePath() != null) {

                Path path =
                        Paths.get(document.getFilePath());

                Files.deleteIfExists(path);
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not delete physical file"
            );
        }

        // Delete database record
        documentRepository.delete(document);
    }
}