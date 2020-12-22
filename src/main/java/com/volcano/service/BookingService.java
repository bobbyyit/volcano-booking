package com.volcano.service;

import com.volcano.domain.Availability;
import com.volcano.domain.Booking;
import com.volcano.domain.Confirmation;
import com.volcano.domain.User;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    List<Availability> getAvailabilities(Optional<String> from, Optional<String> to);

    List<Booking> getBookingWithIds(Optional<Integer[]> ids);

    List<Booking> getBookingWithUserId(Optional<Integer> userId);

    Confirmation book(User user, Booking booking);

    Confirmation deleteBooking(Integer id);
}
