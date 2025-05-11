package com.vkr.tournament_service.repository.prediction;

import com.vkr.tournament_service.entity.prediction.BracketPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BracketPredictionRepository extends JpaRepository<BracketPrediction, Long> {

    Optional<BracketPrediction> findByUserIdAndStageId(String userId, UUID stageId);

    Optional<BracketPrediction> findById(UUID uuid);

    void deleteByUserIdAndStageId(String userId, UUID stageId);

    List<BracketPrediction> findAllByStageId(UUID stageId);
}

