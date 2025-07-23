package com.sysone.ogamza.dao.admin;

import com.sysone.ogamza.dto.admin.BaseRequestDTO;
import java.util.List;

/**
 * 공통 요청 DAO 인터페이스
 * @param <T> BaseRequest를 상속한 DTO 타입
 */
public interface RequestDAO<T extends BaseRequestDTO> {
    /** 대기 중 요청 건수 조회 */
    int countPendingRequests();
    /** 대기 중 요청 목록 조회 */
    List<T> getPendingRequests();
    /** 완료(승인·거절) 건수 조회 */
    int countCompletedRequests();
    /** 완료(승인·거절) 목록 조회 */
    List<T> getCompletedRequests();
    /** 요청 상태 업데이트 */
    void updateRequestStatus(int requestId, String newStatus);
}
