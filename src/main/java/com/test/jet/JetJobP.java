package com.test.jet;

import com.hazelcast.jet.Traverser;
import com.hazelcast.jet.Traversers;
import com.hazelcast.jet.core.AbstractProcessor;
import com.hazelcast.spring.context.SpringAware;
import com.test.exception.TestException;
import com.test.exception.UnserializedClass;

import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Processor for searching data in repo in parallel.
 */
@SpringAware
public class JetJobP extends AbstractProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(JetJobP.class);

  private ArrayList<String> incomingTraces = new ArrayList<>();
  
  private Context context;
  private UnserializedClass traceRepository;

  public JetJobP() {
  }
  
  @Override
  protected void init(Context context) {
    this.context = context;
    
  }
  
  @Override
  @SuppressWarnings("unchecked")
  protected boolean tryProcess(int ordinal, Object item) {

    try {
      String trace = ((Map.Entry<String, String>) item).getValue();
      incomingTraces.add(trace);

    } catch (Exception ex) {
      LOG.warn("{}, reducer failed\n{}",ex.getMessage(), item, ex);
    } 

    return true;
  }

  private final Traverser<Map.Entry<String, String>> traverser = Traversers.lazy(() -> {
    if (incomingTraces.size() > 0) {
      throw new TestException();
    } else {
      return Traversers.empty();
    }
  });


  @Override
  public boolean complete() {
    return emitFromTraverser(traverser);
  }

  @Override
  public boolean isCooperative() {
    return false;
  }
}