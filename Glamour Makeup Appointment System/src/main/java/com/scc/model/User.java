package com.scc.model;

public record User(int id, String name, String username, String password, String role, String status) {
    @Override
    public String toString(){
        return String.format("[%d] %s (%s) - Status: %s", id, name, username, password, role, status);

    }
}
