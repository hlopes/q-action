package org.acme.users;

import java.time.LocalDate;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/")
@Blocking
public class ReservationsResource {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index(String name, LocalDate startDate, LocalDate endDate);
    }

    SecurityContext securityContext;
    ReservationsClient reservationsClient;

    public ReservationsResource(
            SecurityContext securityContext,
            @RestClient ReservationsClient reservationsClient) {
        super();

        this.securityContext = securityContext;
        this.reservationsClient = reservationsClient;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
        if(startDate == null) {
            startDate = LocalDate.now().plusDays(1L);
        }

        if(endDate == null) {
            startDate = LocalDate.now().plusDays(7L);
        }

        return Templates.index(securityContext.getUserPrincipal().getName(), startDate, endDate);
    }
}
