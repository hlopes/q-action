package org.acme.users;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Optional;

@Path("/whoami")
public class WhoAmIResource {

  Template whoami;
  SecurityContext securityContext;

  public WhoAmIResource(Template whoami, SecurityContext securityContext) {
    this.whoami = whoami;
    this.securityContext = securityContext;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public TemplateInstance get() {
    Optional.ofNullable(securityContext.getUserPrincipal())
        .ifPresent(principal -> whoami.data("name", principal.getName()));

    return whoami.instance();
  }
}
