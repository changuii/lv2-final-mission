package finalmission.member.dto;

public record AuthRequest(
        String email,
        String password
) {
}
