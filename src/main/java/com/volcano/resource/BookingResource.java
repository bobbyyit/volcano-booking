package com.volcano.resource;

import com.volcano.domain.ApiValidationError;
import com.volcano.domain.Availability;
import com.volcano.domain.Booking;
import com.volcano.domain.Confirmation;
import com.volcano.domain.SearchParameters;
import com.volcano.domain.User;
import com.volcano.domain.builder.ApiErrorBuilder;
import com.volcano.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.volcano.helper.ResponseBodyHelper.*;

@RestController
public class BookingResource {

    private BookingService bookingService;

    @Autowired
    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @RequestMapping(value = "/availabilities", method = RequestMethod.GET)
    public ResponseEntity availabilities(@RequestBody SearchParameters searchParameters) {

        List<ApiValidationError> errors = new ApiErrorBuilder()
                .notNull("from", searchParameters.getFrom())
                .isDateValid("from", searchParameters.getFrom())
                .notNull("to", searchParameters.getTo())
                .isDateValid("to", searchParameters.getTo())
                .build();

        if (!errors.isEmpty()) {
            return badRequest(errors);
        }

        List<Availability> availabilities = bookingService.getAvailabilities(
                Optional.ofNullable(searchParameters.getFrom()),
                Optional.ofNullable(searchParameters.getTo()));

        return ok(availabilities);
    }

    @RequestMapping(value = "/booking", method = RequestMethod.GET)
    public ResponseEntity booking(@RequestBody SearchParameters searchParameters) {

        List<ApiValidationError> errors = new ApiErrorBuilder()
                .notEmpty("ids", searchParameters.getIds())
                .notNull("userId", searchParameters.getUserId())
                .build();

        if (errors.size() == 1) {
            List<Booking> booking = null;
            if (searchParameters.getIds() != null) {
                booking = bookingService.getBookingWithIds(Optional.of(searchParameters.getIds()));
            } else if (searchParameters.getUserId() != null) {
                booking = bookingService.getBookingWithUserId(Optional.of(searchParameters.getUserId()));
            }
            return ok(booking);
        }

        return badRequest(errors);
    }

    @RequestMapping(value = "/book", method = RequestMethod.DELETE)
    public ResponseEntity deleteBooking(@RequestBody SearchParameters searchParameters) {

        List<ApiValidationError> errors = new ApiErrorBuilder()
                .notNull("id", searchParameters.getId())
                .build();

        if (!errors.isEmpty()) {
            return badRequest(errors);
        }

        Confirmation confirmation = bookingService.deleteBooking(searchParameters.getId());

        if (confirmation.hasErrors()) {
            return badRequest(confirmation.getErrors());
        } else {
            return ok(confirmation);
        }
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public ResponseEntity book(@RequestBody SearchParameters searchParameters) {
        List<ApiValidationError> errors = new ApiErrorBuilder()
                .notNull("email", searchParameters.getEmail())
                .notNull("firstName", searchParameters.getFirstName())
                .notNull("lastName", searchParameters.getLastName())
                .notNull("from", searchParameters.getFrom())
                .isDateValid("from", searchParameters.getFrom())
                .notNull("to", searchParameters.getTo())
                .isDateValid("to", searchParameters.getTo())
                .isNotToday("from", searchParameters.getFrom())
                .isNotAfterDays("from", 30, searchParameters.getTo())
                .doesNotExceedDays("from/to", 3, searchParameters.getFrom(), searchParameters.getTo())
                .build();

        if (!errors.isEmpty()) {
            return badRequest(errors);
        }

        User user = new User();
        user.setEmail(searchParameters.getEmail());
        user.setFirstName(searchParameters.getFirstName());
        user.setLastName(searchParameters.getLastName());

        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.parse(searchParameters.getFrom()));
        booking.setCheckOutDate(LocalDate.parse(searchParameters.getTo()));

        Confirmation confirmation = bookingService.book(user, booking);
        if (confirmation.hasErrors()) {
            return badRequest(confirmation.getErrors());
        } else {
            return created(confirmation);
        }
    }

}
