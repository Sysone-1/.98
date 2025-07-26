
package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.RankingDTO;
import com.sysone.ogamza.sql.user.UserHomeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * 부서별 근태 점수 랭킹을 조회하는 DAO 클래스입니다.
 * 각 부서의 총점과 순위를 계산하여 화면에 제공됩니다.
 *
 * - 점수는 출근/퇴근, 지각 등의 가중치 합산으로 구성됩니다.
 * - 부서 ID, 부서 이름, 총점, 순위 정보를 포함한 리스트를 반환합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class RankingDAO {
    private static final RankingDAO instance = new RankingDAO();
    private RankingDAO() {}
    public static RankingDAO getInstance() { return instance; }

    /**
     * 부서별 랭킹 정보를 조회합니다.
     *
     * @return RankingDTO 리스트 (부서 ID, 이름, 총점, 순위 포함)
     * @throws SQLException DB 조회 중 오류 발생 시
     */
    public List<RankingDTO> getRanking() throws SQLException {
        String sql = UserHomeSQL.SELECT_RANKING;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet resultSet = pstmt.executeQuery();
            List<RankingDTO> response = new ArrayList<>();
            while (resultSet.next()) {
                response.add(RankingDTO.builder()
                        .deptId(resultSet.getInt("DEPARTMENT_ID"))
                        .deptName(resultSet.getString("NAME"))
                        .score(resultSet.getInt("TOTAL_SCORE"))
                        .ranking(resultSet.getInt("RANKING"))
                        .build());

                // 로그 출력 (디버깅용)
                System.out.printf("부서: %s, 점수: %d, 순위: %d\n",
                        resultSet.getString("NAME"),
                        resultSet.getInt("TOTAL_SCORE"),
                        resultSet.getInt("RANKING"));
            }
            return response;
        }
    }
}
