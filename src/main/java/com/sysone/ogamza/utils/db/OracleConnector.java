package com.sysone.ogamza.utils.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class OracleConnector {
    private static HikariDataSource dataSource;

    static {
        try{
            Properties props = new Properties();
            try(InputStream input = OracleConnector.class.getClassLoader().getResourceAsStream("db-config.properties")){
                props.load(input);
            }
            // Wallet 경로를 JVM 시스템 속성에 등록
            System.setProperty("oracle.net.tns_admin", props.getProperty("tns_admin_path"));

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("ORACLE_JDBC_URL"));
            config.setUsername(props.getProperty("ORACLE_JDBC_USERNAME"));
            config.setPassword(props.getProperty("ORACLE_JDBC_PASSWORD"));

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
}