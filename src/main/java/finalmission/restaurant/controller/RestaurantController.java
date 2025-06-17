package finalmission.restaurant.controller;

import finalmission.config.AuthenticationPrincipal;
import finalmission.restaurant.dto.RestaurantRequest;
import finalmission.restaurant.dto.RestaurantResponse;
import finalmission.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(
            @AuthenticationPrincipal final String email,
            @RequestBody final RestaurantRequest request
    ) {
        final RestaurantResponse response = restaurantService.createRestaurant(request, email);
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(response);
    }


}
