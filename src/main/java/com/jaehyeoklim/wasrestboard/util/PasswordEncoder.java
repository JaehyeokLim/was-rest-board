package main.java.com.jaehyeoklim.wasrestboard.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt 기반 비밀번호 인코더 유틸 클래스.
 *
 * <p>- 비밀번호 해시: 내부적으로 salt 자동 생성
 * - 비밀번호 비교: 입력값과 해시값 비교
 * - 정적 메서드 기반, 상태 없음</p>
 *
 * <p>사용 예:
 * String hash = PasswordEncoder.hash("mypw");
 * boolean match = PasswordEncoder.matches("mypw", hash);</p>
 */
public final class PasswordEncoder {

    private PasswordEncoder() {}

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
