package com.mashup_team5.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins(
					"http://localhost:5173",
					"https://mashup-modoodoc.vercel.app",
					"https://mashup-modoodoc-preview.vercel.app"
			)
			.allowedMethods("GET", "HEAD", "POST")
			.allowedHeaders("Content-Type")
			.allowCredentials(false);
	}
}
