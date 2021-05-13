package com.test.jet;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.DAG;
import com.hazelcast.jet.core.Edge;
import com.hazelcast.jet.core.Vertex;
import com.hazelcast.jet.core.processor.SinkProcessors;
import com.hazelcast.jet.core.processor.SourceProcessors;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Creates a Jet DAG to query repo in parallel.
 */
public class JetJob implements Serializable {
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(JetJob.class);
  private static final long serialVersionUID = 6794295527010045944L;

  private transient JetInstance jetInstance;
  
  /**
   * Construct object.
   * @param jetInstance The jetInstance.
   */
  @Autowired
  public JetJob(JetInstance jetInstance) {
    this.jetInstance = jetInstance;
  }

  protected DAG buildReducerDag(String srcMapName, String destMapName) {
    DAG dag = new DAG();

    Vertex source = dag.newVertex("search-incoming", SourceProcessors.readMapP(srcMapName));
    Vertex sink = dag.newVertex("search-sink", SinkProcessors.writeMapP(destMapName));

    Vertex reducer = dag.newVertex("search-repo", () -> new JetJobP());

    dag.edge(Edge.between(source, reducer))
      .edge(Edge.between(reducer, sink));

    return dag;
  }

  private JobConfig getJobConfig() {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setName(this.getClass().getName());
    return jobConfig;
  }

  public String run(String mapName) throws Exception {
    String destMapName = mapName + "-dest";
    
    jetInstance.newJob(buildReducerDag(mapName, destMapName), getJobConfig()).join();

    return destMapName;
  }

}
