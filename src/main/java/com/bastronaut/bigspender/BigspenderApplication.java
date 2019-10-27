package com.bastronaut.bigspender;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
