package com.vkr.tournament_service.client.matchmaking;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "matchmaking-service", url = "http://localhost:8081/")
public interface MatchMakingClient {
}
