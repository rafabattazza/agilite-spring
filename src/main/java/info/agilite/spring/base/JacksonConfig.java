package info.agilite.spring.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import info.agilite.utils.jackson.JSonMapperCreator;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper createObjectMapper() {
		return JSonMapperCreator.create();
	}
}