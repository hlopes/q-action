package org.acme.inventory.model;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class CarInventory {
  private static final AtomicLong ids = new AtomicLong(0);
  private List<Car> cars;

  @PostConstruct
  void initialize() {
    cars = new CopyOnWriteArrayList<>();

    initialData();
  }

  public List<Car> getCars() {
    return cars;
  }

  public Car addCar(final Car car) {
    car.setId(ids.incrementAndGet());
    cars.add(car);

    return car;
  }

  public boolean removeCar(final Car car) {
    return cars.remove(car);
  }

  private void initialData() {
    final Car mazda = Car.builder()
        .id(ids.incrementAndGet())
        .manufacturer("Mazda")
        .model("6")
        .licencePlateNumber("ABC123")
        .build();

    final Car ford = Car.builder()
        .id(ids.incrementAndGet())
        .manufacturer("Ford")
        .model("Mustang")
        .licencePlateNumber("XYZ987")
        .build();

    cars.add(mazda);
    cars.add(ford);
  }
}
