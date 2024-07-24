package com.br.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.pdf.service.ImageToText;

@RestController
@RequestMapping("/ler")
public class LerImagemController {

	@Autowired
	private ImageToText imageToText;

	@PostMapping
	public ResponseEntity<?> ler(@RequestPart MultipartFile file) throws Exception {
		
		return ResponseEntity.ok(imageToText.ler(file));
	}

}
