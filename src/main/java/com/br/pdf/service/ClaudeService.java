package com.br.pdf.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ClaudeService {

	@Value("${token.claude}")
	private String tokenClaude;

	public String askClaude(String text) throws Exception {
		String url = "https://api.anthropic.com/v1/messages";
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("x-api-key", tokenClaude);
		con.setRequestProperty("anthropic-version", "2023-06-01"); // Verifique a versão mais recente

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("model", "claude-3-sonnet-20240229");
		ObjectNode messageNode = mapper.createObjectNode();
		messageNode.put("role", "user");
		messageNode.put("content", text);
		rootNode.putArray("messages").add(messageNode);

		String jsonInputString = rootNode.toString();

		con.setDoOutput(true);
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				JsonNode responseNode = mapper.readTree(response.toString());
				return responseNode.path("content").get(0).path("text").asText();
			}
		} else {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"))) {
				StringBuilder errorResponse = new StringBuilder();
				String errorLine;
				while ((errorLine = br.readLine()) != null) {
					errorResponse.append(errorLine.trim());
				}
				throw new RuntimeException(
						"HTTP error code : " + responseCode + " Error Body : " + errorResponse.toString());
			}
		}
	}
}