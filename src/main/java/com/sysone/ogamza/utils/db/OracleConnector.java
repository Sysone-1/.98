package com.sysone.ogamza.utils.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class OracleConnector {
    private static HikariDataSource dataSource;

    static {
        try{
            // 1. properties에서 경로 읽기
            Properties props = new Properties();
            try(InputStream input = OracleConnector.class
                    .getClassLoader()
                    .getResourceAsStream("db-config.properties")){
                props.load(input);
            }
            // 2. 경로 시스템 속성으로 등록
            System.setProperty("oracle.net.tns_admin", props.getProperty("tns_admin_path"));

            // 3. HikariCP 설정
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(props.getProperty("ORACLE_JDBC_URL"));
            config.setUsername(props.getProperty("ORACLE_JDBC_USERNAME"));
            config.setPassword(props.getProperty("ORACLE_JDBC_PASSWORD"));

            // 풀 설정
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);

            dataSource = new HikariDataSource(config);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /*
        DB 에서 데이터 조회 후 데이터 값 반환
        Optional 사용 NullPointerException 방지
    */
    public static <T> Optional<T> fetchData(String sql, Class<T> type, Object... params) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Object value = rs.getObject(1);

                    if (value == null) return Optional.empty();

                    // 타입 변환 : oracle.sql.TIMESTAMP -> java.sql.Timestamp -> LocalDateTime
                    if (type == LocalDateTime.class) {
                        Method method = value.getClass().getMethod("timestampValue");
                        Timestamp ts = (Timestamp) method.invoke(value);
                        return Optional.of(type.cast(ts.toLocalDateTime()));
                    }

                    // 타입 변환 : NUMBER -> BigDecimal -> Integer
                    if (type == Integer.class) {
                        return Optional.of(type.cast(((Number) value).intValue()));
                    }

                    return Optional.ofNullable(type.cast(value));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /*
        DB 에서 데이터 조회 후 리스트 값 반환
    */
    public static <T> List<T> fetchList(String sql, Class<T> type, Object... params) {
        List<T> results = new java.util.ArrayList<>();
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object value = rs.getObject(1);
                    if (value == null) {
                        results.add(null);
                        continue;
                    }

                    if (type == LocalDateTime.class) {
                        Method method = value.getClass().getMethod("timestampValue");
                        Timestamp ts = (Timestamp) method.invoke(value);
                        results.add(type.cast(ts.toLocalDateTime()));
                    } else if (type == Integer.class) {
                        results.add(type.cast(((Number) value).intValue()));
                    } else {
                        results.add(type.cast(value));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /*
        DB 에 데이터 삽입, 수정, 삭제
    */
    public static void executeDML(String sql, Object... params) {
        try (Connection conn = OracleConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}