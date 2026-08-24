package com.privatedocs.backend.controller;

import com.privatedocs.backend.entity.DocumentChunk;
import com.privatedocs.backend.repository.DocumentChunkRepository;
import com.privatedocs.backend.service.DocumentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentChunkRepository documentChunkRepository;

    public DocumentController(
            DocumentService documentService,
            DocumentChunkRepository documentChunkRepository) {

        this.documentService = documentService;
        this.documentChunkRepository = documentChunkRepository;
    }

    // ==========================================
    // UPLOAD DOCUMENT
    // ==========================================

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select a file");
            }

            return ResponseEntity.ok(
                    documentService.uploadDocument(file)
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Error uploading document: " + e.getMessage());
        }
    }


    // ==========================================
    // GET ALL DOCUMENTS
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getAllDocuments() {

        try {

            return ResponseEntity.ok(
                    documentService.getAllDocuments()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Error fetching documents: " + e.getMessage());
        }
    }


    // ==========================================
    // GET DOCUMENT BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentById(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    documentService.getDocumentById(id)
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Error fetching document: " + e.getMessage());
        }
    }


    // ==========================================
    // GET DOCUMENT CHUNKS
    // ==========================================

    @GetMapping("/{id}/chunks")
    public ResponseEntity<?> getDocumentChunks(
            @PathVariable Long id) {

        try {

            List<DocumentChunk> chunks =
                    documentChunkRepository.findByDocumentId(id);

            return ResponseEntity.ok(chunks);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Error fetching document chunks: "
                            + e.getMessage());
        }
    }


    // ==========================================
    // DELETE DOCUMENT
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable Long id) {

        try {

            documentService.deleteDocument(id);

            return ResponseEntity.ok(
                    "Document deleted successfully"
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Error deleting document: "
                            + e.getMessage());
        }
    }
}