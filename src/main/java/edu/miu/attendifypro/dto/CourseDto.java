package edu.miu.attendifypro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private int credits;
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private String department;
    private List<CourseDto> prerequisites=new ArrayList<>();
}
