# Overview
Springboot app to test jet issue when processor throws Exception with non-serializable object

# Steps to reproduce
* Import the maven project into eclipse/intellij
* Update hazelcast.xml with license key, or alternatively change the pom.xml to use only opensource version of jet
* Run the project and wait for it to fully start, below lines should be logged

```
2021-05-14 02:01:07,522 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 9100 (http) with context path ''
...
2021-05-14 02:01:07,832 [main] INFO  com.test.Application - Started Application in 13.027 seconds (JVM running for 13.804)
```
* Update application.properties to use port 9200
* Run the project again and wait for it to fully start, below lines should be logged

```
2021-05-14 02:01:31,941 [hz.blissful_khayyam.generic-operation.thread-0] INFO  c.h.internal.cluster.ClusterService - 

Members {size:2, ver:2} [
	Member [localhost]:5701 - f2f5f8f4-033c-446c-b08d-2fc1e94522ed
	Member [localhost]:5702 - 12c52c4e-5058-48ec-a919-fe83e1fd1661 this
]
...
2021-05-14 02:01:37,661 [main] INFO  com.test.Application - Started Application in 19.222 seconds (JVM running for 20.067)
```

* Go to http://localhost:9100/swagger-ui.html
* Click on the GET /v0.1/jet/run_job endpoint
* Click the try now button
* Enter in "test" in the value text input
* Click the execute button

## Expected results
* Should throw error that UnserializedClass is not serializable and stop the job

## Actual results
* Got below error in console, and job becomes stuck forever

```
2021-05-14 02:01:42,883 [hz.charming_mayer.cached.thread-3] INFO  c.h.jet.impl.JobExecutionService - Start execution of job 'com.test.jet.JetJob', execution 062d-dc72-0581-0001 from coordinator [localhost]:5701
2021-05-14 02:01:42,958 [hz.charming_mayer.jet.blocking.thread-0] WARN  c.h.j.i.e.TaskletExecutionService - Exception in ProcessorTasklet{com.test.jet.JetJob/search-repo#0}
com.test.exception.TestException: null
	at com.test.jet.JetJobP.lambda$0(JetJobP.java:54)
	at com.hazelcast.jet.Traversers$LazyTraverser.next(Traversers.java:193)
	at com.hazelcast.jet.core.AbstractProcessor.emitFromTraverser(AbstractProcessor.java:405)
	at com.hazelcast.jet.core.AbstractProcessor.emitFromTraverser(AbstractProcessor.java:421)
	at com.test.jet.JetJobP.complete(JetJobP.java:63)
	at com.hazelcast.jet.impl.execution.ProcessorTasklet.complete(ProcessorTasklet.java:465)
	at com.hazelcast.jet.impl.execution.ProcessorTasklet.stateMachineStep(ProcessorTasklet.java:365)
	at com.hazelcast.jet.impl.execution.ProcessorTasklet.call(ProcessorTasklet.java:240)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$BlockingWorker.run(TaskletExecutionService.java:293)
	at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
	at java.util.concurrent.FutureTask.run(Unknown Source)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.lang.Thread.run(Unknown Source)
```

* This is obviously not ideal as some external libraries e.g. elasticsearch have exceptions that contains objects that are not serializable
* Have also seem times where it did log that the object is unserializable as shown below.

