package org.acme.reservation.inventory;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class Reservation {
  private Long id;
  private String userId;
  private Long carId;
  private LocalDate startDay;
  private LocalDate endDay;

  /**
   * Check if the given duration overlaps with this reservation
   *
   * @return true if the dates overlap with the reservation, false otherwise
   */
  public boolean isReserved(LocalDate startDay, LocalDate endDay) {
    return this.endDay.isAfter(startDay) && this.startDay.isBefore(endDay);
  }
}
