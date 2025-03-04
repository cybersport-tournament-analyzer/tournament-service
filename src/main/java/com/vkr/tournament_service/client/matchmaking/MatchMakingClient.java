package com.vkr.tournament_service.client.matchmaking;


import com.vkr.tournament_service.entity.match.Match;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "matchmaking-service", url = "http://localhost:8081/")
public interface MatchMakingClient {
    @GetMapping("/match/{matchId}")
    Match getMatchById(@PathVariable String matchId);
}
