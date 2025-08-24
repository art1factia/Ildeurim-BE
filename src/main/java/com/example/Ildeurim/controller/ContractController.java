package com.example.Ildeurim.controller;

// ContractController.java
import com.example.Ildeurim.dto.job.ContractSummaryDto;
import com.example.Ildeurim.gpt.ContractPrompts;
import com.example.Ildeurim.service.LlmService;
import com.example.Ildeurim.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final OcrService ocr;
    private final LlmService llm;

    @PostMapping(value="/summarize", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ContractSummaryDto summarize(@RequestPart("file") MultipartFile pdf) throws Exception {
        String text = ocr.extractText(pdf.getBytes());
        return llm.completeJson(ContractPrompts.SYSTEM, ContractPrompts.userPrompt(text), ContractSummaryDto.class);
    }
}
