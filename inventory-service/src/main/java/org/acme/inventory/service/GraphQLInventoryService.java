package org.acme.inventory.service;

import jakarta.inject.Inject;
import org.acme.inventory.model.Car;
import org.acme.inventory.model.CarInventory;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

  @Inject
  CarInventory carInventory;

  @Query
  public List<Car> cars() {
    return carInventory.getCars();
  }

  @Mutation
  public Car register(Car car) {
    return carInventory.addCar(car);
  }

  @Mutation
  public boolean remove(String licencePlateNumber) {
    var cars = carInventory.getCars();

    Optional<Car> toBeRemoved =
        cars.stream().filter(car -> car.getLicencePlateNumber().equals(licencePlateNumber))
            .findAny();

    return toBeRemoved.map(car -> carInventory.removeCar(car)).isPresent();
  }
}
