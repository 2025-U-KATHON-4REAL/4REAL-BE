package com.team4real.demo.domain.auth.entity;

public enum Role {
    CREATOR, BRAND, ADMIN;

    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}
