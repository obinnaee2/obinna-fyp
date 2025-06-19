package com.fyp.reconciliation_automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableAsync
@EnableScheduling // Enable scheduling for cron jobs
public class ReconciliationAutomation {
	private static final Logger log = LoggerFactory.getLogger(ReconciliationAutomation.class);
	private final WebClient webClient;

	public ReconciliationAutomation() {
		this.webClient = WebClient.create();
	}

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		SpringApplication.run(ReconciliationAutomation.class, args);
	}

	@PostConstruct
	public void printHeapSpaceOnStartup() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory() / (1024 * 1024); // Convert to MB
		long allocatedMemory = runtime.totalMemory() / (1024 * 1024); // Convert to MB
		long freeMemory = runtime.freeMemory() / (1024 * 1024); // Convert to MB
		long usedMemory = allocatedMemory - freeMemory;


		log.info("Heap Space Details on Startup:");
		log.info("Max Memory: {} MB", maxMemory);
		log.info("Allocated Memory: {} MB", allocatedMemory);
		log.info("Used Memory: {} MB", usedMemory);
		log.info("Free Memory: {} MB", freeMemory);
	}

	// Cron job to ping the server every 5 minutes
	@Scheduled(fixedRate = 300000) // 300,000 ms = 5 minutes
	public void keepServerAlive() {
		log.info("Pinging server to keep it alive...");
		webClient.get()
				.uri("https://obinna-fyp.onrender.com/api/index")
				.retrieve()
				.bodyToMono(String.class)
				.subscribe(
						response -> log.info("Ping successful: {}", response),
						error -> log.error("Ping failed: {}", error.getMessage())
				);
	}
}