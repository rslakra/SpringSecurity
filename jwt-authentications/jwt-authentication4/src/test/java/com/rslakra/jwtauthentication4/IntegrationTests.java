package com.rslakra.jwtauthentication4;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rslakra.jwtauthentication4.domain.User;
import com.rslakra.jwtauthentication4.repository.UserRepository;
import com.rslakra.jwtauthentication4.repository.VehicleRepository;
import com.rslakra.jwtauthentication4.web.AuthenticationRequest;
import com.rslakra.jwtauthentication4.web.VehicleForm;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
class IntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setup() {
        RestAssured.port = this.port;
        
        // Ensure test user exists - delete and recreate to avoid conflicts
        // First delete any vehicles that reference this user to avoid foreign key constraint violations
        userRepository.findByUsername("user").ifPresent(user -> {
            // Delete vehicles that reference this user
            vehicleRepository.deleteAll();
            vehicleRepository.flush();
            // Now safe to delete the user
            userRepository.delete(user);
            userRepository.flush();
        });
        
        User testUser = User.builder()
            .username("user")
            .password(passwordEncoder.encode("password"))
            .roles(Arrays.asList("ROLE_USER"))
            .build();
        User savedUser = userRepository.saveAndFlush(testUser);
        
        log.debug("Created test user: {}, ID: {}, password encoded: {}", 
            savedUser.getUsername(), savedUser.getId(), savedUser.getPassword() != null);
        
        // Verify user exists in database and has an ID
        var foundUser = userRepository.findByUsername("user");
        if (foundUser.isPresent()) {
            log.debug("User found in DB: {}, ID: {}", foundUser.get().getUsername(), foundUser.get().getId());
        } else {
            log.error("User not found in database after save!");
        }
        
        // Try authentication with detailed logging
        try {
            var response = given()
                .contentType(ContentType.JSON)
                .body(AuthenticationRequest.builder().username("user").password("password").build())
                .when().post("/auth/signin");
            
            log.debug("Auth response status: {}, body: {}", response.getStatusCode(), response.getBody().asString());
            
            if (response.getStatusCode() == 200) {
                token = response.jsonPath().getString("token");
                log.debug("Got token: {}", token != null ? "success" : "null");
            } else {
                log.error("Authentication failed with status: {}, body: {}", response.getStatusCode(), response.getBody().asString());
                token = null;
            }
        } catch (Exception e) {
            log.error("Failed to get token: {}", e.getMessage(), e);
            token = null;
        }
    }

    @Test
    void getAllVehicles() throws Exception {
        //@formatter:off
        given()

            .accept(ContentType.JSON)

            .when()
            .get("/v1/vehicles")

            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
        //@formatter:on
    }

    @Test
    void testSave() throws Exception {
        //@formatter:off
        given()

            .contentType(ContentType.JSON)
            .body(VehicleForm.builder().name("test").build())

            .when()
            .post("/v1/vehicles")

            .then()
            .statusCode(401); // Unauthenticated request should return 401 (Unauthorized), not 403 (Forbidden)

        //@formatter:on
    }

    @Test
    void testSaveWithAuth() throws Exception {

        //@formatter:off
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(VehicleForm.builder().name("test").build())

            .when()
            .post("/v1/vehicles")

            .then()
            .statusCode(201);

        //@formatter:on
    }

    @Test
    @Disabled
    void testSaveWithInvalidAuth() throws Exception {

        //@formatter:off
        given()
            .header("Authorization", "Bearer " + "invalidtoken")
            .contentType(ContentType.JSON)
            .body(VehicleForm.builder().name("test").build())

            .when()
            .post("/v1/vehicles")

            .then()
            .statusCode(403);

        //@formatter:on
    }

}
