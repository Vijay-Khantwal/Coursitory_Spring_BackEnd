package com.coursitory.app.Services;

import com.coursitory.app.Entities.PDF;
import com.coursitory.app.Repositories.PDFRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PDFService {

    @Autowired
    PDFRepository pdfRepository;

    @Value("${MAX_PDF_SIZE}")
    private String MAX_FILE_SIZE ;

    public String uploadPDF(MultipartFile file,String title) throws IOException {
        if(file.getSize() > Long.parseLong(MAX_FILE_SIZE)){
            throw new IOException("File size exceeds 15MB!!");
        }
        PDF pdf = new PDF();
        pdf.setFilename(title);
        pdf.setContent(file.getBytes());
        pdfRepository.save(pdf);

        return pdf.getId().toString();
    }

    public Optional<PDF> getPDFById(String id) {
        return pdfRepository.findById(new ObjectId(id));
    }

    public boolean deletePDF(String id) {
        Optional<PDF> pdf = getPDFById(id);
        if (pdf.isPresent()) { 
            pdfRepository.delete(pdf.get());
            return true;
        }
        return false;
    }

}
