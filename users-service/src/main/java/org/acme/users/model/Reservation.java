package org.acme.users.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Reservation {
  private Long id;
  private String userId;
  private Long carId;
  private LocalDate startDay;
  private LocalDate endDay;
}
