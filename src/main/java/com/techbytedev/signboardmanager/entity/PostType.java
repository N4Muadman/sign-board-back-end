package com.techbytedev.signboardmanager.entity;

public enum PostType {
    news("news"),
    production_info("production_info"),
    project("project"),
    policy("policy");

    private final String value;

    PostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
