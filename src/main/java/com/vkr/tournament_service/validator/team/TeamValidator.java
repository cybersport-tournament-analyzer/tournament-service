package com.vkr.tournament_service.validator.team;

import java.util.UUID;

public interface TeamValidator {
    void validateAccess(UUID teamId, String userId);
}
