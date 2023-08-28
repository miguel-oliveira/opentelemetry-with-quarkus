package miguel.quarkus.opentelemetry;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@Slf4j
@Path("/trace")
public class Resource {

  private final RemoteService remoteService;

  public Resource(@RestClient final RemoteService remoteService) {
    this.remoteService = remoteService;
  }

  @POST
  @Path("/start")
  @Produces(MediaType.TEXT_PLAIN)
  public String start(@RequestBody(required = true) final String requestBody) {
    log.info("Start: {}", requestBody);
    return remoteService.end(requestBody);
  }

  @POST
  @Path("/end")
  @Produces(MediaType.TEXT_PLAIN)
  public String end(@RequestBody(required = true) final String requestBody) {
    log.info("End: {}", requestBody);
    return requestBody;
  }


}
