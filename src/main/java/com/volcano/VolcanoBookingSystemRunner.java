package com.volcano;


import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class VolcanoBookingSystemRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(VolcanoBookingSystemRunner.class, args);
    }

    @PostConstruct
    private void initDb() {
        System.out.println("****** Creating table: USER, BOOKING, and Inserting test data ******");

        String sqlStatements[] = {
                "drop table user if exists",
                "CREATE TABLE user" +
                        "(id bigint auto_increment, " +
                        "email varchar(100), " +
                        "first_name varchar(100), " +
                        "last_name varchar(100), " +
                        "PRIMARY KEY (id))",
                "drop table booking if exists",
                "CREATE TABLE booking\n" +
                "(\n" +
                "  id bigint   AUTO_INCREMENT,\n" +
                "  user_id     bigint,\n" +
                "  checkin   DATE,\n" +
                "  checkout  DATE,\n" +
                "  PRIMARY   KEY (ID),\n" +
                "  FOREIGN KEY (user_id) REFERENCES user(id)" +
                ") "
        };

        Arrays.stream(sqlStatements).forEach(sql -> {
            System.out.println(sql);
            jdbcTemplate.execute(sql);
        });

        long userJohnWickId = insertUser("john.wick@continental.com", "John", "Wick");
        insertBooking(userJohnWickId, LocalDate.now(), LocalDate.now().plusDays(3));
        insertBooking(userJohnWickId, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));

        long userNeoAndersonId = insertUser("neo.anderson@zion.com", "Neo", "Anderson");
        insertBooking(userNeoAndersonId, LocalDate.now().plusDays(7), LocalDate.now().plusDays(9));

        long userJohnnySilverhandId = insertUser("johhny.silverhand@cyberpunk.com", "Johnny", "Silverhand");
        insertBooking(userJohnnySilverhandId, LocalDate.now().plusDays(20), LocalDate.now().plusDays(23));

    }

    public long insertBooking(long userId, LocalDate checkInDate, LocalDate checkOutDate) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("booking").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("user_id", userId);
        parameters.put("checkin", checkInDate.toString());
        parameters.put("checkout", checkOutDate.toString());
        return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    public long insertUser(String email, String firstName, String lastName) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("user").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("email", email);
        parameters.put("first_name", firstName);
        parameters.put("last_name", lastName);
        return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9091");
    }
}
