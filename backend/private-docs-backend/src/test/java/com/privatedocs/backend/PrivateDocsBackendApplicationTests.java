package com.privatedocs.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "JWT_SECRET=VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdUZXN0U2VjcmV0S2V5")
class PrivateDocsBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
