package com.licoflix;

import com.licoflix.core.service.rest.RestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private RestService restService;

	@Test
	void contextLoads() {
	}

}