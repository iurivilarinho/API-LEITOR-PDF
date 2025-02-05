package com.br.pdf.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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

	@Autowired
	private TranslationService translationService;

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

	public String extractTextFromPDF2(MultipartFile file) throws IOException, InterruptedException {
		StringBuilder fullText = new StringBuilder();
		String[] pagesText;

		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			PDFTextStripper pdfStripper = new PDFTextStripper();
			pagesText = new String[document.getNumberOfPages()];

			for (int page = 0; page < document.getNumberOfPages(); page++) {
				pdfStripper.setStartPage(page + 1);
				pdfStripper.setEndPage(page + 1);

				String extractedText = pdfStripper.getText(document);
				System.out.println("---------------------------------------------");
				System.err.println(extractedText);

				// Dividir o texto extraído em linhas e traduzir cada linha individualmente
				String[] lines = extractedText.split("\n");
				StringBuilder translatedText = new StringBuilder();
				for (String line : lines) {
					if (!line.trim().isEmpty()) {
						translatedText.append(translationService.translate(line, "pt")).append("\n");
					}
				}

				String translatedTextStr = translatedText.toString();
				System.out.println(translatedTextStr);
				System.out.println("---------------------------------------------");

				if (!translatedTextStr.trim().isEmpty()) {
					pagesText[page] = translatedTextStr;
					fullText.append(translatedTextStr).append("\n");
				} else {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
					try {
						String ocrText = translationService.translate(imageToText.ler(bim), "pt");
						pagesText[page] = ocrText;
						fullText.append(ocrText).append("\n");
					} catch (TesseractException e) {
						e.printStackTrace();
						throw new IOException("Erro ao realizar OCR na página " + page, e);
					}
				}
			}

			// Chama o método para criar o novo PDF com o texto extraído
			createNewPDFWithText(pagesText, "output.pdf");
		}

		return fullText.toString();
	}

	private void createNewPDFWithText(String[] textPages, String outputFileName) throws IOException {
		try (PDDocument newDocument = new PDDocument()) {
			for (String text : textPages) {
				if (text != null) { // Adiciona a verificação para evitar NPE
					PDPage currentPage = new PDPage();
					newDocument.addPage(currentPage);

					// Configurações de layout
					float leading = 15f;
					float margin = 50f;
					float startX = margin;
					float startY = 750;
					float endY = margin;
					float currentY = startY;

					PDFont font = PDType1Font.HELVETICA;
					float fontSize = 12f;
					float maxWidth = currentPage.getMediaBox().getWidth() - 2 * margin;

					// Remover caracteres de controle CR (\r)
					String cleanedText = text.replace("\r", "");
					String[] lines = cleanedText.split("\n");

					PDPageContentStream contentStream = new PDPageContentStream(newDocument, currentPage,
							AppendMode.OVERWRITE, true, true);
					contentStream.setFont(font, fontSize);

					for (String line : lines) {
						String[] words = line.split(" ");
						StringBuilder lineBuffer = new StringBuilder();

						for (String word : words) {
							String testLine = lineBuffer.toString() + word + " ";
							float width = font.getStringWidth(testLine) / 1000 * fontSize;

							if (width > maxWidth) {
								contentStream.beginText();
								contentStream.newLineAtOffset(startX, currentY);
								contentStream.showText(lineBuffer.toString().trim());
								contentStream.endText();
								currentY -= leading;
								lineBuffer.setLength(0);

								if (currentY <= endY) {
									contentStream.close();
									currentPage = new PDPage();
									newDocument.addPage(currentPage);
									contentStream = new PDPageContentStream(newDocument, currentPage,
											AppendMode.OVERWRITE, true, true);
									contentStream.setFont(font, fontSize);
									currentY = startY;
								}
							}
							lineBuffer.append(word).append(" ");
						}

						if (lineBuffer.length() > 0) {
							contentStream.beginText();
							contentStream.newLineAtOffset(startX, currentY);
							contentStream.showText(lineBuffer.toString().trim());
							contentStream.endText();
							currentY -= leading;
						}

						// Adiciona uma nova página se o espaço acabar
						if (currentY <= endY) {
							contentStream.close();
							currentPage = new PDPage();
							newDocument.addPage(currentPage);
							contentStream = new PDPageContentStream(newDocument, currentPage, AppendMode.OVERWRITE,
									true, true);
							contentStream.setFont(font, fontSize);
							currentY = startY;
						}
					}

					contentStream.close();
				}
			}

			newDocument.save(outputFileName);
		}
	}

}
