package com.careerx.services;

import com.careerx.apirequests.ExploreCareerRequest;
import com.careerx.apiresponses.ExploreCareerResponse;

import java.util.List;

public interface ExploreCareerService {

    List<ExploreCareerResponse> getAllCareers();

    ExploreCareerResponse getCareerById(Long careerId);

    ExploreCareerResponse createCareer(ExploreCareerRequest request);

    ExploreCareerResponse updateCareer(Long careerId, ExploreCareerRequest request);

    void deleteCareer(Long careerId);

    List<ExploreCareerResponse> getCareersByJobSector(String sector);

    List<ExploreCareerResponse> searchCareers(String keyword);
}