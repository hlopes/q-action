package org.acme.users;

import java.time.LocalDate;
import java.util.List;

import org.acme.users.model.Car;
import org.acme.users.model.Reservation;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@RegisterRestClient(baseUri = "http://localhost:8081")
@AccessToken
@Path("reservation")
public interface ReservationsClient {
    @GET
    @Path("all")
    List<Reservation> getAllReservations();

    @POST
    Reservation make(Reservation reservation);

    @GET
    @Path("availability")
    List<Car> availability(
            @RestQuery LocalDate startDate,
            @RestQuery LocalDate endDate);
}
