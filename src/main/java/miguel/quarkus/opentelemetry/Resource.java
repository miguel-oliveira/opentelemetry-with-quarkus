package miguel.quarkus.opentelemetry;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.logging.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@Path("/trace")
public class Resource {

  private static final Logger LOG = Logger.getLogger(Resource.class.getName());

  private final RemoteService remoteService;

  public Resource(@RestClient final RemoteService remoteService) {
    this.remoteService = remoteService;
  }

  @GET
  @Path("/start")
  @Produces(MediaType.TEXT_PLAIN)
  public String start() {
    LOG.info("Start");
    return remoteService.end();
  }

  @GET
  @Path("/end")
  @Produces(MediaType.TEXT_PLAIN)
  public String end() {
    final String end = "End";
    LOG.info(end);
    return end;
  }


}
