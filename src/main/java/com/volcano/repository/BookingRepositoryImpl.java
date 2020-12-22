package com.volcano.repository;

import com.volcano.domain.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BookingRepositoryImpl implements BookingRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @Autowired
    public BookingRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public List<Booking> getBookings(LocalDate from, LocalDate to) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("from", from);
        parameters.addValue("to", to);

        List<Booking> bookings = jdbcTemplate.query("select id, user_id, checkin, checkout from booking where checkin >= :from and checkin <= :to",
                parameters,
                new RowMapper<Booking>() {
                    @Override
                    public Booking mapRow(ResultSet rs, int i) throws SQLException {
                        Booking booking = new Booking();
                        booking.setId(rs.getInt("id"));
                        booking.setUserId(rs.getInt("user_id"));
                        booking.setCheckInDate(LocalDate.parse(rs.getString("checkin")));
                        booking.setCheckOutDate(LocalDate.parse(rs.getString("checkout")));

                        return booking;
                    }
                });
        return bookings;
    }

    @Override
    public List<Booking> getBookings(List<Integer> ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        List<Booking> bookings = jdbcTemplate.query("select id, user_id, checkin, checkout from booking where id in (:ids)",
                parameters,
                new RowMapper<Booking>() {
                    @Override
                    public Booking mapRow(ResultSet rs, int i) throws SQLException {
                        Booking booking = new Booking();
                        booking.setId(rs.getInt("id"));
                        booking.setUserId(rs.getInt("user_id"));
                        booking.setCheckInDate(LocalDate.parse(rs.getString("checkin")));
                        booking.setCheckOutDate(LocalDate.parse(rs.getString("checkout")));

                        return booking;
                    }
                });

        return bookings;
    }

    @Override
    public List<Booking> getBookings(Integer userId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        List<Booking> bookings = jdbcTemplate.query("select id, user_id, checkin, checkout from booking where user_id = :userId",
                parameters,
                new RowMapper<Booking>() {
                    @Override
                    public Booking mapRow(ResultSet rs, int i) throws SQLException {
                        Booking booking = new Booking();
                        booking.setId(rs.getInt("id"));
                        booking.setUserId(rs.getInt("user_id"));
                        booking.setCheckInDate(LocalDate.parse(rs.getString("checkin")));
                        booking.setCheckOutDate(LocalDate.parse(rs.getString("checkout")));

                        return booking;
                    }
                });

        return bookings;
    }

    @Override
    public Booking  book(Booking booking) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("booking")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", booking.getUserId());
        parameters.put("checkin", booking.getCheckInDate());
        parameters.put("checkout", booking.getCheckOutDate());

        Number bookingId = simpleJdbcInsert.executeAndReturnKey(parameters);
        booking.setId(bookingId.intValue());

        return booking;
    }

    @Override
    public boolean deleteBooking(Integer id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        String sql = "DELETE FROM booking WHERE id = :id";
        return jdbcTemplate.update(sql, parameters) == 1;
    }
}
