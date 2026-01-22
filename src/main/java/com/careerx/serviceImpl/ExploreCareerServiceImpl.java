package com.careerx.serviceImpl;

import com.careerx.apirequests.ExploreCareerRequest;
import com.careerx.apiresponses.ExploreCareerResponse;
import com.careerx.entities.ExploreCareer;
import com.careerx.exception.ResourceNotFoundException;
import com.careerx.repository.ExploreCareerRepository;
import com.careerx.services.ExploreCareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExploreCareerServiceImpl implements ExploreCareerService {

    private final ExploreCareerRepository exploreCareerRepository;

    @Override
    public List<ExploreCareerResponse> getAllCareers() {
        return exploreCareerRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExploreCareerResponse getCareerById(Long careerId) {
        ExploreCareer career = exploreCareerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career not found with ID: " + careerId));
        return mapToResponse(career);
    }

    @Override
    public ExploreCareerResponse createCareer(ExploreCareerRequest request) {
        ExploreCareer career = new ExploreCareer();
        career.setTitle(request.getTitle());
        career.setDescription(request.getDescription());
        career.setImageUrl(request.getImageUrl());
        career.setRequiredEducation(request.getRequiredEducation());
        career.setSkillsRequired(request.getSkillsRequired());
        career.setJobSector(request.getJobSector());
        career.setAverageSalary(request.getAverageSalary());
        career.setCareerPath(request.getCareerPath());

        ExploreCareer savedCareer = exploreCareerRepository.save(career);
        return mapToResponse(savedCareer);
    }

    @Override
    public ExploreCareerResponse updateCareer(Long careerId, ExploreCareerRequest request) {
        ExploreCareer existingCareer = exploreCareerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career not found with ID: " + careerId));

        existingCareer.setTitle(request.getTitle());
        existingCareer.setDescription(request.getDescription());
        existingCareer.setImageUrl(request.getImageUrl());
        existingCareer.setRequiredEducation(request.getRequiredEducation());
        existingCareer.setSkillsRequired(request.getSkillsRequired());
        existingCareer.setJobSector(request.getJobSector());
        existingCareer.setAverageSalary(request.getAverageSalary());
        existingCareer.setCareerPath(request.getCareerPath());
        existingCareer.setUpdatedDate(LocalDateTime.now());

        ExploreCareer updatedCareer = exploreCareerRepository.save(existingCareer);
        return mapToResponse(updatedCareer);
    }

    @Override
    public void deleteCareer(Long careerId) {
        if (!exploreCareerRepository.existsById(careerId)) {
            throw new ResourceNotFoundException("Career not found with ID: " + careerId);
        }
        exploreCareerRepository.deleteById(careerId);
    }

    @Override
    public List<ExploreCareerResponse> getCareersByJobSector(String sector) {
        return exploreCareerRepository.findByJobSector(sector)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExploreCareerResponse> searchCareers(String keyword) {
        return exploreCareerRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExploreCareerResponse mapToResponse(ExploreCareer career) {
        ExploreCareerResponse response = new ExploreCareerResponse();
        response.setCareerId(career.getCareerId());
        response.setTitle(career.getTitle());
        response.setDescription(career.getDescription());
        response.setImageUrl(career.getImageUrl());
        response.setRequiredEducation(career.getRequiredEducation());
        response.setSkillsRequired(career.getSkillsRequired());
        response.setJobSector(career.getJobSector());
        response.setAverageSalary(career.getAverageSalary());
        response.setCareerPath(career.getCareerPath());
        response.setCreatedAt(career.getCreatedDate().atStartOfDay());
        response.setUpdatedAt(career.getUpdatedDate());
        return response;
    }
}