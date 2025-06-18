package finalmission.member.service;

import finalmission.member.domain.AuthTokenProvider;
import finalmission.member.domain.Member;
import finalmission.member.dto.AuthRequest;
import finalmission.member.dto.AuthResponse;
import finalmission.member.exception.MemberNotExistsException;
import finalmission.member.exception.MemberPasswordMissMatchException;
import finalmission.member.infrastructure.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberJpaRepository memberJpaRepository;
    private final AuthTokenProvider authTokenProvider;

    public AuthResponse login(final AuthRequest request){
        final Member member = memberJpaRepository.findByEmail(request.email())
                .orElseThrow(MemberNotExistsException::new);

        if(!member.matchPassword(request.password())){
            throw new MemberPasswordMissMatchException();
        }

        final String token = authTokenProvider.generateToken(member.getEmail());
        return new AuthResponse(token);
    }

}
