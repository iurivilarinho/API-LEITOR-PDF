package com.br.pdf.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

@Service
public class TranslationService {

	private final HttpClient httpClient;

	public TranslationService() {
		this.httpClient = HttpClient.newHttpClient();
	}

	public String translate(String text, String targetLang) throws IOException, InterruptedException {
		String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + targetLang + "&dt=t";
		String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.POST(HttpRequest.BodyPublishers.ofString("q=" + encodedText))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		HttpResponse<String> response = httpClient.send(request,
				HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

		return parseResponse(response.body());
	}

	private String parseResponse(String responseBody) {
		// A resposta é um array JSON, precisamos analisá-lo para obter o texto traduzido
		String[] parts = responseBody.split("\"");
		return parts[1]; // O texto traduzido é o segundo elemento da matriz
	}
}
