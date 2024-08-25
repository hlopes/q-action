package org.acme.reservation.inventory;

import java.util.List;

public interface ReservationsRepository {

  List<Reservation> findAll();

  Reservation save(Reservation reservation);
}
