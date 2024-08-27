package org.acme.inventory.client;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

  private static final String USAGE =
      "Usage: inventory <add>|<remove> " + "<licence plate number> <manufacturer> <model>";

  @GrpcClient("inventory")
  InventoryService inventoryService;

  @Override
  public int run(final String... args) throws Exception {
    final String action = args.length > 0 ? args[0] : null;

    if ("add".equals(action) && args.length >= 4) {
      add(args[1], args[2], args[3]);

      return 0;
    }

    if ("addM".equals(action) && args.length >= 7) {
      addM(args[1], args[2], args[3], args[4], args[5], args[6]);

      return 0;
    }

    System.err.println(USAGE);

    return 1;
  }

  private void addM(
      final String arg,
      final String arg1,
      final String arg2,
      final String arg3,
      final String arg4,
      final String arg5) {
    var result =
        inventoryService
            .addMulti(
                Multi.createFrom()
                    .items(
                        InsertCarRequest.newBuilder()
                            .setLicencePlateNumber(arg)
                            .setManufacturer(arg1)
                            .setModel(arg2)
                            .build(),
                        InsertCarRequest.newBuilder()
                            .setLicencePlateNumber(arg3)
                            .setManufacturer(arg4)
                            .setModel(arg5)
                            .build()))
            .subscribe()
            .asStream()
            .findAny()
            .isPresent();

    System.out.println("RESULT " + result);
  }

  private void add(final String licencePlateNumber, final String manufacturer, final String model) {
    inventoryService
        .add(
            InsertCarRequest.newBuilder()
                .setLicencePlateNumber(licencePlateNumber)
                .setManufacturer(manufacturer)
                .setModel(model)
                .build())
        .await()
        .indefinitely();
  }
}
