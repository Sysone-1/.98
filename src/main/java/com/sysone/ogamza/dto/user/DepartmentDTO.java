package com.sysone.ogamza.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 부서 정보를 담는 DTO 클래스입니다.
 * <p>
 * 부서 ID와 부서 이름 정보를 포함하며,
 * 주로 콤보박스 등에서 부서 목록을 표시할 때 사용됩니다.
 * {@link #toString()} 메서드를 오버라이딩하여 부서 이름이 출력되도록 설정되어 있습니다.
 *
 * @author 김민호
 */
@Data
@NoArgsConstructor
public class DepartmentDTO {
    private int id;
    private String name;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param id 부서 ID
     * @param name 부서 이름
     */
    public DepartmentDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 객체를 문자열로 표현할 때 부서 이름만 반환합니다.
     * 콤보박스 등에 표시될 때 유용하게 사용됩니다.
     *
     * @return 부서 이름
     */
    @Override
    public String toString() {
        return name;
    }
}
