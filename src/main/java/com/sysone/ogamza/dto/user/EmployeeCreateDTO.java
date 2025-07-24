package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사원 등록에 사용되는 DTO 클래스입니다.
 * <p>
 * 부서 ID, 이름, 이메일, 직급, 전화번호, 비밀번호, 사진 경로, 카드 ID, 연차 개수 등의 정보를 담고 있으며,
 * 사원 등록, 수정, 삭제 시 사용됩니다.
 *
 * @author 김민호
 */
@Data
@NoArgsConstructor
public class EmployeeCreateDTO {
    private int departmentId;
    private String name;
    private String email;
    private String position;
    private String telNum;
    private String password;
    private String picDir;
    private byte[] cardId;
    private int vacNum;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param departmentId 부서 ID
     * @param name 사원 이름
     * @param email 사원 이메일
     * @param position 직급
     * @param telNum 전화번호
     * @param password 비밀번호
     * @param picDir 사진 경로
     * @param cardId 카드 ID
     * @param vacNum 연차 개수
     */
    public EmployeeCreateDTO(int departmentId, String name, String email, String position,
                             String telNum, String password, String picDir, byte[] cardId, int vacNum) {
        this.departmentId = departmentId;
        this.name = name;
        this.email = email;
        this.position = position;
        this.telNum = telNum;
        this.password = password;
        this.picDir = picDir;
        this.cardId = cardId;
        this.vacNum = vacNum;
    }
}
