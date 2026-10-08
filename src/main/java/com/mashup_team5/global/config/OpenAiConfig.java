package com.mashup_team5.global.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAiConfig {

	@Bean
	public RestClient openAiRestClient(@Value("${openai.timeout:30s}") Duration timeout) {
		if (timeout.isZero() || timeout.isNegative()) {
			throw new IllegalArgumentException("OpenAI 요청 제한 시간은 0보다 커야 합니다.");
		}
		HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(timeout);
		return RestClient.builder()
			.baseUrl("https://api.openai.com/v1")
			.requestFactory(requestFactory)
			.build();
	}
}
