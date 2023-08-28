# opentelemetry-with-quarkus

Simple application integrated with [quarkus open telemetry extension](https://quarkus.io/guides/opentelemetry).

The application is automatically instrumented to send http request/response traces to an opentelemetry collector.

Additionally, all request/response headers and bodies are added to the traces through a [custom implementation](https://github.com/miguel-oliveira/opentelemetry-with-quarkus/blob/master/src/main/java/miguel/quarkus/opentelemetry/RequestResponseInterceptor.java).

## Usage

### Build and run 

Run following command in the project's root path (Docker must be installed):
````shell
docker-compose up
````

This will create and build the following containers:
* [Jaeger all in one container](https://www.jaegertracing.io/docs/1.48/getting-started/) which will collect and present all tracing information sent by the running application containers
* Two distinct application containers running at http://localhost:8080 and http://localhost:8081 with distinct names, App1 and App2 respectively

### Perform a request and check the generated traces in Jaeger UI

* Access App1 API through http://localhost:8080/swagger
* Invoke the /trace/start endpoint
* App1 will then invoke App2 /trace/end endpoint and return its response
* Check the recorded traces by accessing http://localhost:16686
* Traces may be searched by trace id, which is available in the "trace-id" response header

### Example traces
![img.png](trace_example.png)