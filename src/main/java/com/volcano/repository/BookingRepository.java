package com.volcano.repository;

import com.volcano.domain.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository {

    List<Booking> getBookings(LocalDate from, LocalDate to);

    List<Booking> getBookings(List<Integer> ids);

    List<Booking> getBookings(Integer userId);

    Booking book(Booking booking);

    boolean deleteBooking(Integer id);
}
