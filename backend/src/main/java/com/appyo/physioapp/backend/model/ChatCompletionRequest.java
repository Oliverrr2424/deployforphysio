package com.appyo.physioapp.backend.model;

import java.util.List;

public class ChatCompletionRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
    private boolean stream;

    public ChatCompletionRequest(String model, List<Message> messages, double temperature, boolean stream) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.stream = stream;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }
} 