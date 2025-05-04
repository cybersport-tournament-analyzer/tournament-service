package com.vkr.tournament_service.entity.player;

import com.vkr.tournament_service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InGameRole {
    LURKER("Lurker"),
    SUPPORT("Support"),
    IGL("In Game Leader"),
    RIFFLER("Riffler"),
    AWPER("AWPer");

    private final String roleName;

    @Override
    public String toString() {
        return roleName;
    }

    public static InGameRole fromString(String roleName) {
        for (InGameRole role : InGameRole.values()) {
            if (role.roleName.equals(roleName)) {
                return role;
            }
        }
        throw new EntityNotFoundException("No such status: " + roleName);
    }
}
