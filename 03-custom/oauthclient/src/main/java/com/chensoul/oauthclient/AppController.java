package com.chensoul.socialloginclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AppController {

	private final HelloClient helloClient;
	@Autowired
	private AppService appService;

	public AppController(HelloClient helloClient) {
		this.helloClient = helloClient;
	}

	@GetMapping(value = "/", produces = "text/html")
	public ResponseEntity<String> getPublicData() {
		return ResponseEntity.ok("<h1>Public data</h1>");
	}

	@GetMapping("/private-data")
	public ResponseEntity<String> getPrivateData() {
		return ResponseEntity.ok(appService.getJwtToken());
	}

	@GetMapping(value = "/hello", produces = "text/html")
	public ResponseEntity<String> sayHello() {
		return ResponseEntity.ok(
			"""
				    <h1>
				        %s
				    </h1>
				""".formatted(helloClient.getHello())
		);
	}
}
