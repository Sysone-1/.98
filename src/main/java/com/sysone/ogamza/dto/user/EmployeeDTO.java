package com.sysone.ogamza.dto.user;

import lombok.*;

/**
 * =============================================
 * 사원 데이터 전송 객체 (EmployeeDTO)
 * =============================================
 * - 사원 정보를 전달하기 위한 DTO 클래스
 * - id: 사원의 고유 식별자
 * - name: 사원 이름
 * - departmentId: 사원이 속한 부서의 ID
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * =============================================
 */
@Data                   // getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor      // 기본 생성자 생성
@AllArgsConstructor     // 모든 필드를 초기화하는 생성자 생성
public class EmployeeDTO {
    private int id;             // 사원 ID
    private String name;        // 사원 이름
    private int departmentId;   // 부서 ID
}