```
2021-05-14 02:29:27,322 [hz.vigorous_knuth.cached.thread-4] ERROR c.h.jet.impl.MasterJobContext - Execution of job 'com.test.jet.JetJob', execution 062d-e2cb-3441-0001 failed
	Start time: 2021-05-14T02:29:26.714
	Duration: 586 ms
	For further details enable JobConfig.storeMetricsAfterJobCompletion
com.hazelcast.jet.JetException: com.hazelcast.nio.serialization.HazelcastSerializationException: Failed to serialize 'com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse'
	at com.hazelcast.internal.serialization.impl.SerializationUtil.handleSerializeException(SerializationUtil.java:115)
	at com.hazelcast.internal.serialization.impl.AbstractSerializationService.toBytes(AbstractSerializationService.java:173)
	at com.hazelcast.internal.serialization.impl.AbstractSerializationService.toBytes(AbstractSerializationService.java:149)
	at com.hazelcast.internal.serialization.impl.AbstractSerializationService.toBytes(AbstractSerializationService.java:140)
	at com.hazelcast.spi.impl.operationservice.impl.OutboundResponseHandler.send(OutboundResponseHandler.java:114)
	at com.hazelcast.spi.impl.operationservice.impl.OutboundResponseHandler.sendResponse(OutboundResponseHandler.java:94)
	at com.hazelcast.spi.impl.operationservice.Operation.sendResponse(Operation.java:470)
	at com.hazelcast.jet.impl.operation.AsyncOperation.doSendResponse(AsyncOperation.java:80)
	at com.hazelcast.jet.impl.operation.AsyncOperation.lambda$run$0(AsyncOperation.java:59)
	at com.hazelcast.jet.impl.util.ExceptionUtil.lambda$withTryCatch$0(ExceptionUtil.java:182)
	at java.util.concurrent.CompletableFuture.uniWhenComplete(Unknown Source)
	at java.util.concurrent.CompletableFuture$UniWhenComplete.tryFire(Unknown Source)
	at java.util.concurrent.CompletableFuture.postComplete(Unknown Source)
	at java.util.concurrent.CompletableFuture.completeExceptionally(Unknown Source)
	at com.hazelcast.jet.impl.util.NonCompletableFuture.internalCompleteExceptionally(NonCompletableFuture.java:59)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$ExecutionTracker.taskletDone(TaskletExecutionService.java:461)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$CooperativeWorker.dismissTasklet(TaskletExecutionService.java:392)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$CooperativeWorker.runTasklet(TaskletExecutionService.java:378)
	at java.util.concurrent.CopyOnWriteArrayList.forEach(Unknown Source)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$CooperativeWorker.run(TaskletExecutionService.java:346)
	at java.lang.Thread.run(Unknown Source)
Caused by: com.hazelcast.nio.serialization.HazelcastSerializationException: Failed to serialize 'com.hazelcast.jet.JetException'
	at com.hazelcast.internal.serialization.impl.SerializationUtil.handleSerializeException(SerializationUtil.java:115)
	at com.hazelcast.internal.serialization.impl.AbstractSerializationService.writeObject(AbstractSerializationService.java:269)
	at com.hazelcast.internal.serialization.impl.ByteArrayObjectDataOutput.writeObject(ByteArrayObjectDataOutput.java:378)
	at com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse.writeData(ErrorResponse.java:49)
	at com.hazelcast.internal.serialization.impl.DataSerializableSerializer.write(DataSerializableSerializer.java:244)
	at com.hazelcast.internal.serialization.impl.DataSerializableSerializer.write(DataSerializableSerializer.java:51)
	at com.hazelcast.internal.serialization.impl.StreamSerializerAdapter.write(StreamSerializerAdapter.java:39)
	at com.hazelcast.internal.serialization.impl.AbstractSerializationService.toBytes(AbstractSerializationService.java:170)
	... 19 more
Caused by: java.io.NotSerializableException: com.test.exception.UnserializedClass
	at java.io.ObjectOutputStream.writeObject0(Unknown Source)
	at java.io.ObjectOutputStream.defaultWriteFields(Unknown Source)
	at java.io.ObjectOutputStream.writeSerialData(Unknown Source)
	at java.io.ObjectOutputStream.writeOrdinaryObject(Unknown Source)
	at java.io.ObjectOutputStream.writeObject0(Unknown Source)
	at java.io.ObjectOutputStream.defaultWriteFields(Unknown Source)
	at java.io.ObjectOutputStream.defaultWriteObject(Unknown Source)
	at java.lang.Throwable.writeObject(Unknown Source)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
	at java.lang.reflect.Method.invoke(Unknown Source)
	at java.io.ObjectStreamClass.invokeWriteObject(Unknown Source)
	at java.io.ObjectOutputStream.writeSerialData(Unknown Source)
	at java.io.ObjectOutputStream.writeOrdinaryObject(Unknown Source)
	at java.io.ObjectOutputStream.writeObject0(Unknown Source)
	at java.io.ObjectOutputStream.writeObject(Unknown Source)
	at com.hazelcast.internal.serialization.impl.JavaDefaultSerializers$JavaSerializer.write(JavaDefaultSerializers.java:115)
	at com.hazelcast.internal.serialization.impl.JavaDefaultSerializers$JavaSerializer.write(JavaDefaultSerializers.java:108)
	at com.hazelcast.internal.serialization.impl.StreamSerializerAdapter.write(StreamSerializerAdapter.java:39)
	at com.hazelcast.internal.serialization.impl.AbstractSerializationService.writeObject(AbstractSerializationService.java:267)
	... 25 more

	at com.hazelcast.jet.impl.operation.AsyncOperation.doSendResponse(AsyncOperation.java:90)
	at com.hazelcast.jet.impl.operation.AsyncOperation.lambda$run$0(AsyncOperation.java:59)
	at com.hazelcast.jet.impl.util.ExceptionUtil.lambda$withTryCatch$0(ExceptionUtil.java:182)
	at java.util.concurrent.CompletableFuture.uniWhenComplete(Unknown Source)
	at java.util.concurrent.CompletableFuture$UniWhenComplete.tryFire(Unknown Source)
	at java.util.concurrent.CompletableFuture.postComplete(Unknown Source)
	at java.util.concurrent.CompletableFuture.completeExceptionally(Unknown Source)
	at com.hazelcast.jet.impl.util.NonCompletableFuture.internalCompleteExceptionally(NonCompletableFuture.java:59)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$ExecutionTracker.taskletDone(TaskletExecutionService.java:461)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$CooperativeWorker.dismissTasklet(TaskletExecutionService.java:392)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$CooperativeWorker.runTasklet(TaskletExecutionService.java:378)
	at java.util.concurrent.CopyOnWriteArrayList.forEach(Unknown Source)
	at com.hazelcast.jet.impl.execution.TaskletExecutionService$CooperativeWorker.run(TaskletExecutionService.java:346)
	at java.lang.Thread.run(Unknown Source)
```
