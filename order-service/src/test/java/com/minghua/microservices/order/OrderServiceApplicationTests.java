package com.minghua.microservices.order;

import com.minghua.microservices.order.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

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
	void testPlaceOrder() {
		String orderJson = """
				{
				    "skuCode": "iPhone_15",
				    "price": 1000,
				    "quantity": 1
				}
				""";

		InventoryClientStub.stubInventoryCallIsInStock("iPhone_15", 1);

		RestAssured.given()
				.contentType("application/json")
				.body(orderJson)
				.when()
				.post("/api/order")
				.then()
				.statusCode(201)
				.body(Matchers.equalTo("Order Placed Successfully"));
	}

	@Test
	void testFailurePlaceOrder() {
		String orderJson = """
				{
				    "skuCode": "iPhone_15",
				    "price": 1000,
				    "quantity": 101
				}
				""";

		InventoryClientStub.stubInventoryCallNegativeIsInStock("iPhone_15", 101);

		RestAssured.given()
				.contentType("application/json")
				.body(orderJson)
				.when()
				.post("/api/order")
				.then()
				.statusCode(400)
				.body(Matchers.equalTo("Product with SkuCode iPhone_15 is not enough in stock"));
	}
}
