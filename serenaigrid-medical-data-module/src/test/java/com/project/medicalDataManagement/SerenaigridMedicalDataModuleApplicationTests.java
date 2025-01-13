package com.project.medicalDataManagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
class SerenaigridMedicalDataModuleApplicationTests {

	@Test
	void contextLoads() {
	}

}
