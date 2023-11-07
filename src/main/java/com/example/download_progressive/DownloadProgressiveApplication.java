package com.example.download_progressive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableAsync
public class DownloadProgressiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(DownloadProgressiveApplication.class, args);
	}
	@GetMapping("/")
    public String home(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }

}
