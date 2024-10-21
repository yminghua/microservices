package com.minghua.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
	@ServiceConnection
	static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:7.0.5");
	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDbContainer.start();
	}

	@Test
	void testCreateProduct() {
		String requestBody = """
				{
					"name": "iPhone 15",
					"description": "iPhone 15 is a smartphone from Apple.",
					"price": 1000
				}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("iPhone 15"))
				.body("description", Matchers.equalTo("iPhone 15 is a smartphone from Apple."))
				.body("price", Matchers.equalTo(1000));
	}

	@Test
	void testGetAllProducts() {
		RestAssured.given()
				.get("/api/product")
				.then()
				.statusCode(200)
				.body("[0].id", Matchers.notNullValue())
				.body("[0].name", Matchers.equalTo("iPhone 15"))
				.body("[0].description", Matchers.equalTo("iPhone 15 is a smartphone from Apple."))
				.body("[0].price", Matchers.equalTo(1000));
	}
}
