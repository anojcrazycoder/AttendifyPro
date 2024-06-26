package edu.miu.attendifypro.service.persistence.impl;

import edu.miu.attendifypro.domain.Student;
import edu.miu.attendifypro.domain.StudentCourseSelection;
import edu.miu.attendifypro.repository.StudentCourseSelectionRepository;
import edu.miu.attendifypro.service.persistence.StudentCourseSelectionPersistenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StudentCourseSelectionPersistenceServiceImpl implements StudentCourseSelectionPersistenceService {

    private final StudentCourseSelectionRepository studentCourseSelectionRepository;


    public StudentCourseSelectionPersistenceServiceImpl(StudentCourseSelectionRepository studentCourseSelectionRepository) {
        this.studentCourseSelectionRepository = studentCourseSelectionRepository;
    }

    @Override
    public List<StudentCourseSelection> getOfferingStartingInNDays(int n) {
        LocalDate targetDate = LocalDate.now().minusDays(n);
        return studentCourseSelectionRepository.getOfferingStartingInNDays(targetDate);
    }

    @Override
    public List<Student> findStudentByOfferingId(long id) {
        return studentCourseSelectionRepository.findStudentsByCouseOfferingId(id);
    }

    @Override
    public StudentCourseSelection save(StudentCourseSelection courseSelection) {
        return studentCourseSelectionRepository.save(courseSelection);
    }

    public List<StudentCourseSelection> findByStudentIdAndCourseOfferingId(String studentId, long courseOfferingId){
        return studentCourseSelectionRepository.findByStudentIdAndCourseOfferingId(studentId,courseOfferingId);
    }

    @Override
    public List<StudentCourseSelection> findByStudentId(String studentId) {
        return studentCourseSelectionRepository.findByStudentId(studentId);
    }
}
