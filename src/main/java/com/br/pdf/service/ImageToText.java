package com.br.pdf.service;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.coyote.BadRequestException;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class ImageToText {

	public String ler(MultipartFile file) throws IOException, TesseractException {
		// Carregar a imagem

		Mat image = imdecode(new Mat(file.getBytes()), Imgcodecs.IMREAD_GRAYSCALE);

		// Verificar se a imagem foi carregada corretamente
		if (image.empty()) {
			System.out.println("Erro ao carregar a imagem");
			throw new BadRequestException("Erro ao carregar a imagem");
		}

		// Aplicar filtro gaussiano para suavizar
		Mat imageSmoothed = new Mat();
		opencv_imgproc.GaussianBlur(image, imageSmoothed, new Size(5, 5), 0);

		// Salvar a imagem suavizada para verificação (opcional)
		opencv_imgcodecs.imwrite("caminho/para/sua/imagem_suavizada.png", imageSmoothed);

		// Converter a Mat do JavaCV para BufferedImage para usar no Tesseract
		BufferedImage bufferedImage = matToBufferedImage(imageSmoothed);

		// Inicializar o Tesseract
		ITesseract tesseract = new Tesseract();
		tesseract.setDatapath("C:/Users/IuriSouza/Documents/");

		// Extrair texto usando OCR
		String extractedText = tesseract.doOCR(bufferedImage);
		System.out.println("Texto extraído: " + extractedText);
		return extractedText;

	}

	private static BufferedImage matToBufferedImage(Mat mat) {
		try (OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
				Java2DFrameConverter converterToBufferedImage = new Java2DFrameConverter()) {
			return converterToBufferedImage.convert(converterToMat.convert(mat));
		}
	}
}
