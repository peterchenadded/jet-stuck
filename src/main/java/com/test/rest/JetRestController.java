package com.test.rest;

import com.google.common.collect.ImmutableMap;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.map.IMap;
import com.test.jet.JetJob;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller exposes endpoints related to jet.
 */
@RestController
@RequestMapping(path = "/v0.1/jet")
@Component("jet-rest-controller")
public class JetRestController {
  private static final Logger LOG = LoggerFactory.getLogger(JetRestController.class);

  private JetInstance jetInstance;

  @Autowired
  public JetRestController(JetInstance jetInstance) {
    this.jetInstance = jetInstance;
  }

  @RequestMapping(method = RequestMethod.GET, path = "jobs")
  public Map<String, Object> jobs() {
    Map<String, Object> results = new HashMap<>();
    for (Job job: jetInstance.getJobs()) {
      results.put(job.getName(), job);
    }
    
    return results;
  }
  
  @RequestMapping(method = RequestMethod.GET, path = "run_job")
  public Map<String, Object> runJob() {
    
    String mapName = "test";
    IMap<String, Object> map = jetInstance.getHazelcastInstance().getMap(mapName);
    
    map.put("test", "test");
    
    try {
      JetJob jetJob = new JetJob(this.jetInstance);
      jetJob.run(mapName);
    } catch (Exception e) {
      LOG.error("error", e);
    }

    return ImmutableMap.of("status", "OK");
  }
}
