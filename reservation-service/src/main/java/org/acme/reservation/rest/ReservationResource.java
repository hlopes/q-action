package org.acme.reservation.rest;

import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.Reservation;
import org.acme.reservation.inventory.ReservationsRepository;
import org.acme.reservation.rental.RentalClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("reservation")
public class ReservationResource {

  @Inject ReservationsRepository reservationsRepository;

  @GraphQLClient("inventory")
  @Inject
  GraphQLInventoryClient inventoryClient;

  @RestClient @Inject RentalClient rentalClient;

  @GET
  @Path("availability")
  public Collection<Car> availability(
      @RestQuery final LocalDate startDate, @RestQuery final LocalDate endDate) {
    final var availableCars = inventoryClient.getAllCars();

    final var carById = new HashMap<Long, Car>();

    availableCars.forEach(car -> carById.put(car.getId(), car));

    final var reservations = reservationsRepository.findAll();

    reservations.forEach(
        reservation -> {
          if (reservation.isReserved(startDate, endDate)) {
            carById.remove(reservation.getCarId());
          }
        });

    return carById.values();
  }

  @POST
  public Reservation make(final Reservation reservation) {
    final var persitedReservation = reservationsRepository.save(reservation);

    if (persitedReservation.getStartDay().equals(LocalDate.now())) {
      rentalClient.start(persitedReservation.getUserId(), persitedReservation.getId().toString());
    }

    return persitedReservation;
  }
}
