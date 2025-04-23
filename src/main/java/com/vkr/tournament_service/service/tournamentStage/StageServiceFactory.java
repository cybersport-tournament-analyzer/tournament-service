package com.vkr.tournament_service.service.tournamentStage;

import com.vkr.tournament_service.entity.tournamentStage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StageServiceFactory {
    private final Map<Stage, StageService> serviceMap;

    public StageServiceFactory(List<StageService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(StageService::getStageType, Function.identity()));
    }

    public StageService getService(Stage stageType) {
        StageService service = serviceMap.get(stageType);
        if (service == null) {
            throw new IllegalArgumentException("No StageService found for type: " + stageType);
        }
        return service;
    }
}
