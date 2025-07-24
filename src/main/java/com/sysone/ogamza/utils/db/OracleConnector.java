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

    /**
     * 주어진 SQL 쿼리문을 실행하여 단일 값을 조회하는 메서드입니다.
     * <p>
     * - 첫 번째 컬럼 값을 반환하며, 반환 타입은 호출 시 지정한 제네릭 타입으로 변환됩니다.
     * - Oracle DB 특성에 따라 oracle.sql.TIMESTAMP 등의 특수 타입을 자동으로 변환합니다.
     * </p>
     *
     * @param <T>    반환할 데이터의 타입 (예: String.class, Integer.class, LocalDateTime.class 등)
     * @param sql    실행할 SQL 쿼리문 (단일 컬럼 조회 쿼리여야 함)
     * @param type   반환할 데이터 타입의 클래스 객체
     * @param params 쿼리의 파라미터 값들 (PreparedStatement 바인딩용)
     * @return Optional<T> - 데이터가 존재하면 변환된 값, 없으면 Optional.empty()
     *
     * 작성자 김민호
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

    /**
     * 데이터베이스에서 지정한 SQL 쿼리를 실행하고 결과를 리스트로 반환합니다.
     * 쿼리 결과의 첫 번째 컬럼 값을 지정된 타입으로 변환하여 리스트에 담아 반환합니다.
     *  LocalDateTime, Integer 타입에 대해 특별한 변환 로직을 수행하며,
     * 그 외 타입은 기본 타입 캐스팅을 시도합니다.
     *
     * @author 조윤상
     * @since 2025-07-23
     *
     * @param <T>    반환할 리스트 요소의 타입
     * @param sql    실행할 SQL 쿼리 문자열
     * @param type   결과값의 타입 클래스 (예: LocalDateTime.class, Integer.class)
     * @param params SQL 쿼리의 파라미터 (가변 인자)
     * @return 쿼리 실행 결과를 지정 타입으로 변환한 리스트
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

    /**
     * 데이터베이스에 대해 DML(데이터 조작어) 작업을 실행합니다.
     *
     * 주로 INSERT, UPDATE, DELETE 문을 수행하며, 지정한 SQL과 파라미터를 사용합니다.
     * 실행 중 예외가 발생하면 스택 트레이스를 출력합니다.
     *
     * @author 조윤상
     * @since 2025-07-23
     *
     * @param sql    실행할 DML SQL 문
     * @param params SQL 문의 파라미터 (가변 인자)
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