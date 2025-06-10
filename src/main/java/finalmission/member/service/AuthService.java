package finalmission.member.service;

import finalmission.exception.CustomException;
import finalmission.member.domain.AuthTokenProvider;
import finalmission.member.domain.Member;
import finalmission.member.dto.AuthRequest;
import finalmission.member.dto.AuthResponse;
import finalmission.member.infrastructure.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberJpaRepository memberJpaRepository;
    private final AuthTokenProvider authTokenProvider;

    public AuthResponse login(final AuthRequest request){
        final Member member = memberJpaRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException("존재하지 않는 이메일입니다."));

        if(!member.matchPassword(request.password())){
            throw new CustomException("비밀번호가 일치하지 않습니다.");
        }

        final String token = authTokenProvider.generateToken(member.getEmail());
        return new AuthResponse(token);
    }

}
