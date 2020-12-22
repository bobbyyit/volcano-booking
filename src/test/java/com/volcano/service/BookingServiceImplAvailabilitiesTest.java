package com.volcano.service;

import com.volcano.domain.Availability;
import com.volcano.domain.Booking;
import com.volcano.repository.BookingRepository;
import com.volcano.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

class BookingServiceImplAvailabilitiesTest {

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
    void returnsAvailabilitiesFor30DaysByDefault() {
        List<Availability> result = underTest.getAvailabilities(Optional.empty(), Optional.empty());

        assertThat(result, hasSize(30));
    }

    @Test
    void returnsAvailabilitiesFor5DaysFromToday() {
        List<Availability> result = underTest.getAvailabilities(
                Optional.of(LocalDate.now().toString()),
                Optional.of(LocalDate.now().plusDays(5).toString()));

        Availability availability = getAvailability(LocalDate.now());
        Availability availability1 = getAvailability(LocalDate.now().plusDays(1));
        Availability availability2 = getAvailability(LocalDate.now().plusDays(2));
        Availability availability3 = getAvailability(LocalDate.now().plusDays(3));
        Availability availability4 = getAvailability(LocalDate.now().plusDays(4));

        assertThat(result, containsInRelativeOrder(
                availability,
                availability1,
                availability2,
                availability3,
                availability4));
        assertThat(result, hasSize(5));
    }

    @Test
    void doesNotReturnDateThatIsBooked() {
        LocalDate now = LocalDate.now();
        LocalDate dateIn5Days = LocalDate.now().plusDays(5);
        Booking booking = getBooking(now, 3, 4);
        Mockito.when(bookingRepository.getBookings(now, dateIn5Days)).thenReturn(Collections.singletonList(booking));

        List<Availability> result = underTest.getAvailabilities(
                Optional.of(now.toString()),
                Optional.of(dateIn5Days.toString()));

        Availability availability = getAvailability(now);
        Availability availability1 = getAvailability(now.plusDays(1));
        Availability availability2 = getAvailability(now.plusDays(2));
        Availability availability4 = getAvailability(now.plusDays(4));

        assertThat(result, containsInRelativeOrder(
                availability,
                availability1,
                availability2,
                availability4));
        assertThat(result, hasSize(4));
    }

    @Test
    void doesNotReturnDatesThatIsBooked() {
        LocalDate now = LocalDate.now();
        LocalDate dateIn5Days = LocalDate.now().plusDays(5);
        Booking booking = getBooking(now, 3, 4);
        Booking anotherBooking = getBooking(now, 0, 2);
        Mockito.when(bookingRepository.getBookings(now, dateIn5Days)).thenReturn(Arrays.asList(booking, anotherBooking));

        List<Availability> result = underTest.getAvailabilities(
                Optional.of(now.toString()),
                Optional.of(dateIn5Days.toString()));

        Availability availability = getAvailability(now.plusDays(2));
        Availability anotherAvailability = getAvailability(now.plusDays(4));

        assertThat(result, hasSize(2));
        assertThat(result, containsInRelativeOrder(
                availability,
                anotherAvailability));
    }

    private Booking getBooking(LocalDate now, long daysToAddToFromDate, long daysToAddToToDate) {
        Booking anotherBooking = new Booking();
        anotherBooking.setCheckInDate(now.plusDays(daysToAddToFromDate));
        anotherBooking.setCheckOutDate(now.plusDays(daysToAddToToDate));
        return anotherBooking;
    }

    private Availability getAvailability(LocalDate date) {
        Availability availability = new Availability();
        availability.setDate(date.toString());
        return availability;
    }
}