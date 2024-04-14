package com.mywork.rest.it;

import com.mywork.rest.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.OK;

//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderApplicationRestAssuredTest {

    private static final int PORT = 8080;

    @LocalServerPort
    private int port;

    private String uri;

    @BeforeEach
    public void init() {
        //uri = "http://localhost:" + PORT;
        uri = "http://localhost:" + port;
    }

    @Test
    public void getAllOrdersTest() {
        final String path = "/api/orders";
        get(uri + path).then().assertThat().statusCode(OK.value());
                given()
                .when()
                .get(path)
                .then()
                .extract()
                .response().then()
                .body("size()", greaterThan(0));
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (20, 'sam', 50.0, 4)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='20'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testOrderById() {
        final Order expectedOrder = new Order(20L, "sam", 50.0, 4);
        final String path = "/api/orders/" + expectedOrder.getId();
        get(path)
            .then()
            .assertThat().statusCode(OK.value())
            .body("id", equalTo(expectedOrder.getId().intValue()))
            .body("buyer", equalTo(expectedOrder.getBuyer()))
            .body("price", equalTo(expectedOrder.getPrice().floatValue()))
            .body("qty", equalTo(expectedOrder.getQty()));
    }
}
