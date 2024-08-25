package org.acme.reservation.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
  private Long id;
  private String licencePlateNumber;
  private String manufacturer;
  private String model;
}
