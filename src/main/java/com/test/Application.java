package com.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Main class to setup security and spring boot container.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {HazelcastAutoConfiguration.class})
@EnableWebSecurity
@ImportResource({"classpath:spring-security-config.xml"})
public class Application {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
