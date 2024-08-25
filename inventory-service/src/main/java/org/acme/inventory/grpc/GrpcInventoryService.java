package org.acme.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.inventory.model.Car;
import org.acme.inventory.model.CarInventory;
import org.acme.inventory.model.CarResponse;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

@Slf4j
@GrpcService
public class GrpcInventoryService implements InventoryService {

  @Inject
  CarInventory inventory;

  @Override
  public Uni<CarResponse> add(final InsertCarRequest request) {
    final var car = buildCarFromCarRequest(request);

    final var savedCar = inventory.addCar(car);

    log.info("Persisted {}", savedCar);

    return Uni.createFrom().item(
        CarResponse.newBuilder().setLicencePlateNumber(savedCar.getLicencePlateNumber())
            .setManufacturer(savedCar.getManufacturer()).setModel(savedCar.getModel())
            .setId(savedCar.getId()).build());
  }

  @Override
  public Uni<CarResponse> remove(final RemoveCarRequest request) {
    final var optionalCar = inventory.getCars().stream()
        .filter(car -> car.getLicencePlateNumber().equals(request.getLicencePlateNumber()))
        .findAny();

    final var isCarRemoved = optionalCar.map(inventory::removeCar);

    if (isCarRemoved.isPresent()) {
      final var removedCar = optionalCar.get();

      return Uni.createFrom().item(
          CarResponse.newBuilder().setLicencePlateNumber(removedCar.getLicencePlateNumber())
              .setManufacturer(removedCar.getManufacturer()).setModel(removedCar.getModel())
              .setId(removedCar.getId()).build());
    }

    return Uni.createFrom().nullItem();
  }

  @Override
  public Multi<CarResponse> addMulti(final Multi<InsertCarRequest> requests) {

    return requests.map(request -> {
      final var car = buildCarFromCarRequest(request);

      return inventory.addCar(car);
    }).onItem().invoke(car -> {
      log.info("### Persisted {}", car);
    }).map(
        savedCar -> CarResponse.newBuilder().setLicencePlateNumber(savedCar.getLicencePlateNumber())
            .setManufacturer(savedCar.getManufacturer()).setModel(savedCar.getModel())
            .setId(savedCar.getId()).build());
  }

  private Car buildCarFromCarRequest(final InsertCarRequest request) {
    return Car.builder().licencePlateNumber(request.getLicencePlateNumber())
        .manufacturer(request.getManufacturer()).model(request.getModel()).build();
  }
}
