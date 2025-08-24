package com.example.Ildeurim.service;

// OcrService.java
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import net.sourceforge.tess4j.*;

import org.springframework.stereotype.Service;

@Service
public class OcrService {
    private final boolean enableOcr = true;
    private final String tessdataPath = "/usr/share/tesseract-ocr/4.00/tessdata";

    public String extractText(byte[] pdfBytes) {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            String text = new PDFTextStripper().getText(doc);
            if (text != null && text.replaceAll("\\s+","").length() > 300) return text;

            if (!enableOcr) return text==null?"":text;
            ITesseract t = new Tesseract();
            t.setDatapath(tessdataPath);
            t.setLanguage("kor+eng");

            PDFRenderer renderer = new PDFRenderer(doc);
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<doc.getNumberOfPages(); i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, 300);
                File tmp = File.createTempFile("p", ".png");
                ImageIO.write(img, "png", tmp);
                sb.append(t.doOCR(tmp)).append("\n\n");
                tmp.delete();
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("PDF extract failed", e);
        }
    }
}
