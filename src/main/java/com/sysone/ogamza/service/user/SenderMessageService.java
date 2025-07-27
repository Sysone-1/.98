package com.sysone.ogamza.service.user;

import com.sysone.ogamza.Session;
import com.sysone.ogamza.dao.user.DepartmentDAO;
import com.sysone.ogamza.dao.user.EmployeeDAO;
import com.sysone.ogamza.dao.user.MessageDAO;
import com.sysone.ogamza.dto.user.DepartmentDTO;
import com.sysone.ogamza.dto.user.EmployeeDTO;
import com.sysone.ogamza.dto.user.MessageDTO;

import java.util.List;

/**
 * ============================================
 * 쪽지 전송 서비스 (SenderMessageService)
 * ============================================
 * - 부서 및 사원 정보 조회
 * - 쪽지 작성 및 DB 저장 기능 제공
 * - 컨트롤러와 DAO 사이에서 비즈니스 로직을 담당
 *
 * 작성자: 구희원
 * 작성일: 2025-07-27
 * ============================================
 */

public class SenderMessageService {

    // DAO 객체들 (DB 접근 담당)
    private final DepartmentDAO departmentDAO = new DepartmentDAO();  // 부서 조회용
    private final EmployeeDAO employeeDAO = new EmployeeDAO();        // 사원 조회용
    private final MessageDAO messageDAO = new MessageDAO();           // 메시지 저장용

    /**
     * ▶ 전체 부서 목록을 조회하여 반환
     *
     * @return 부서 DTO 리스트
     */
    public List<DepartmentDTO> getAllDepartment() {
        return departmentDAO.findAll();
    }

    /**
     * ▶ 부서 ID에 해당하는 모든 사원 목록 조회
     *
     * @param deptId 부서 ID
     * @return 사원 DTO 리스트
     */
    public List<EmployeeDTO> getAllEmployeeByDeptId(int deptId) {
        return employeeDAO.findByDepartmentId(deptId);
    }

    /**
     * ▶ 쪽지 전송
     * - 현재 로그인한 사용자를 발신자로 하여 수신자와 내용을 DTO로 구성
     * - 메시지 DAO를 통해 DB에 저장
     *
     * @param receiverId 수신자 ID
     * @param content 쪽지 내용
     */
    public void sendMessage(int receiverId, String content) {
        int senderId = Session.getInstance().getLoginUser().getId(); // 현재 로그인한 사용자 ID
        MessageDTO dto = new MessageDTO(senderId, receiverId, content); // 메시지 객체 생성
        messageDAO.insertMessage(dto); // DB에 저장
    }
}
