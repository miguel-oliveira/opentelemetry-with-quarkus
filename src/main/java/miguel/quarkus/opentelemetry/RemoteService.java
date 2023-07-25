package miguel.quarkus.opentelemetry;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface RemoteService {

  @GET
  @Path("/trace/end")
  @Produces(MediaType.TEXT_PLAIN)
  String end();

}
