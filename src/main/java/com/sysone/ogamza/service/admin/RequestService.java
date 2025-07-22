package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.ClockChangeRequestDAO;
import com.sysone.ogamza.dao.admin.OutworkRequestDAO;
import com.sysone.ogamza.dao.admin.ScheduleRequestDAO;
import com.sysone.ogamza.dao.admin.VacationRequestDAO;
import com.sysone.ogamza.enums.RequestType;
import com.sysone.ogamza.dto.admin.BaseRequestDTO;
import com.sysone.ogamza.utils.db.OracleConnector; // Oracle Cloud 연결용

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestService (Oracle Cloud 환경 완전 호환 버전)
 * - Oracle Cloud Wallet 연결 지원
 * - Java 8+ 호환 switch 구문
 * - DAO void 메서드 처리 로직
 * - 트랜잭션 관리 강화
 */
public class RequestService {

    private final VacationRequestDAO vacationDao = new VacationRequestDAO();
    private final ClockChangeRequestDAO clockDao = new ClockChangeRequestDAO();
    private final OutworkRequestDAO outworkDao = new OutworkRequestDAO();
    private final ScheduleRequestDAO dao = new ScheduleRequestDAO();

    /**
     * 요청 타입별 대기중인 건수 조회 (Java 8+ 호환)
     */
    public int getPendingCount(RequestType type) {
        try {
            switch (type) {
                case VACATION:
                    return vacationDao.countPendingRequests();
                case CLOCK_CHANGE:
                    return clockDao.countPendingRequests();
                case OUTWORK:
                    return outworkDao.countPendingRequests();
                default:
                    System.err.println("알 수 없는 요청 타입: " + type);
                    return 0;
            }
        } catch (Exception e) {
            System.err.println("대기 건수 조회 오류 (" + type + "): " + e.getMessage());
            return 0;
        }
    }

    /**
     * 요청 타입별 대기중인 목록 조회 (Java 8+ 호환)
     */
    public List<BaseRequestDTO> getPendingList(RequestType type) {
        try {
            switch (type) {
                case VACATION:
                    return new ArrayList<>(vacationDao.getPendingRequests());
                case CLOCK_CHANGE:
                    return new ArrayList<>(clockDao.getPendingRequests());
                case OUTWORK:
                    return new ArrayList<>(outworkDao.getPendingRequests());
                default:
                    System.err.println("알 수 없는 요청 타입: " + type);
                    return new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("대기 목록 조회 오류 (" + type + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 요청 타입별 승인·거절된 건수 조회 (Java 8+ 호환)
     */
    public int getCompletedCount(RequestType type) {
        try {
            switch (type) {
                case VACATION:
                    return vacationDao.countCompletedRequests();
                case CLOCK_CHANGE:
                    return clockDao.countCompletedRequests();
                case OUTWORK:
                    return outworkDao.countCompletedRequests();
                default:
                    System.err.println("알 수 없는 요청 타입: " + type);
                    return 0;
            }
        } catch (Exception e) {
            System.err.println("완료 건수 조회 오류 (" + type + "): " + e.getMessage());
            return 0;
        }
    }

    /**
     * 요청 타입별 승인·거절된 목록 조회 (Java 8+ 호환)
     */
    public List<BaseRequestDTO> getCompletedRequests(RequestType type) {
        try {
            switch (type) {
                case VACATION:
                    return new ArrayList<>(vacationDao.getCompletedRequests());
                case CLOCK_CHANGE:
                    return new ArrayList<>(clockDao.getCompletedRequests());
                case OUTWORK:
                    return new ArrayList<>(outworkDao.getCompletedRequests());
                default:
                    System.err.println("알 수 없는 요청 타입: " + type);
                    return new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("완료 목록 조회 오류 (" + type + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 모든 요청 타입의 승인·거절된 내역 통합 조회
     */
    public List<BaseRequestDTO> getAllCompletedRequests() {
        try {
            return dao.getAllCompletedList();
        } catch (Exception e) {
            System.err.println("전체 완료 목록 조회 오류: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 요청 상태 업데이트 (Oracle Cloud 환경 최적화)
     * DAO 메서드가 void 반환타입인 경우 예외 처리로 성공/실패 판단
     */
    public boolean updateRequestStatus(RequestType type, int requestId, String newStatus) {
        Connection connection = null;
        boolean success = false;

        try {
            System.out.println("상태 업데이트 시작 - Type: " + type + ", ID: " + requestId + ", Status: " + newStatus);

            // Oracle Cloud Connection 획득
            connection = getConnection();
            connection.setAutoCommit(false);

            // 타입별 업데이트 실행 (void 메서드 처리)
            switch (type) {
                case VACATION:
                    try {
                        vacationDao.updateRequestStatus(requestId, newStatus);
                        success = true; // 예외 없으면 성공으로 간주
                        System.out.println("휴가 요청 상태 업데이트 성공: ID=" + requestId);
                    } catch (Exception e) {
                        System.err.println("휴가 요청 상태 업데이트 실패: " + e.getMessage());
                        success = false;
                    }
                    break;

                case CLOCK_CHANGE:
                    try {
                        clockDao.updateRequestStatus(requestId, newStatus);
                        success = true;
                        System.out.println("출퇴근 변경 요청 상태 업데이트 성공: ID=" + requestId);
                    } catch (Exception e) {
                        System.err.println("출퇴근 변경 요청 상태 업데이트 실패: " + e.getMessage());
                        success = false;
                    }
                    break;

                case OUTWORK:
                    try {
                        outworkDao.updateRequestStatus(requestId, newStatus);
                        success = true;
                        System.out.println("출장 요청 상태 업데이트 성공: ID=" + requestId);
                    } catch (Exception e) {
                        System.err.println("출장 요청 상태 업데이트 실패: " + e.getMessage());
                        success = false;
                    }
                    break;

                default:
                    System.err.println("알 수 없는 요청 타입: " + type);
                    success = false;
                    break;
            }

            if (success) {
                connection.commit();
                System.out.println("✅ 상태 업데이트 성공 및 COMMIT 완료 - ID: " + requestId + ", Type: " + type);
            } else {
                connection.rollback();
                System.err.println("❌ 상태 업데이트 실패, ROLLBACK 수행 - ID: " + requestId + ", Type: " + type);
            }

        } catch (SQLException e) {
            System.err.println("상태 업데이트 중 SQL 오류: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("예외 발생으로 ROLLBACK 수행");
                } catch (SQLException rollbackEx) {
                    System.err.println("ROLLBACK 실패: " + rollbackEx.getMessage());
                }
            }
            success = false;
        } catch (Exception e) {
            System.err.println("상태 업데이트 중 일반 오류: " + e.getMessage());
            e.printStackTrace();
            success = false;
        } finally {
            // 리소스 정리
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // 자동 커밋 복원
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("커넥션 정리 실패: " + e.getMessage());
                }
            }
        }

        return success;
    }

    /**
     * 모든 요청 타입의 승인·거절된 총합을 반환
     */
    public int getAllCompletedCount() {
        try {
            return dao.getCompletedCount();
        } catch (Exception e) {
            System.err.println("전체 완료 건수 조회 오류: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Oracle Cloud DB 커넥션 가져오기 (Wallet 기반)
     */
    private Connection getConnection() throws SQLException {
        try {
            return OracleConnector.getConnection(); // Oracle Cloud Wallet 연결 사용
        } catch (SQLException e) {
            System.err.println("Oracle Cloud DB 연결 실패: " + e.getMessage());
            throw e;
        }
    }
}
