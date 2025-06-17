package finalmission.restaurant.service;

import finalmission.exception.CustomException;
import finalmission.member.domain.Member;
import finalmission.member.infrastructure.MemberJpaRepository;
import finalmission.restaurant.domain.Restaurant;
import finalmission.restaurant.dto.RestaurantRequest;
import finalmission.restaurant.dto.RestaurantResponse;
import finalmission.restaurant.infrastructure.RestaurantJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    public RestaurantResponse createRestaurant(final RestaurantRequest request, final String email) {
        final Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버입니다."));

        final Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .member(member)
                .build();

        final Restaurant savedRestaurant = restaurantJpaRepository.save(restaurant);
        return RestaurantResponse.from(savedRestaurant);
    }

    public List<RestaurantResponse> findAll(){
        return restaurantJpaRepository.findAll()
                .stream()
                .map(RestaurantResponse::from)
                .toList();
    }
}
