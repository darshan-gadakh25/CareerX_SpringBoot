package com.careerx.serviceImpl;


import com.careerx.apirequests.StudentProfileRequest;
import com.careerx.apiresponses.AchievementsAndCertifications;
import com.careerx.apiresponses.CareerInterests;
import com.careerx.apiresponses.EducationDetails;
import com.careerx.apiresponses.EntranceExams;
import com.careerx.apiresponses.GeneralInformation;
import com.careerx.apiresponses.HobbiesAndInterests;
import com.careerx.apiresponses.SkillsAndStrengths;
import com.careerx.apiresponses.StudentProfileResponse;
import com.careerx.entities.StudentProfile;
import com.careerx.entities.Users;
import com.careerx.exception.ResourceNotFoundException;
import com.careerx.repository.StudentProfileRepository;
import com.careerx.repository.UserRepository;
import com.careerx.services.StudentProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public StudentProfileServiceImpl(StudentProfileRepository studentProfileRepository,
                                     UserRepository userRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public StudentProfileResponse createStudentProfile(Long userId, StudentProfileRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        studentProfileRepository.findByUserId(userId).ifPresent(p -> {
            throw new RuntimeException("Student profile already exists for user ID " + userId);
        });

        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);

        mapRequestToEntity(profile, request);
        profile.setCreatedAt(LocalDateTime.now());

        studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    public StudentProfileResponse getStudentProfile(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID " + userId));

        return mapToResponse(profile);
    }

    @Override
    public StudentProfileResponse getStudentProfileById(Long studentId) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for student ID " + studentId));

        return mapToResponse(profile);
    }

    @Override
    public StudentProfileResponse updateStudentProfile(Long userId, StudentProfileRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID " + userId));

        mapRequestToEntity(profile, request);
        profile.setUpdatedAt(LocalDateTime.now());

        studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    public boolean deleteStudentProfile(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElse(null);
        if (profile == null) return false;

        studentProfileRepository.delete(profile);
        return true;
    }

    // 🔁 Mapping Methods

    private void mapRequestToEntity(StudentProfile profile, StudentProfileRequest request) {

        // General Info
        profile.setDateOfBirth(request.getGeneralInformation().getDateOfBirth());
        profile.setGender(request.getGeneralInformation().getGender());
        profile.setMobileNumber(request.getGeneralInformation().getMobileNumber());
        profile.setPreferredLanguage(request.getGeneralInformation().getPreferredLanguage());

        // Education
        profile.setCurrentEducationLevel(request.getEducationDetails().getCurrentEducationLevel());
        profile.setBoardOrUniversity(request.getEducationDetails().getBoardOrUniversity());
        profile.setSchoolOrCollegeName(request.getEducationDetails().getSchoolOrCollegeName());
        profile.setStream(request.getEducationDetails().getStreams());
        profile.setCurrentYearOrSemester(request.getEducationDetails().getCurrentYearOrSemester());
        profile.setOverallPercentageOrCGPA(request.getEducationDetails().getOverallPercentageOrCGPA());
        profile.setHasTakenGapYear(request.getEducationDetails().getHasTakenGapYear());
        profile.setNumberOfGapYears(request.getEducationDetails().getNumberOfGapYears());
        profile.setReasonForGap(request.getEducationDetails().getReasonForGap());

        // Career
        if (request.getCareerInterests() != null) {
            profile.setAreasOfInterest(request.getCareerInterests().getAreasOfInterest());
            profile.setPreferredCareerDomain(request.getCareerInterests().getPreferredCareerDomain());
            profile.setDreamJobOrRole(request.getCareerInterests().getDreamJobOrRole());
        }

        // Skills
        if (request.getSkillsAndStrengths() != null) {
            profile.setTechnicalSkills(request.getSkillsAndStrengths().getTechnicalSkills());
            profile.setSoftSkills(request.getSkillsAndStrengths().getSoftSkills());
            profile.setSkillLevel(request.getSkillsAndStrengths().getSkillLevel());
        }

        // Hobbies
        if (request.getHobbiesAndInterests() != null) {
            profile.setHobbies(request.getHobbiesAndInterests().getHobbies());
            profile.setExtracurricularActivities(request.getHobbiesAndInterests().getExtracurricularActivities());
        }

        // Achievements
        if (request.getAchievementsAndCertifications() != null) {
            profile.setAcademicAchievements(request.getAchievementsAndCertifications().getAcademicAchievements());
            profile.setScholarships(request.getAchievementsAndCertifications().getScholarships());
            profile.setRankOrMeritCertificates(request.getAchievementsAndCertifications().getRankOrMeritCertificates());
            profile.setCompetitionsOrHackathons(request.getAchievementsAndCertifications().getCompetitionsOrHackathons());
            profile.setSportsOrCulturalAchievements(request.getAchievementsAndCertifications().getSportsOrCulturalAchievements());
            profile.setCertifications(request.getAchievementsAndCertifications().getCertifications());
            profile.setCertificationCourseName(request.getAchievementsAndCertifications().getCertificationCourseName());
            profile.setCertificationPlatform(request.getAchievementsAndCertifications().getCertificationPlatform());
            profile.setCertificationYear(request.getAchievementsAndCertifications().getCertificationYear());
        }

        // Entrance Exams
        if (request.getEntranceExams() != null) {
            profile.setAppearedForCompetitiveExams(request.getEntranceExams().getAppearedForCompetitiveExams());
            profile.setExamName(request.getEntranceExams().getExamName());
            profile.setExamScoreOrRank(request.getEntranceExams().getExamScoreOrRank());
            profile.setExamYear(request.getEntranceExams().getExamYear());
        }
    }

    private StudentProfileResponse mapToResponse(StudentProfile profile) {

        StudentProfileResponse response = new StudentProfileResponse();
        response.setStudentId(profile.getStudentId());
        response.setUserId(profile.getUserId());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        // General Information
        GeneralInformation general = new GeneralInformation();
        general.setName(profile.getUser() != null ? profile.getUser().getName() : "");
        general.setEmail(profile.getUser() != null ? profile.getUser().getEmail() : "");
        general.setLocation(profile.getUser() != null ? profile.getUser().getLocation() : "");
        general.setAge(profile.getUser() != null ? profile.getUser().getAge() : 0);
        general.setDateOfBirth(profile.getDateOfBirth());
        general.setGender(profile.getGender());
        general.setMobileNumber(profile.getMobileNumber());
        general.setPreferredLanguage(profile.getPreferredLanguage());
        response.setGeneralInformation(general);

        // Education
        EducationDetails edu = new EducationDetails();
        edu.setCurrentEducationLevel(profile.getCurrentEducationLevel());
        edu.setBoardOrUniversity(profile.getBoardOrUniversity());
        edu.setSchoolOrCollegeName(profile.getSchoolOrCollegeName());
        edu.setStream(profile.getStream());
        edu.setCurrentYearOrSemester(profile.getCurrentYearOrSemester());
        edu.setOverallPercentageOrCGPA(profile.getOverallPercentageOrCGPA());
        edu.setHasTakenGapYear(profile.getHasTakenGapYear());
        edu.setNumberOfGapYears(profile.getNumberOfGapYears());
        edu.setReasonForGap(profile.getReasonForGap());
        response.setEducationDetails(edu);

        // Career
        CareerInterests career = new CareerInterests();
        career.setAreasOfInterest(profile.getAreasOfInterest());
        career.setPreferredCareerDomain(profile.getPreferredCareerDomain());
        career.setDreamJobOrRole(profile.getDreamJobOrRole());
        response.setCareerInterests(career);

        // Skills
        SkillsAndStrengths skills = new SkillsAndStrengths();
        skills.setTechnicalSkills(profile.getTechnicalSkills());
        skills.setSoftSkills(profile.getSoftSkills());
        skills.setSkillLevel(profile.getSkillLevel());
        response.setSkillsAndStrengths(skills);

        // Hobbies
        HobbiesAndInterests hobbies = new HobbiesAndInterests();
        hobbies.setHobbies(profile.getHobbies());
        hobbies.setExtracurricularActivities(profile.getExtracurricularActivities());
        response.setHobbiesAndInterests(hobbies);

        // Achievements
        AchievementsAndCertifications achievements = new AchievementsAndCertifications();
        achievements.setAcademicAchievements(profile.getAcademicAchievements());
        achievements.setScholarships(profile.getScholarships());
        achievements.setRankOrMeritCertificates(profile.getRankOrMeritCertificates());
        achievements.setCompetitionsOrHackathons(profile.getCompetitionsOrHackathons());
        achievements.setSportsOrCulturalAchievements(profile.getSportsOrCulturalAchievements());
        achievements.setCertifications(profile.getCertifications());
        achievements.setCertificationCourseName(profile.getCertificationCourseName());
        achievements.setCertificationPlatform(profile.getCertificationPlatform());
        achievements.setCertificationYear(profile.getCertificationYear());
        response.setAchievementsAndCertifications(achievements);

        // Entrance Exams
        EntranceExams exams = new EntranceExams();
        exams.setAppearedForCompetitiveExams(profile.getAppearedForCompetitiveExams());
        exams.setExamName(profile.getExamName());
        exams.setExamScoreOrRank(profile.getExamScoreOrRank());
        exams.setExamYear(profile.getExamYear());
        response.setEntranceExams(exams);

        return response;
    }
}
