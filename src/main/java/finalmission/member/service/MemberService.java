package finalmission.member.service;

import finalmission.exception.CustomException;
import finalmission.member.domain.Member;
import finalmission.member.dto.MemberRequest;
import finalmission.member.dto.MemberResponse;
import finalmission.member.infrastructure.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;

    public MemberResponse createMember(final MemberRequest request) {
        if (memberJpaRepository.existsByEmail(request.email())) {
            throw new CustomException("이미 존재하는 이메일입니다.");
        }

        final Member notSavedMember = new Member(request.email(), request.password());
        final Member savedMember = memberJpaRepository.save(notSavedMember);
        return MemberResponse.from(savedMember);
    }
}
