package com.minghua.microservices.inventory;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.3.0");
	@LocalServerPort
	private Integer port;

	static {
		mysqlContainer.start();
	}

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void testIsInStock() {
		var response = RestAssured.given()
				.when()
				.get("/api/inventory?skuCode=iphone_15&quantity=1")
				.then()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertTrue(response);

		var negativeResponse = RestAssured.given()
				.when()
				.get("/api/inventory?skuCode=iphone_15&quantity=101")
				.then()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertFalse(negativeResponse);
	}
}
