package com.careerx.controller;

import com.careerx.apirequests.ExploreCareerRequest;
import com.careerx.apiresponses.ApiResponse;
import com.careerx.apiresponses.ExploreCareerResponse;
import com.careerx.services.ExploreCareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/explore-career")
@RequiredArgsConstructor
public class ExploreCareerController {

    private final ExploreCareerService exploreCareerService;

    @GetMapping
    public ResponseEntity<?> getAllCareers() {
        try {
            List<ExploreCareerResponse> careers = exploreCareerService.getAllCareers();
            return ResponseEntity.ok(new ApiResponse<>("Careers fetched successfully", careers));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCareerById(@PathVariable Long id) {
        try {
            ExploreCareerResponse career = exploreCareerService.getCareerById(id);
            return ResponseEntity.ok(new ApiResponse<>("Career fetched successfully", career));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCareer(@Valid @RequestBody ExploreCareerRequest request) {
        try {
            ExploreCareerResponse career = exploreCareerService.createCareer(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Career created successfully", career));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCareer(@PathVariable Long id, @Valid @RequestBody ExploreCareerRequest request) {
        try {
            ExploreCareerResponse career = exploreCareerService.updateCareer(id, request);
            return ResponseEntity.ok(new ApiResponse<>("Career updated successfully", career));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCareer(@PathVariable Long id) {
        try {
            exploreCareerService.deleteCareer(id);
            return ResponseEntity.ok(new ApiResponse<>("Career deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<?> getCareersByJobSector(@PathVariable String sector) {
        try {
            List<ExploreCareerResponse> careers = exploreCareerService.getCareersByJobSector(sector);
            return ResponseEntity.ok(new ApiResponse<>("Careers fetched successfully", careers));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCareers(@RequestParam String keyword) {
        try {
            List<ExploreCareerResponse> careers = exploreCareerService.searchCareers(keyword);
            return ResponseEntity.ok(new ApiResponse<>("Search results fetched successfully", careers));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), "Failed"));
        }
    }
}