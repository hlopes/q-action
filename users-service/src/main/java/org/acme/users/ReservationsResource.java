package org.acme.users;

import java.time.LocalDate;
import java.util.List;

import org.acme.users.model.Car;
import org.acme.users.model.Reservation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/")
@Blocking
public class ReservationsResource {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index(String name, LocalDate startDate, LocalDate endDate);

        public static native TemplateInstance listofreservations(List<Reservation> reservations);

        public static native TemplateInstance availablecars(List<Car> cars, LocalDate startDate, LocalDate endDate);
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
        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1L);
        }

        if (endDate == null) {
            endDate = LocalDate.now().plusDays(7L);
        }

        return Templates.index(securityContext.getUserPrincipal().getName(), startDate, endDate);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/get")
    public TemplateInstance getReservations() {
        log.info("### reservations {}", reservationsClient.getAllReservations());

        return Templates.listofreservations(reservationsClient.getAllReservations());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/available")
    public TemplateInstance getAvailableCars(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
        return Templates.availablecars(reservationsClient.availability(startDate, endDate), startDate, endDate);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/reserve")
    public RestResponse<TemplateInstance> create(
            @RestForm LocalDate startDate,
            @RestForm LocalDate endDate,
            @RestForm Long carId) {
        var reservation = Reservation.builder()
                .startDay(startDate)
                .endDay(endDate)
                .carId(carId)
                .build();

        reservationsClient.make(reservation);

        return RestResponse.ResponseBuilder
                .ok(getReservations())
                .header("HX-Trigger-After-Swap", "update-available-cars-list")
                .build();

    }
}
