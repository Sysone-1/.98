package com.sysone.ogamza.sql.user;

/**
 * 사용자 홈 화면에서 사용되는 SQL 쿼리들을 정의한 클래스입니다.
 * 근무자 정보 조회, 랜덤 행운 정보 갱신, 이모지 업데이트, 부서별 랭킹 조회 등
 * 홈 관련 데이터 처리를 담당합니다.
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class UserHomeSQL {

    /** 홈 화면에서 사용자 이름, 부서명, 프로필 이미지, 오늘의 행운 정보(색상, 숫자, 도형), 랜덤 메시지, 이모지를 조회하는 쿼리 */
    public static final String SELECT_HOME=
            """
            SELECT e.name AS employeeName, d.name AS deptName, e.pic_dir, e.lucky_color, e.lucky_number
                 , e.lucky_shape, e.random_message, e.mood_emoji
            FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE e.ID = ?
            """;

    /** 모든 사원의 ID를 조회하는 쿼리 (행운 정보 일괄 업데이트를 위해 사용) */
    public static final String SELECT_ALL=
            """
            SELECT id
            FROM employee       
            """;

    /** 임시 테이블(luck_update_temp)에 행운 데이터를 저장하는 쿼리 */
    public static final String INSERT_TEMP=
            """
            INSERT INTO luck_update_temp (employee_id, lucky_number, lucky_shape, lucky_color, random_message)
            VALUES (?, ?, ? , ? ,?)
            """;

    /** 임시 테이블의 데이터를 기반으로 employee 테이블의 행운 정보를 일괄 갱신하는 MERGE 쿼리 */
    public static final String MERGE_DATA=
            """
            BEGIN
                MERGE INTO employee e
                USING luck_update_temp t
                ON(e.ID = t.employee_id)
                WHEN MATCHED THEN
                    UPDATE SET
                        e.lucky_number = t.lucky_number,
                        e.lucky_shape = t.lucky_shape,
                        e.lucky_color = t.lucky_color,
                        e.random_message = t.random_message;
            END;
            """;

    /** 사원의 이모지(기분 상태)를 업데이트하는 쿼리 */
    public static final String UPDATE_EMOJI=
            """
            UPDATE employee
            SET mood_emoji = ?
            WHERE id = ?        
            """;

    /** 부서별 근무 점수를 기준으로 랭킹을 매기고 상위 3개 부서를 조회하는 쿼리 */
    public static final String SELECT_RANKING =
            """
            SELECT *
            FROM (
                SELECT RANK() OVER (ORDER BY TOTAL_SCORE ASC) AS RANKING,
                       DEPARTMENT_ID,
                       NAME,
                       TOTAL_SCORE
                FROM DEPT_RANKING
            )
            WHERE RANKING <= 3
            ORDER BY RANKING
            """;
}
