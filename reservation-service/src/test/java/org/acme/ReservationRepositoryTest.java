package org.acme;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDate;
import org.acme.reservation.inventory.Reservation;
import org.acme.reservation.inventory.ReservationsRepository;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ReservationRepositoryTest {

  @Inject
  ReservationsRepository repository;

  @Test
  public void testCreateReservation() {
    var reservation = Reservation.builder()
        .carId(384L)
        .startDay(LocalDate.now().plusDays(5))
        .endDay(LocalDate.now().plusDays(12))
        .build();

    repository.save(reservation);

    assertNotNull(reservation.getId());
    assertTrue(repository.findAll().contains(reservation));
  }
}
