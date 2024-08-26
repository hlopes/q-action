package org.acme;

import static org.hamcrest.CoreMatchers.notNullValue;

import java.net.URL;
import java.time.LocalDate;

import org.acme.reservation.inventory.Reservation;
import org.acme.reservation.rest.ReservationResource;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
class ReservationResourceTest {

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource
  URL reservationResourceURL;

  @Test
  public void testReservationIds() {
    var reservation = Reservation.builder()
      .carId(12345L)
      .startDay(LocalDate.parse("2025-03-20"))
      .endDay(LocalDate.parse("2025-03-29"))
      .build();

      RestAssured
      .given()
      .contentType(ContentType.JSON)
      .body(reservation)
      .when()
      .post(reservationResourceURL)
      .then()
      .statusCode(StatusCode.OK)
      .body("id", notNullValue());
  }
}
