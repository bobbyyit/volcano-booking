package com.volcano.service;

import com.volcano.domain.ApiValidationError;
import com.volcano.domain.Booking;
import com.volcano.domain.Confirmation;
import com.volcano.domain.User;
import com.volcano.repository.BookingRepository;
import com.volcano.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class BookingServiceImplBookTest {

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
    void bookAndCreateUserWhenUserDoesntExist() {
        when(userRepository.getUser("some-email@host.com")).thenReturn(Optional.empty());
        User user = aUser();
        Booking booking = aBooking();
        user.setId(777);
        when(userRepository.createUser(user)).thenReturn(user);
        when(bookingRepository.getBookings(any(), any())).thenReturn(emptyList());
        when(bookingRepository.book(any())).thenReturn(booking);

        Confirmation result = underTest.book(user, booking);

        booking.setId(999);
        verify(userRepository, times(1)).createUser(user);
        verify(bookingRepository, times(1)).book(booking);
        assertThat(result.getBooking(), is(booking));
        assertThat(result.getUser(), is(user));
    }

    @Test
    void bookWhenUserExist() {
        User user = aUser();
        user.setId(777);
        when(userRepository.getUser("some-email@host.com")).thenReturn(Optional.of(user));
        Booking booking = aBooking();
        when(bookingRepository.getBookings(any(), any())).thenReturn(emptyList());
        when(bookingRepository.book(any())).thenReturn(booking);

        Confirmation result = underTest.book(user, booking);

        booking.setId(999);
        verify(userRepository, times(0)).createUser(user);
        verify(bookingRepository, times(1)).book(booking);
        assertThat(result.getBooking(), is(booking));
        assertThat(result.getUser(), is(user));
    }

    @Test
    void doesNotBookWhenDateConflicts() {
        User user = aUser();
        user.setId(777);
        when(userRepository.getUser("some-email@host.com")).thenReturn(Optional.of(user));
        Booking booking = aBooking();
        when(bookingRepository.getBookings(any(), any())).thenReturn(Collections.singletonList(booking));
        Confirmation result = underTest.book(user, booking);

        verify(userRepository, times(0)).createUser(user);
        verify(bookingRepository, times(0)).book(booking);
        assertThat(result.getErrors(), contains(new ApiValidationError("from/to", "Date chosen is unavailable")));
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
    }
}