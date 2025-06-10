package finalmission.config;

import finalmission.exception.CustomException;
import finalmission.member.infrastructure.JwtAuthTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class JwtMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory)
            throws Exception {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        final String jwt = request.getHeader("Authorization");

        if (jwt == null) {
            throw new CustomException("토큰이 존재하지 않습니다.");
        }
        if (!jwtAuthTokenProvider.isValidJwt(jwt)) {
            throw new CustomException("유효하지 않은 토큰입니다.");
        }

        return jwtAuthTokenProvider.extractSubject(jwt);
    }
}
