package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.sql.user.DepartmentSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
    public List<DepartmentDTO> findAll() {
        List<DepartmentDTO> list = new ArrayList<>();
        String sql = DepartmentSQL.SELECT_DEPARTMENT;

        try (Connection conn = OracleConnector.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {
            while(rs.next()) {
                list.add(new DepartmentDTO(rs.getInt("id"),rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
