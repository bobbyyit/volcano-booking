package com.volcano.service;

import com.volcano.domain.ApiValidationError;
import com.volcano.domain.Booking;
import com.volcano.domain.Confirmation;
import com.volcano.domain.User;
import com.volcano.repository.BookingRepository;
import com.volcano.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class BookingServiceImplDeleteBookTest {
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
    void deletesBooking() {
        when(bookingRepository.getBookings(Collections.singletonList(50))).thenReturn(Collections.singletonList(aBooking()));
        when(bookingRepository.deleteBooking(any())).thenReturn(true);
        Confirmation result = underTest.deleteBooking(50);

        verify(bookingRepository, times(1)).deleteBooking(50);
        assertThat(result.hasErrors(), is(false));
    }

    @Test
    void doesNotDeleteWhenBookingDoesNotExist() {
        when(bookingRepository.getBookings(Collections.singletonList(50))).thenReturn(emptyList());

        Confirmation result = underTest.deleteBooking(50);

        assertThat(result.getErrors(), contains(new ApiValidationError("id", "Booking Id does not exist.")));
        assertThat(result.hasErrors(), is(true));
    }

    private User aUser() {
        User user = new User();
        user.setEmail("some-email@host.com");
        user.setFirstName("first-name");
        user.setLastName("last-name");
        return user;
    }

    private Booking aBooking() {
        Booking booking = new Booking();
        booking.setId(100);
        booking.setUserId(20);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now());
        return booking;
    }}
