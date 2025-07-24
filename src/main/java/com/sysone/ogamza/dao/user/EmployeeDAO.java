package com.sysone.ogamza.dao.user;

import com.sysone.ogamza.dto.user.EmployeeDTO;
import com.sysone.ogamza.sql.user.EmployeeSQL;
import com.sysone.ogamza.utils.db.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public List<EmployeeDTO> findByDepartmentId(int deptId) {
        List<EmployeeDTO> list = new ArrayList<>();
        String sql = EmployeeSQL.SELECT_EMPLOYEE;

        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new EmployeeDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("department_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
