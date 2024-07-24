package com.br.pdf.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.pdf.service.LerPdfService;

@RestController
@RequestMapping("/ler_pdf")
public class LerPdfController {

	@Autowired
	private LerPdfService pdfService;

	@PostMapping
	public String extractText(@RequestPart("file") MultipartFile file) {
		try {
			return pdfService.extractTextFromPDF(file);
		} catch (IOException e) {
			return "Error reading PDF: " + e.getMessage();
		}
	}
}
