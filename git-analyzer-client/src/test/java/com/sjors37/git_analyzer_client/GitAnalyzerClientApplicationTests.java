package com.sjors37.git_analyzer_client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.ai.mcp.client.enabled=false")
class GitAnalyzerClientApplicationTests {

	@Test
	void contextLoads() {
	}

}
