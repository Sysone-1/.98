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

public class RankingDAO {
    private static final RankingDAO instance = new RankingDAO();
    private  RankingDAO(){}

    public static RankingDAO getInstance(){return instance;}

    public List<RankingDTO> getRanking()throws SQLException {
        String sql = UserHomeSQL.SELECT_RANKING;

        try(Connection conn = OracleConnector.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            ResultSet resultSet = pstmt.executeQuery();
            List<RankingDTO> response = new ArrayList<>();
            while (resultSet.next()){
               response.add(RankingDTO.builder()
                            .deptId(resultSet.getInt("DEPARTMENT_ID"))
                            .deptName(resultSet.getString("NAME"))
                            .score(resultSet.getInt("TOTAL_SCORE"))
                            .ranking(resultSet.getInt("RANKING"))
                            .build());
            }
            return response;
        }
    }
}
