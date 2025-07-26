
package com.sysone.ogamza.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
/**
 * 사원의 일일 근태 기록 정보를 담는 DTO 클래스입니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
@Builder
@Data
public class UserRecordDTO {

    /** 사원 고유 ID */
    private int employeeId;

    /** 사원 이름 */
    private String name;

    /** 근무 일자 */
    private Date workDate;

    /** 출근 시각 */
    private String checkInTime;

    /** 퇴근 시각 */
    private String checkOutTime;

    /** 근태 상태 */
    private String workStatus;
}
