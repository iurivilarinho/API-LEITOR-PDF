//package com.br.pdf.service;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Service
//public class ChatGPTService {
//
//	@Value("${token.chatgpt}")
//	private String tokenChatGPT;
//
//	public void chatGPT(String text) throws Exception {
//		String url = "https://api.openai.com/v1/completions";
//		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//
//		con.setRequestMethod("POST");
//		con.setRequestProperty("Content-Type", "application/json");
//		con.setRequestProperty("Authorization", "Bearer " + tokenChatGPT);
//		System.out.println(tokenChatGPT);
//
//		ObjectMapper mapper = new ObjectMapper();
//		String jsonInputString = mapper.createObjectNode().put("model", "gpt-4o").put("prompt", text)
//				.put("max_tokens", 4000).put("temperature", 1.0).toString();
//
//		con.setDoOutput(true);
//		con.getOutputStream().write(jsonInputString.getBytes());
//
//		String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines().reduce((a, b) -> a + b)
//				.get();
//
//		JsonNode rootNode = mapper.readTree(output);
//		String responseText = rootNode.path("choices").get(0).path("text").asText();
//		System.out.println(responseText);
//	}
//}
////    public static void main(String[] args) throws Exception {
////        chatGPT("Hello, how are you?");
////    }
