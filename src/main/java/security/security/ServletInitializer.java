package security.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// setRegisterErrorPageFilter(false);; // <- this one
		return application.sources(ServletInitializer.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ServletInitializer.class, args);
    } 
}	
