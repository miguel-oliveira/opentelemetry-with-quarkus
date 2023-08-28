package miguel.quarkus.opentelemetry;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface RemoteService {

  @POST
  @Path("/trace/end")
  @Produces(MediaType.TEXT_PLAIN)
  String end(final String requestBody);

}
