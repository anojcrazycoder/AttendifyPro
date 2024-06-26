package edu.miu.attendifypro.mapper;

import edu.miu.attendifypro.domain.*;
import edu.miu.attendifypro.dto.request.CourseOfferingCreateRequest;
import edu.miu.attendifypro.dto.request.LocationCreateRequest;
import edu.miu.attendifypro.dto.request.LocationUpdateRequest;
import edu.miu.attendifypro.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseOfferingDtoMapper {
    CourseOfferingDtoMapper courseOfferingDtoMapper =
            Mappers.getMapper(CourseOfferingDtoMapper.class);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "location", ignore = true)
    CourseOffering courseOfferingCreateRequestToCourseOffering(CourseOfferingCreateRequest courseOffRequest);


    CourseOfferingResponse courseOfferingToCourseOfferingResponse(CourseOffering courseOffering);
    FacultyResponse facultyToFacultyResponse(Faculty faculty);

    StudentCourseSelectionResponse studentCourseSelectionToResponse(StudentCourseSelection data);
}
