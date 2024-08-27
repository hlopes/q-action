package org.acme;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import groovy.util.logging.Slf4j;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.Reservation;
import org.acme.reservation.rest.ReservationResource;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Slf4j
@QuarkusTest
class ReservationResourceTest {

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource
  URL reservationResourceURL;

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource("availability")
  URL availabilityURL;

  @BeforeAll
  public static void setup() {
    GraphQLInventoryClient inventoryClientMock = Mockito.mock(GraphQLInventoryClient.class);

    var peugeot =
        Car.builder()
            .id(1L)
            .manufacturer("Peugeot")
            .model("3006")
            .licencePlateNumber("testPlate")
            .build();

    var fiat =
        Car.builder()
            .id(2L)
            .manufacturer("Fiat")
            .model("500")
            .licencePlateNumber("testPlate1")
            .build();

    Mockito.when(inventoryClientMock.getAllCars()).thenReturn(List.of(peugeot, fiat));

    QuarkusMock.installMockForType(inventoryClientMock, GraphQLInventoryClient.class);
  }

  @Test
  public void testReservationIds() {
    var reservation =
        Reservation.builder()
            .carId(12345L)
            .startDay(LocalDate.parse("2025-03-20"))
            .endDay(LocalDate.parse("2025-03-29"))
            .build();

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResourceURL)
        .then()
        .statusCode(StatusCode.OK)
        .body("id", notNullValue());
  }

  @Test
  public void testMakingAReservationAndCheckAvailability() {
    String startDate = "2022-01-01";
    String endDate = "2022-01-10";

    var cars =
        RestAssured.given()
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .when()
            .get(availabilityURL)
            .then()
            .statusCode(StatusCode.OK)
            .extract()
            .as(Car[].class);

    // Choose one of the cars
    Car car = cars[0];

    System.out.println("### cars: " + car);

    // Prepare a Reservation object
    Reservation reservation =
        Reservation.builder()
            .carId(car.getId())
            .startDay(LocalDate.parse(startDate))
            .endDay(LocalDate.parse(endDate))
            .build();

    // Submit the reservation
    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResourceURL)
        .then()
        .statusCode(StatusCode.OK)
        .body("carId", is(car.getId().intValue()));

    // Verify that this car doesn't show as available anymore
    RestAssured.given()
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .when()
        .get(availabilityURL)
        .then()
        .statusCode(StatusCode.OK)
        .body("findAll { car -> car.id == " + car.getId() + "}", hasSize(0));
  }
}
