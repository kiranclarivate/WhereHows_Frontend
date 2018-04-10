package com.clarivate.cc.wherehows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import java.util.Arrays;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

        System.out.println("READY ...");

/*
        RestClient client = new RestClient("");
        try {
            client.getMyNotebooks("richardx");

        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addNewParagraph("2DA5VZG5P",  "%hive", "test_my11_newname166", "hive");
*/

    }
}
