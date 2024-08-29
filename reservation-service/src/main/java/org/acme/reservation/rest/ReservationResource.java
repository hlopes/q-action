package org.acme.reservation.rest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.Reservation;
import org.acme.reservation.inventory.ReservationsRepository;
import org.acme.reservation.rental.RentalClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.SecurityContext;

@Path("reservation")
public class ReservationResource {

  private GraphQLInventoryClient inventoryClient;
  private RentalClient rentalClient;
  private ReservationsRepository reservationsRepository;
  private SecurityContext securityContext;

  public ReservationResource(
      @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient, @RestClient RentalClient rentalClient,
      ReservationsRepository reservationsRepository,
      SecurityContext securityContext) {
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
    this.reservationsRepository = reservationsRepository;
  }

  @GET
  @Path("availability")
  public Collection<Car> availability(
      @RestQuery final LocalDate startDate,
      @RestQuery final LocalDate endDate) {
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
    final var principal = Optional.ofNullable(securityContext.getUserPrincipal());
    final var userId = principal.isPresent() ? principal.get().getName() : null;

    reservation.setUserId(userId);

    final var persitedReservation = reservationsRepository.save(reservation);

    if (persitedReservation.getStartDay().equals(LocalDate.now())) {
      rentalClient.start(persitedReservation.getUserId(), persitedReservation.getId().toString());
    }

    return persitedReservation;
  }

  @GET
  @Path("all")
  public Collection<Reservation> getAllReservations() {
    final var userId = getLoggedUserId();

    return reservationsRepository
        .findAll()
        .stream()
        .filter(reservation -> userId == null || userId.equals(reservation.getUserId()))
        .toList();
  }

  private String getLoggedUserId() {
    final var principal = Optional.ofNullable(securityContext.getUserPrincipal());

    return principal.isPresent() ? principal.get().getName() : null;
  }
}
