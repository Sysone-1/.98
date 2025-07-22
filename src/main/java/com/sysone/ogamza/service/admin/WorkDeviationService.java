package com.sysone.ogamza.service.admin;

import com.sysone.ogamza.dao.admin.WorkDeviationDAO;
import com.sysone.ogamza.dto.admin.WorkDeviationModelDTO;

import java.util.List;

/**
 * WorkDeviationService
 * – 일일 근무 이탈자 통계 조회 기능용 Service 클래스
 */
public class WorkDeviationService {

    private final WorkDeviationDAO dao = new WorkDeviationDAO();

    /**
     * 전체 부서의 이탈자 통계 조회
     *
     * @return List of WorkDeviationModel
     */
    public List<WorkDeviationModelDTO> getAllDeviations() {
        return dao.selectAll();
    }

    /**
     * 특정 부서의 이탈자 통계 조회
     *
     * @param deptName 조회할 부서명 ("전체"일 경우 전체 조회)
     * @return List of WorkDeviationModel
     */
    public List<WorkDeviationModelDTO> getDeviationsByDept(String deptName) {
        return dao.selectByDept(deptName);
    }
}
