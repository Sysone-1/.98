package com.sysone.ogamza.sql;

public class LoginSQL {
    public static final String SELECT_LOGIN = """
              SELECT e.id, d.name AS dept_name, e.position, e.email, e.name, e.is_admin, e.card_uid, e.pic_dir
              FROM employee e
              JOIN department d ON d.id = e.department_id
              WHERE email = ? AND password = ?
            """;
}
