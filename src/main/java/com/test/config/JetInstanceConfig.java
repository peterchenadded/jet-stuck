package com.test.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.spring.context.SpringManagedContext;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JetInstanceConfig {
  @Bean
  public SpringManagedContext springManagedContext() {
    SpringManagedContext managedContext = new SpringManagedContext();
    return managedContext;
  }
  
  /**
   * Loads in JetInstance settings from application.properties and returns the instance
   *
   * @param hazelcastProperties Hazelcast properties from application.properties
   * @return
   */
  @Bean
  public JetInstance jetInstance(SpringManagedContext springManagedContext) {

    // Hack to load Hazelcast properties without writing them to system properties.
    Properties properties = new Properties();
    properties.putAll(System.getProperties());

    Config hazelcastConfig = new XmlConfigBuilder()
        .setProperties(properties).build();
    hazelcastConfig.setClassLoader(getClass().getClassLoader());
    hazelcastConfig = hazelcastConfig.setManagedContext(springManagedContext);
    JetConfig jetConfig = JetConfig.loadDefault(properties);
    jetConfig.setHazelcastConfig(hazelcastConfig);
    JetInstance instance = Jet.newJetInstance(jetConfig);

    return instance;
  }

  @Bean
  public HazelcastInstance hazelcastInstance(JetInstance jetInstance) {
    return jetInstance.getHazelcastInstance();
  }
}
