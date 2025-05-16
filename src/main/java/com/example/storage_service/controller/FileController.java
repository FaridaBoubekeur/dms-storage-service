package com.example.storage_service.controller;

import com.example.storage_service.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

  private final StorageService storage;

  public FileController(StorageService storage) {
    this.storage = storage;
  }

  @PostMapping("/upload/{documentId}")
  public ResponseEntity<?> uploadFile(@PathVariable("documentId") String documentId,
      @RequestParam("file") MultipartFile file) {
    try {
      String key = storage.uploadFile(documentId, file);
      return ResponseEntity.ok().body("Uploaded to: " + key);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
    }
  }

  @GetMapping("/download-url")
  public ResponseEntity<?> getDownloadUrl(@RequestParam("key") String key) {
    String url = storage.generatePresignedUrlByKey(key);
    return ResponseEntity.ok().body(url);
  }
}