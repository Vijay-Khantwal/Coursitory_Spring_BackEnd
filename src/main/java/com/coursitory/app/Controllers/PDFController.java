package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.PDF;
import com.coursitory.app.Services.PDFService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
public class PDFController {

    @Autowired
    PDFService pdfService;

    @PostMapping("/upload/pdf")
    public ResponseEntity<String> uploadPDF(@RequestParam("file") MultipartFile file){
        try {
            String pdfId = pdfService.uploadPDF(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("fieldId :- "+pdfId);
        }
        catch (IOException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in uploading! " + e.getMessage());
        }

    }

    @GetMapping("/get/pdf/{id}")
    public ResponseEntity<?> getPDF(@PathVariable("id") String pdfId){
        Optional<PDF> pdf = pdfService.getPDFById(pdfId);
        if(pdf.isPresent()){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(pdf.get().getContent()));
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @DeleteMapping("delete/pdf/{id}")
    public  ResponseEntity<String> deletePDF(@PathVariable("id") String pdfId){
        if(pdfService.deletePDF(pdfId)){
            return ResponseEntity.ok().body("PDF successfully deleted");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PdfId invalid! ");
        }
    }
}
