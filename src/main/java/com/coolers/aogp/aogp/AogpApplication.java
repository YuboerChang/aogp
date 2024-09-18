package com.coolers.aogp.aogp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan("com.coolers.aogp.aogp.dao")
public class AogpApplication {

	public static void main(String[] args) {
		SpringApplication.run(AogpApplication.class, args);
	}

}
