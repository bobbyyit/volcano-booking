package com.volcano.service;

import com.volcano.domain.Booking;
import com.volcano.repository.BookingRepository;
import com.volcano.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplBookingTest {

    private BookingServiceImpl underTest;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        underTest = new BookingServiceImpl(bookingRepository, userRepository);
    }

    @Test
    void returnNoEmptyBooking() {
        List<Booking> result = underTest.getBookingWithIds(Optional.empty());

        assertThat(result, is(empty()));
    }

    @Test
    void returnsABookingFromIds() {
        Booking booking = aBooking();
        when(bookingRepository.getBookings(singletonList(100))).thenReturn(singletonList(booking));
        List<Booking> result = underTest.getBookingWithIds(Optional.of(new Integer[]{100}));

        assertThat(result, hasSize(1));
        assertThat(result, contains(booking));
    }

    @Test
    void returnsABookingFromUserId() {
        Booking booking = aBooking();
        when(bookingRepository.getBookings(2)).thenReturn(singletonList(booking));
        List<Booking> result = underTest.getBookingWithUserId(Optional.of(2));

        assertThat(result, hasSize(1));
        assertThat(result, contains(booking));
    }

    private Booking aBooking() {
        Booking booking = new Booking();
        booking.setId(100);
        booking.setUserId(20);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now());
        return booking;
    }
}