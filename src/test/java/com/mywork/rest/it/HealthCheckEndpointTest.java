package com.mywork.rest.it;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class HealthCheckEndpointTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void testHealthCheckEndpoint() {
        //final String url = "https://reqres.in/api/users?page=2";
        //Response response = get(url);
        //System.out.println("status: " + response.getStatusCode());
        given()
                .when()
                .get("/api/health")
                .then()
                .statusCode(200)
                .body(equalTo("OK"));
    }
}
