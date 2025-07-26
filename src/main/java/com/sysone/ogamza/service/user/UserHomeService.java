package com.sysone.ogamza.service.user;

import com.sysone.ogamza.dto.user.UserInfoDTO;
import com.sysone.ogamza.dao.user.UserHomeDAO;

/**
 * 유저 홈 화면에 필요한 정보를 제공하는 서비스 클래스입니다.
 *
 * <p>
 * 유저 ID를 기반으로 부서 정보, 프로필 사진 경로, 오늘의 행운 요소 등을 조회합니다.
 * </p>
 *
 * 예: 홈 화면 진입 시 사용자에게 맞춤 정보 출력
 * - 유저 이름, 부서명, 프로필 이미지
 * - 오늘의 행운 색상, 숫자, 모양
 * - GPT 메시지, 기분 이모지 등
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class UserHomeService {

    private static final UserHomeService instance = new UserHomeService();

    private UserHomeService() {}

    public static UserHomeService getInstance() {
        return instance;
    }

    /**
     * 주어진 유저 ID로 유저 홈 정보를 조회합니다.
     *
     * @param userId 유저 고유 ID
     * @return UserInfoDTO 유저 홈 정보 DTO
     * @throws RuntimeException 사용자를 찾지 못했거나 조회 실패 시
     */
    public UserInfoDTO getUserHomeInfo(int userId) {
        try {
            UserInfoDTO user = UserHomeDAO.getInstance().getUserHome(userId);
            if (user == null) {
                throw new RuntimeException("사용자를 찾을 수 없습니다");
            }
            System.out.println(user); // 디버깅용 출력
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
