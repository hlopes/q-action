package org.acme.inventory;

import io.quarkus.test.Mock;
import java.util.List;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.InventoryClient;

@Mock
public class MockInventoryClient implements InventoryClient {

  @Override
  public List<Car> getAllCars() {
    var car =
        Car.builder()
            .id(1L)
            .licencePlateNumber("ABC123")
            .manufacturer("Peugeot")
            .model("3006")
            .build();

    return List.of(car);
  }
}
