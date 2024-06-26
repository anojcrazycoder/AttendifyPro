package edu.miu.attendifypro.service;

import edu.miu.attendifypro.config.ContextUser;
import edu.miu.attendifypro.domain.*;
import edu.miu.attendifypro.dto.request.CourseOfferingCreateRequest;
import edu.miu.attendifypro.dto.request.CourseOfferingUpdateRequest;
import edu.miu.attendifypro.dto.response.CourseOfferingResponse;
import edu.miu.attendifypro.dto.response.CourseResponse;
import edu.miu.attendifypro.dto.response.StudentCourseSelectionResponse;
import edu.miu.attendifypro.dto.response.common.ServiceResponse;
import edu.miu.attendifypro.dto.response.report.CourseOfferingWithRosterResponse;
import edu.miu.attendifypro.dto.response.report.CourseScheduleResponse;
import edu.miu.attendifypro.dto.response.report.Report1Response;
import edu.miu.attendifypro.mapper.CourseOfferingDtoMapper;
import edu.miu.attendifypro.mapper.DtoMapper;
import edu.miu.attendifypro.mapper.ReportMapper;
import edu.miu.attendifypro.service.persistence.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseOfferingServiceImpl implements CourseOfferingService{

    private final CourseOfferingPersistenceService persistenceService;
    private final CoursePersistenceService coursePersistenceService;
    private final FacultyPersistenceService facultyPersistenceService;
    private final LocationPersistenceService locationPersistenceService;
    private final StudentCourseSelectionPersistenceService studentCoursePersistence;
    final StudentPersistenceService studentPersistenceService;

    final ContextUser user;


    public CourseOfferingServiceImpl(CourseOfferingPersistenceService persistenceService,
                                     CoursePersistenceService coursePersistenceService,
                                     FacultyPersistenceService facultyPersistenceService,
                                     LocationPersistenceService locationPersistenceService,
                                     StudentCourseSelectionPersistenceService studentCoursePersistence,
                                     StudentPersistenceService studentPersistenceService, ContextUser user) {
        this.persistenceService = persistenceService;
        this.coursePersistenceService = coursePersistenceService;
        this.facultyPersistenceService = facultyPersistenceService;
        this.locationPersistenceService = locationPersistenceService;
        this.studentCoursePersistence=studentCoursePersistence;
        this.studentPersistenceService = studentPersistenceService;
        this.user = user;
    }

    @Override
    public ServiceResponse<Page<CourseOfferingResponse>> getCourseOfferingPage(Pageable pageable) {
        try {
            Page<CourseOffering> courseOfferingPage = persistenceService.findAll(pageable);
            Page<CourseOfferingResponse> responsePage = courseOfferingPage
                                                    .map(CourseOfferingDtoMapper.courseOfferingDtoMapper
                                                    ::courseOfferingToCourseOfferingResponse);

            return ServiceResponse.of(responsePage, AppStatusCode.S20000);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<List<CourseOfferingResponse>> getAllCourseOfferings() {
        try {
            List<CourseOffering> lst = persistenceService.findAll();
            List<CourseOfferingResponse> responseList =lst.stream()
                                            .map(CourseOfferingDtoMapper.courseOfferingDtoMapper
                                            ::courseOfferingToCourseOfferingResponse).toList();
            return ServiceResponse.of(responseList, AppStatusCode.S20000);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<CourseOfferingResponse> getCourseOfferingById(long id) {
        try {
            Optional<CourseOffering> courseOffering = persistenceService.findById(id);
            if(courseOffering.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("not.found","Course offering "));
            }
            return ServiceResponse.of(CourseOfferingDtoMapper.courseOfferingDtoMapper
                    .courseOfferingToCourseOfferingResponse(courseOffering.get()),AppStatusCode.S20001);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }
    @Override
    public ServiceResponse<List<CourseScheduleResponse>> getCoursesByDate(String date) {
        try {
            LocalDate localDate=null;
            if(date!=null && !date.isEmpty())
                localDate = LocalDate.parse(date);

            List<Course> lst = persistenceService.getCoursesByDate(localDate);
            List<CourseScheduleResponse> responseList =lst.stream()
                    .map(ReportMapper.reportMapper
                            ::courseToCourseScheduleResponse).toList();
            return ServiceResponse.of(responseList, AppStatusCode.S20000);
        }
        catch (DateTimeParseException e) {
            return ServiceResponse.of(AppStatusCode.E40002,List.of("date.parse.error"));
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50002);
        }

    }

    @Override
    public ServiceResponse<CourseOfferingResponse> createCourseOffering(CourseOfferingCreateRequest createRequest) {
        try {
            CourseOffering courseOffering = CourseOfferingDtoMapper.courseOfferingDtoMapper
                                    .courseOfferingCreateRequestToCourseOffering(createRequest);

            Optional<Course> course=coursePersistenceService.findById(createRequest.getCourseId());
            if(course.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("course.not.exists"));
            }
            courseOffering.setCourses(course.get());

            Optional<Location> location=locationPersistenceService.findById(createRequest.getLocationId());
            if(location.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("location.not.exists"));
            }
            courseOffering.setLocation(location.get());

            Optional<Faculty> faculty=facultyPersistenceService.findById(createRequest.getFacultyId());
            if(faculty.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("faculty.not.exists"));
            }
            courseOffering.setFaculty(faculty.get());
            persistenceService.save(courseOffering);

            return ServiceResponse.of(CourseOfferingDtoMapper.courseOfferingDtoMapper
                    .courseOfferingToCourseOfferingResponse(courseOffering),AppStatusCode.S20001);

        }catch(DataIntegrityViolationException ex){
            return ServiceResponse.of(AppStatusCode.E40002);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50003);
        }
    }

    @Override
    public ServiceResponse<CourseOfferingResponse> updateCourseOffering(long id,CourseOfferingUpdateRequest updateRequest) {
        try {
            Optional<CourseOffering> courseOfferingById=persistenceService.findById(id);
            if(courseOfferingById.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("not.found","Course Offering"));
            }
            CourseOffering courseOffering=courseOfferingById.get();
            courseOffering.setCapacity(updateRequest.getCapacity());
            courseOffering.setCourseOfferingType(updateRequest.getCourseOfferingType());
            courseOffering.setStartDate(updateRequest.getStartDate());
            courseOffering.setEndDate(updateRequest.getEndDate());

            Optional<Course> course=coursePersistenceService.findById(updateRequest.getCourseId());
            if(course.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("course.not.exists"));
            }
            courseOffering.setCourses(course.get());

            Optional<Location> location=locationPersistenceService.findById(updateRequest.getLocationId());
            if(location.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("location.not.exists"));
            }
            courseOffering.setLocation(location.get());

            Optional<Faculty> faculty=facultyPersistenceService.findById(updateRequest.getFacultyId());
            if(faculty.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("faculty.not.exists"));
            }
            courseOffering.setFaculty(faculty.get());
            persistenceService.save(courseOffering);

            return ServiceResponse.of(CourseOfferingDtoMapper.courseOfferingDtoMapper
                    .courseOfferingToCourseOfferingResponse(courseOffering),AppStatusCode.S20001);

        }catch(DataIntegrityViolationException ex){
            return ServiceResponse.of(AppStatusCode.E40002);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50003);
        }
    }

    @Override
    public ServiceResponse<Boolean> deleteCourseOffering(Long id) {
        try {
            Optional<CourseOffering> courseOfferingById=persistenceService.findById(id);
            if(courseOfferingById.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("not.found","Course Offering"));
            }
            persistenceService.delete(courseOfferingById.get());
            return ServiceResponse.of(true,AppStatusCode.S20001);

        }catch(DataIntegrityViolationException ex){
            return ServiceResponse.of(AppStatusCode.E40002);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50003);
        }
    }

    @Override
    public ServiceResponse<List<StudentCourseSelectionResponse>> getStudentCourseOfferingById(long offeringId) {
        try{
            Optional<Student> studentOpt=studentPersistenceService.findByAccountId(user.getUser().getId());
            List<StudentCourseSelection> lst=studentCoursePersistence.findByStudentIdAndCourseOfferingId(studentOpt.get().getStudentId(),offeringId);
            List<StudentCourseSelectionResponse> responseList =lst.stream()
                    .map(CourseOfferingDtoMapper.courseOfferingDtoMapper
                            ::studentCourseSelectionToResponse).toList();
            return ServiceResponse.of(responseList, AppStatusCode.S20000);
        }
        catch (Exception e){
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<List<Report1Response>> getStudentCourseOffering() {
        try{
            Optional<Student> studentOpt=studentPersistenceService.findByAccountId(user.getUser().getId());
            List<StudentCourseSelection> lst=studentCoursePersistence.findByStudentId(studentOpt.get().getStudentId());
            List<Report1Response> responseList =lst.stream()
                    .map(ReportMapper.reportMapper
                            ::studentCourseSelectionToReport1Response).toList();
            return ServiceResponse.of(responseList, AppStatusCode.S20000);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ServiceResponse.of(AppStatusCode.E50002);

        }
    }

    @Override
    public ServiceResponse<CourseOfferingWithRosterResponse> getCourseOfferingRoster(long id) {
        try{
            Optional<CourseOffering> courseOffering=persistenceService.findById(id);
            if(courseOffering.isEmpty()){
                return ServiceResponse.of(AppStatusCode.E40004,List.of("not.found","Course Offering"));
            }
            CourseOfferingWithRosterResponse response=new CourseOfferingWithRosterResponse();
            response =  ReportMapper.reportMapper.courseOfferingToRosterMapper(courseOffering.get());
            List<Student> studentList=studentCoursePersistence.findStudentByOfferingId(id);
            response.setStudents(studentList.stream()
                    .map(ReportMapper.reportMapper::studentToStudentReportResponse)
                    .collect(Collectors.toList()));
            return ServiceResponse.of(response, AppStatusCode.S20000);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ServiceResponse.of(AppStatusCode.E50002);

        }
    }
}
