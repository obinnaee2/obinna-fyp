package com.fyp.reconciliation_automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableAsync
public class ReconciliationAutomation {
	private static final Logger log = LoggerFactory.getLogger(ReconciliationAutomation.class);

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
}