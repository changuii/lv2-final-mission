package finalmission.member.infrastructure;

import finalmission.exception.CustomException;
import finalmission.member.domain.NameGenerator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RandomNameGenerator implements NameGenerator {

    private final RestClient restClient;

    public RandomNameGenerator(
            final RestClient.Builder restClientBuilder,
            @Value("${api.random}") final String apiKey
    ) {
        restClient = restClientBuilder
                .baseUrl("https://randommer.io/api/Name?nameType=fullname&quantity=1")
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }

    @Override
    public String generateName() {
        return restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new CustomException("랜덤 이름 생성에 실패하였습니다.");
                })
                .toEntity(new ParameterizedTypeReference<List<String>>() {
                })
                .getBody()
                .getFirst();
    }
}
