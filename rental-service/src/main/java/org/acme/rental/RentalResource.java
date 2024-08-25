package org.acme.rental;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Path("/rental")
public class RentalResource {

  private final AtomicLong id = new AtomicLong(0);

  @POST
  @Path("/start/{userId}/{reservationId}")
  public Rental start(String userId, Long reservationId) {
    log.info("Start rental for {} with reservation {}", userId, reservationId);

    return Rental.builder().userId(userId).reservationId(reservationId).build();
  }
}
