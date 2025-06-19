package com.fyp.reconciliation_automation.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    private final HttpClient httpClient;

    public EmailService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void sendEmail(String subject, String message, String recipientsEmail) {
        String jsonBody = String.format(
                "{ \"to\": [\"%s\"], \"message\": \"%s\", \"text\": \"\", \"subject\": \"%s\", \"sender\": \"reconciliation@payaza.africa\" }",
                recipientsEmail, message, subject
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://adpyd92v06.execute-api.eu-west-2.amazonaws.com/dev/send-email-without-template"))
                .header("Content-Type", "application/json")
                .header("x-api-key", "YpwQhnT4MM3HdGncYcu0C2w1Yh8p4O76aoBiXx3q")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        responseFuture.thenAccept(response -> {
            System.out.println("Error or Started Email sent successfully. Response: " + response.body());
        }).exceptionally(error -> {
            System.out.println("Failed to send Error or Started Email: " + error.getMessage());
            return null;
        });
    }
    void sendCompletedEmails(String subject, String message, String recipientsEmail) {
        String jsonBody = String.format(
                "{ \"to\": [\"%s\"], \"message\": \"%s\", \"text\": \"\", \"subject\": \"%s\", \"sender\": \"reconciliation@payaza.africa\" }",
                recipientsEmail, message, subject
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://4lrgr19qb2.execute-api.eu-west-2.amazonaws.com/live/send-email-without-template"))
                .header("Content-Type", "application/json")
                .header("x-api-key", "yRWTlNCmDoacSTIJS3BcLM6kAQ9jL5E9TOqXdjld")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        responseFuture.thenAccept(response -> {
            System.out.println("Successful Email sent successfully. Response: " + response.body());
        }).exceptionally(error -> {
            System.out.println("Failed to send Successful email: " + error.getMessage());
            return null;
        });
    }
}
