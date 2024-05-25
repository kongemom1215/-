package com.unity.potato.service.openai;

import com.google.gson.Gson;
import com.unity.potato.dto.response.Result;
import com.unity.potato.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    @Value("${openai.secret.key}")
    private String OPEN_AI_KEY;
    @Autowired
    private RedisUtil redisUtil;
    private static final String API_ENDPOINT = "https://api.openai.com/v1/";

    public Result getGptAnswer(String query){
        HttpClient client = HttpClient.newHttpClient();

        Map<Object, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", query);
        requestBody.put("model", "gpt-3.5-turbo-instruct");
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        String requestBodyJson = new Gson().toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/completions"))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + OPEN_AI_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Result("0000", "요청 정상 처리", response.body());
        } catch (Exception e) {
            return new Result("9998" ,"OpenAI API 호출 할당량이 초과되었습니다.");
        }
    }
}
