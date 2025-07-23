package com.sysone.ogamza.service.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.dao.user.DepartmentDAO;
import com.sysone.ogamza.dao.user.EmployeeDAO;
import com.sysone.ogamza.dao.user.MessageDAO;
import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.dto.user.EmployeeDTO;
import com.sysone.ogamza.dto.user.MessageDTO;

import java.util.List;

public class SenderMessageService {
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    public List<DepartmentDTO> getAllDepartment() {
        return departmentDAO.findAll();
    }
    public List<EmployeeDTO> getAllEmployeeByDeptId(int deptId) {
        return employeeDAO.findByDepartmentId(deptId);
    }
    public void sendMessage (int receiverId, String content) {
        int sernderId = Session.getInstance().getLoginUser().getId();
        MessageDTO dto = new MessageDTO(sernderId, receiverId, content);
        messageDAO.insertMessage(dto);

    }
}
