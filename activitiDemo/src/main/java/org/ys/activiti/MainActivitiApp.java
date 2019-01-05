package org.ys.activiti;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MainActivitiApp {
    public static void main( String[] args ) {
        SpringApplication.run(MainActivitiApp.class,args);
    }
}
