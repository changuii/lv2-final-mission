package finalmission.restaurant.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import finalmission.fixture.TestFixture;
import finalmission.restaurant.dto.RestaurantRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("classpath:init.sql")
public class RestaurantApiTest {

    @LocalServerPort
    private int port;


    @Nested
    @DisplayName("식당 생성")
    class Create {

        @DisplayName("정상 식당 생성")
        @Test
        void create1() {
            final String email = "asd123@naver.com";
            final String password = "pass";

            TestFixture.createMember(email, password, port);
            final Header authHeader = TestFixture.createAuthHeader(email, password, port);

            final String restaurantName = "식당이름";
            final RestaurantRequest restaurantRequest = new RestaurantRequest(restaurantName);

            RestAssured.given()
                    .log().all()
                    .port(port)
                    .header(authHeader)
                    .contentType(ContentType.JSON)
                    .body(restaurantRequest)
                    .when().post("/restaurant")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .body("name", equalTo(restaurantName));
        }


    }

}
