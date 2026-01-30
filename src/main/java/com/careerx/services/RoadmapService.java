package com.careerx.services;

import com.careerx.entities.Roadmap;
import java.util.List;
import java.util.Map;

public interface RoadmapService {
    Map<String, Object> generateRoadmap(Long userId, Long paymentId);

    List<Map<String, Object>> getMyRoadmaps(Long userId);

    Map<String, Object> getRoadmap(Long userId, Long roadmapId);
}
