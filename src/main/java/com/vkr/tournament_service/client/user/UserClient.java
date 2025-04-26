package com.vkr.tournament_service.client.user;

import com.vkr.tournament_service.dto.user.GetAverageRatingByIdsDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "http://77.221.158.197:8080/")
public interface UserClient {
    @PostMapping("/users/averageEloRating")
    int getAverageRatingByIds(@RequestBody GetAverageRatingByIdsDto dto);
}