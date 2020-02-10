package rs.ac.uns.ftn.upp.upp;

import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableScheduling
@SpringBootApplication
public class UppApplication {
	@Autowired
	private ProcessEngine processEngine;
	
	@PostConstruct
	public void initIt() throws Exception {
	  System.out.println("Adding demo data");
	  DemoDataGenerator demoDataGenerator = new DemoDataGenerator(processEngine);
	  demoDataGenerator.addUsers();
	}
	public static void main(String[] args) {
		SpringApplication.run(UppApplication.class, args);
	}
	
	
	

}
