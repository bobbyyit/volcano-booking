package com.volcano.service;

import com.google.common.collect.Sets;
import com.volcano.domain.*;
import com.volcano.repository.BookingRepository;
import com.volcano.repository.UserRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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

        Set<LocalDate> all = dateFrom
                .datesUntil(dateTo)
                .collect(Collectors.toSet());

        Set<LocalDate> booked = new HashSet<>();
        List<Booking> bookings = bookingRepository.getBookings(dateFrom, dateTo);
        for (Booking booking : bookings) {
            Set<LocalDate> durationBooked = booking.getCheckInDate()
                    .datesUntil(booking.getCheckOutDate())
                    .collect(Collectors.toSet());
            booked.addAll(durationBooked);
        }

        List<LocalDate> difference = new ArrayList<>(Sets.difference(all, booked));
        Collections.sort(difference);

        return difference
                .stream()
                .map(localDate -> new Availability(localDate.toString()))
                .collect(Collectors.toList());
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
