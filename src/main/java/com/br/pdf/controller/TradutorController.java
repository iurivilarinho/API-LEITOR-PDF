package com.br.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.pdf.service.TranslationService;

@RestController
@RequestMapping("/translator")
public class TradutorController {

	@Autowired
	private TranslationService translationService;

	@GetMapping
	public ResponseEntity<String> ler(@RequestParam String text, @RequestParam String targetLang) throws Exception {

		return ResponseEntity.ok(translationService.translate(text, targetLang));
	}

}
