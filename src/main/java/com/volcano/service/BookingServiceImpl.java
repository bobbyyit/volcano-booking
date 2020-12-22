package com.volcano.service;

import com.volcano.domain.*;
import com.volcano.repository.BookingRepository;
import com.volcano.repository.UserRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Availability> getAvailabilities(Optional<String> from, Optional<String> to) {

        LocalDate dateFrom;
        LocalDate dateTo;
        if (from.isPresent() && to.isPresent()) {
            dateFrom = LocalDate.parse(from.get());
            dateTo = LocalDate.parse(to.get());
        } else {
            dateFrom = LocalDate.now();
            dateTo = dateFrom.plusDays(30);
        }

        long between = ChronoUnit.DAYS.between(dateFrom, dateTo);
        Map<String, Availability> availabilities = new TreeMap<>();
        for (long i = 0; i < between; i++) {
            Availability availability = new Availability();
            LocalDate localDate = dateFrom.plusDays(i);
            availability.setDate(localDate.toString());
            availabilities.put(localDate.toString(), availability);
        }

        List<Booking> bookings = bookingRepository.getBookings(dateFrom, dateTo);
        for (Booking booking : bookings) {
            long bookingDurationDays = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            for (long i = 0; i < bookingDurationDays; i++) {
                LocalDate dateToRemove = booking.getCheckInDate().plusDays(i);
                availabilities.remove(dateToRemove.toString());
            }
        }
        return new ArrayList<>(availabilities.values());
    }

    @Override
    public List<Booking> getBookingWithIds(Optional<Integer[]> ids) {
        if (!ids.isPresent()) {
            return Collections.emptyList();
        }

        return bookingRepository.getBookings(Arrays.asList(ids.get()));
    }

    @Override
    public List<Booking> getBookingWithUserId(Optional<Integer> userId) {
        if (!userId.isPresent()) {
            return Collections.emptyList();
        }

        return bookingRepository.getBookings(userId.get());
    }

    @Override
    @Transactional
    public Confirmation book(User user, Booking booking) {
        Optional<User> existingUser = userRepository.getUser(user.getEmail());
        if (existingUser.isPresent()) {
            booking.setUserId(existingUser.get().getId());
            user.setId(existingUser.get().getId());
        } else {
            Integer userId = userRepository.createUser(user).getId();
            booking.setUserId(userId);
            user.setId(userId);
        }

        List<ApiValidationError> error = null;
        Booking book = null;
        List<Booking> bookings = bookingRepository.getBookings(booking.getCheckInDate(), booking.getCheckOutDate());
        if (bookings.isEmpty()) {
            book = bookingRepository.book(booking);
        } else {
            error = Collections.singletonList(new ApiValidationError("from/to", "Date chosen is unavailable"));
        }

        return new Confirmation(user, book, error);
    }

    @Override
    public Confirmation deleteBooking(Integer id) {
        List<Booking> bookings = bookingRepository.getBookings(Collections.singletonList(id));

        Confirmation confirmation = new Confirmation();
        if (bookings.isEmpty()) {
            confirmation.setErrors(Collections.singletonList(new ApiValidationError("id", "Booking Id does not exist.")));
            return confirmation;
        }

        if (bookingRepository.deleteBooking(id)) {
            confirmation.setBooking(bookings.get(0));
        } else {
            confirmation.setErrors(Collections.singletonList(new ApiValidationError("id", "Could not delete booking.")));
        }

        return confirmation;
    }
}
