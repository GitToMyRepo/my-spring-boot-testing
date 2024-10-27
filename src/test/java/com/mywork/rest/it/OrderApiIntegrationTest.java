package com.mywork.rest.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mywork.rest.repositories.OrderRepository;
import com.mywork.rest.models.Order;
import com.mywork.rest.services.OrderService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebClient webClient; // WebClient instance for reactive testing

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    public void setUp() {
        // Initialize WebClient with the base URL
        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port + "/api")
                .build();
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (22, 'john', 24.0, 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='22'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testOrdersList() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Order>> response = restTemplate.exchange(
                createURLWithPort(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Order>>(){});
        List<Order> orderList = response.getBody();
        assert orderList != null;
        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(orderList.size(), orderService.getOrders().size());
        assertEquals(orderList.size(), orderRepository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (23, 'mike', 40.0, 2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='23'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testOrdersListUsingFluxToObtainTheResultOfAnAsynchronousComputationInASynchronousManner() {

        Flux<Order> orderFlux = webClient.get()
                .uri("/orders")
                .retrieve()
                .bodyToFlux(Order.class);

        List<Order> orderList = orderFlux.collectList().block(); // Block to get the result

        assertNotNull(orderList);
        assertEquals(HttpStatus.OK.value(), 200); // The status code should be OK
        assertEquals(orderList.size(), orderService.getOrders().size());
        assertEquals(orderList.size(), orderRepository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (22, 'john', 24.0, 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='22'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testOrdersListAsynchronously() {
        // Create a WebClient instance
        WebClient webClient = WebClient.create(createURLWithPort());

        // Use WebClient to perform a non-blocking call to get the orders
        Flux<Order> orderFlux = webClient.get()
                .uri("/orders")
                .retrieve()
                .bodyToFlux(Order.class);

        // Collect the results into a List and validate
        orderFlux.collectList().subscribe(orderList -> {
            assertNotNull(orderList);
            assertEquals(HttpStatus.OK.value(), 200); // The status code should be OK
            assertEquals(orderList.size(), orderService.getOrders().size());
            assertEquals(orderList.size(), orderRepository.findAll().size());
            assertEquals(1, orderList.size()); // Expecting 1 order
            assertEquals("john", orderList.get(0).getBuyer());
            assertEquals(24.0, orderList.get(0).getPrice());
            assertEquals(1, orderList.get(0).getQty());
        });
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (20, 'sam', 50.0, 4)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='20'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testOrderById() throws JsonProcessingException {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Order> response = restTemplate.exchange(
                (createURLWithPort() + "/20"), HttpMethod.GET, entity, Order.class);
        Order orderRes = response.getBody();
        String expected = "{\"id\":20,\"buyer\":\"sam\",\"price\":50.0,\"qty\":4}";
        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(expected, objectMapper.writeValueAsString(orderRes));
        assert orderRes != null;
        assertEquals(orderRes, orderService.getOrderById(20L));
        assertEquals(orderRes.getBuyer(), orderService.getOrderById(20L).getBuyer());
        assertEquals(orderRes, orderRepository.findById(20L).orElse(null));
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (21, 'john', 60.0, 2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='21'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetOrderByIdAsMono() {
        Mono<ResponseEntity<Order>> orderMono = webClient.get()
                .uri("/orders/{id}", 21)
                .retrieve()
                .toEntity(Order.class);

        // Subscribe to Mono to get the result
        ResponseEntity<Order> orderResponse = orderMono.block(); // Block here for test purposes

        assertNotNull(orderResponse);
        assertEquals(HttpStatus.OK, orderResponse.getStatusCode());

        Order orderRes = orderResponse.getBody();
        assertNotNull(orderRes);
        assertEquals(21, orderRes.getId());
        assertEquals("john", orderRes.getBuyer());
        assertEquals(60.0, orderRes.getPrice());
        assertEquals(2, orderRes.getQty());
    }

    @Test
    @Sql(statements = "DELETE FROM orders WHERE id='3'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateOrder() throws JsonProcessingException {
        Order order = new Order(3L, "peter", 30.0, 3);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(order), headers);
        ResponseEntity<Order> response = restTemplate.exchange(
                createURLWithPort(), HttpMethod.POST, entity, Order.class);
        assertEquals(response.getStatusCodeValue(), 201);
        Order orderRes = Objects.requireNonNull(response.getBody());
        assertEquals(orderRes.getBuyer(), "peter");
        assertEquals(orderRes.getBuyer(), orderRepository.save(order).getBuyer());
    }

    @Test
    @Sql(statements = "INSERT INTO orders(id, buyer, price, qty) VALUES (6, 'alex', 75.0, 3)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM orders WHERE id='6'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteOrder() {
        ResponseEntity<String> response = restTemplate.exchange(
                (createURLWithPort() + "/6"), HttpMethod.DELETE, null, String.class);
        String orderRes = response.getBody();
        assertEquals(response.getStatusCodeValue(), 200);
        assertNotNull(orderRes);
        assertEquals(orderRes, "Order deleted - Order ID:6");
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/orders";
    }

}
