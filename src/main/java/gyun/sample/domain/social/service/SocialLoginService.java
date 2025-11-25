package gyun.sample.domain.social.service;

import gyun.sample.domain.account.payload.response.AccountLoginResponse;
import gyun.sample.domain.member.enums.MemberType;

/**
 * 소셜 로그인 처리 공통 인터페이스
 */
public interface SocialLoginService {

    /**
     * 이 서비스가 지원하는 소셜 타입 반환
     */
    MemberType getSupportedType();

    /**
     * 소셜 로그인 시작 URL 생성
     */
    String getLoginRedirectUrl();

    /**
     * 인증 코드를 이용해 로그인 처리 (토큰 발급 및 회원가입/로그인)
     *
     * @param code 인증 코드
     * @return JWT 토큰 응답
     */
    AccountLoginResponse login(String code);

}