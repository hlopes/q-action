package org.acme.users;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    var principal = Optional.ofNullable(securityContext.getUserPrincipal());
    var userId = principal.isPresent() ? principal.get().getName() : null;

    return whoami.data("name", userId);
  }
}
