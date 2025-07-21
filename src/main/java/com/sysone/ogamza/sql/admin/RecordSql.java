package com.sysone.ogamza.sql.admin;


public class RecordSql {

    public static final String FIND_ALL_RECORDS =
            "SELECT " +
                    "    NVL(e.id, -1) AS emp_id, " +
                    "    NVL(e.name, '???') AS employee_name, " +
                    "    NVL(d.name, '???') AS department_name, " +
                    "    NVL(e.position, '???') AS position, " +
                    "    a.access_time AS tagging_time, " +
                    "    CASE WHEN e.id IS NULL THEN '?? ??' ELSE '??' END AS approval_status " +
                    "FROM access_log a " +
                    "LEFT JOIN employee e ON a.employee_id = e.id " +
                    "LEFT JOIN department d ON e.department_id = d.id " +
                    "ORDER BY a.access_time DESC";


}
