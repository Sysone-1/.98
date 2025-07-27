package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.AlarmSettingDTO;
import com.sysone.ogamza.sql.user.AlarmSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * ===========================================
 * 알림 설정 DAO 클래스 (AlarmDAO)
 * ===========================================
 * - DB에서 사용자 알림 설정을 조회하거나 저장/업데이트하는 역할
 * - PL/SQL을 활용해 UPSERT(존재 시 UPDATE, 없으면 INSERT) 구현
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ===========================================
 */


public class AlarmDAO {

    /**
     * ▶ 특정 사용자 ID로 알림 설정 조회
     *
     * @param id 사용자 ID
     * @return AlarmSettingDTO 알림 설정 DTO (없으면 null)
     */
    public AlarmSettingDTO findByUserId(int id) {
        String sql = AlarmSQL.SELECT_ALARM;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // ID 바인딩
            pstmt.setInt(1, id);

            // 쿼리 실행
            ResultSet rs = pstmt.executeQuery();

            // 결과가 있으면 DTO로 반환
            if (rs.next()) {
                return new AlarmSettingDTO(
                        id,
                        rs.getInt("alarm_1"),
                        rs.getInt("alarm_2"),
                        rs.getInt("alarm_3")
                );
            }

        } catch (SQLException e) {
            // 예외 발생 시 런타임 예외로 래핑
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * ▶ 알림 설정 저장 또는 수정 (PL/SQL 활용)
     * - 시간(minutesBefore)에 따라 알림 위치(3분, 5분, 10분 전) 설정
     * - 사용자가 존재하면 UPDATE, 없으면 INSERT 처리
     *
     * @param id 사용자 ID
     * @param minutesBefore 알림 시간 (3, 5, 10 중 하나)
     */
    public void saveOrUpdate(int id, int minutesBefore) {
        // 알림 시간에 따른 값 설정
        int a1 = 0, a2 = 0, a3 = 0;
        switch (minutesBefore) {
            case 3 -> a1 = 1;
            case 5 -> a2 = 1;
            case 10 -> a3 = 1;
        }

        String sql = AlarmSQL.UPSERT_ALARM_PL_SQL;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // PL/SQL 내 순차적으로 바인딩될 9개의 파라미터 설정
            pstmt.setInt(1, id);   // SELECT COUNT(*) WHERE id = ?
            pstmt.setInt(2, a1);   // UPDATE alarm_1
            pstmt.setInt(3, a2);   // UPDATE alarm_2
            pstmt.setInt(4, a3);   // UPDATE alarm_3
            pstmt.setInt(5, id);   // UPDATE WHERE id = ?
            pstmt.setInt(6, id);   // INSERT id
            pstmt.setInt(7, a1);   // INSERT alarm_1
            pstmt.setInt(8, a2);   // INSERT alarm_2
            pstmt.setInt(9, a3);   // INSERT alarm_3

            // 쿼리 실행
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // 예외 발생 시 런타임 예외로 래핑
            throw new RuntimeException(e);
        }
    }
}
