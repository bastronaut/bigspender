package com.bastronaut.bigspender;

import com.bastronaut.bigspender.methodargumentresolvers.TransactionDeleteDTOHandlerMethodArgumentResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties
public class BigspenderApplication  {

//	@Override
//	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//		argumentResolvers.add(new TransactionDeleteDTOHandlerMethodArgumentResolver());
//	}

	public static void main(String[] args) {
		SpringApplication.run(BigspenderApplication.class, args);
	}

}
