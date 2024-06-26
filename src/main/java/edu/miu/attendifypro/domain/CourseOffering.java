package edu.miu.attendifypro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kush
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CourseOffering extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int capacity;

    private String courseOfferingType;

    @ManyToOne
    private Location location;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    private Faculty faculty;

    @ManyToOne
    private Course courses;


    public List<Session> getSessions() {
        List<Session> sessions = new ArrayList<>();
        LocalDate currentDate = startDate;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        while (!currentDate.isAfter(endDate)) {
            //TODO better set time from properties file than hard code
            LocalTime startTime1 = LocalTime.parse("10:00 AM", timeFormatter);
            LocalTime endTime1 = LocalTime.parse("12:30 PM", timeFormatter);
            LocalTime startTime2 = LocalTime.parse("01:30 PM", timeFormatter);
            LocalTime endTime2 = LocalTime.parse("03:30 PM", timeFormatter);

            sessions.add(new Session(LocalDateTime.of(currentDate, startTime1), LocalDateTime.of(currentDate, endTime1)));
            sessions.add(new Session(LocalDateTime.of(currentDate, startTime2), LocalDateTime.of(currentDate, endTime2)));

            currentDate = currentDate.plusDays(1);
        }
        sessions.removeLast();
        return sessions;
    }

}
