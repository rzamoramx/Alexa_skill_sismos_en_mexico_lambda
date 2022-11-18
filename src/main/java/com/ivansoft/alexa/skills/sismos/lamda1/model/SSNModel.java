package com.ivansoft.alexa.skills.sismos.lamda1.model;

public class SSNModel {
    final String title;
    final String description;

    public SSNModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
