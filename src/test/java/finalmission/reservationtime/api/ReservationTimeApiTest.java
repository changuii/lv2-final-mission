package finalmission.reservationtime.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import finalmission.fixture.TestFixture;
import finalmission.reservationtime.dto.ReservationTimeRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.time.LocalTime;
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
public class ReservationTimeApiTest {

    @LocalServerPort
    private int port;


    @Nested
    @DisplayName("예약 시간 생성")
    class Create {

        @DisplayName("예약 시간 정상 생성")
        @Test
        void create1() {
            final String email = "asd@naver.com";
            final String password = "1234";

            TestFixture.createMember(email, password, port);
            final Header authHeader = TestFixture.createAuthHeader(email, password, port);

            final Long restaurantId = TestFixture.createRestaurant(authHeader, "내식당", port);
            final LocalTime time = LocalTime.of(12, 30);
            final ReservationTimeRequest request = new ReservationTimeRequest(restaurantId, time);

            RestAssured
                    .given()
                    .log().all()
                    .header(authHeader)
                    .port(port)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/reservation-time")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .body("time", equalTo(time.toString()));
        }

        @DisplayName("해당 식당에 이미 존재하는 시간이라면 400을 응답한다.")
        @Test
        void create2() {
            final String email = "asd@naver.com";
            final String password = "1234";

            TestFixture.createMember(email, password, port);
            final Header authHeader = TestFixture.createAuthHeader(email, password, port);

            final Long restaurantId = TestFixture.createRestaurant(authHeader, "내식당", port);
            final LocalTime time = LocalTime.of(12, 30);
            final ReservationTimeRequest request = new ReservationTimeRequest(restaurantId, time);
            TestFixture.createReservationTime(authHeader, restaurantId, time, port);

            RestAssured
                    .given()
                    .log().all()
                    .header(authHeader)
                    .port(port)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/reservation-time")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @DisplayName("해당 식당의 주인이 아니라면 400을 응답한다.")
        @Test
        void create3() {
            final String email = "asd@naver.com";
            final String password = "1234";

            TestFixture.createMember(email, password, port);
            final Header authHeader = TestFixture.createAuthHeader(email, password, port);

            final Long restaurantId = TestFixture.createRestaurant(authHeader, "내식당", port);
            final LocalTime time = LocalTime.of(12, 30);
            final ReservationTimeRequest request = new ReservationTimeRequest(restaurantId, time);

            final String anotherEmail = "asd1234@naver.com";
            TestFixture.createMember(anotherEmail, password, port);
            final Header anotherAuthHeader = TestFixture.createAuthHeader(anotherEmail, password, port);

            RestAssured
                    .given()
                    .log().all()
                    .header(anotherAuthHeader)
                    .port(port)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/reservation-time")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
