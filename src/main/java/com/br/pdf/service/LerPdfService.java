package com.br.pdf.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.TesseractException;

@Service
public class LerPdfService {

	@Autowired
	private ImageToText imageToText;

	public String extractTextFromPDF(MultipartFile file) throws IOException {
		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				return tStripper.getText(document);
			} else {
				throw new IOException("The PDF document is encrypted and cannot be read.");
			}
		}
	}

	public String extractTextFromPDF2(MultipartFile file) throws IOException {
		StringBuilder fullText = new StringBuilder();

		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			PDFTextStripper pdfStripper = new PDFTextStripper();

			for (int page = 0; page < document.getNumberOfPages(); page++) {
				pdfStripper.setStartPage(page + 1);
				pdfStripper.setEndPage(page + 1);

				String extractedText = pdfStripper.getText(document);

				if (!extractedText.trim().isEmpty()) {
					fullText.append(extractedText).append("\n");
				} else {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
					try {
						String ocrText = imageToText.ler(bim); // método para realizar OCR na imagem
						fullText.append(ocrText).append("\n");
					} catch (TesseractException e) {
						e.printStackTrace();
						throw new IOException("Erro ao realizar OCR na página " + page, e);
					}
				}
			}
		}

		return fullText.toString();
	}

}
