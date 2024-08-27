package org.acme.reservation.rental;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rental {
  private final Long id;
  private final String userId;
  private final Long reservationId;
  private final LocalDate startDate;
}
