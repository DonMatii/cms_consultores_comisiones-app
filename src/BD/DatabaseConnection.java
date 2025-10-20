package BD;

import java.sql.*;

public class DatabaseConnection {
    // URL CORREGIDA - formato simple
    private static final String DB_URL = "jdbc:oracle:thin:@(description=(retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.sa-valparaiso-1.oraclecloud.com))(connect_data=(service_name=g6be726cb63234c_basededatos_low.adb.oraclecloud.com))(security=(ssl_server_cert_dn=\"CN=adb.sa-valparaiso-1.oraclecloud.com, O=Oracle Corporation, L=Redwood City, ST=California, C=US\")))";
    
    private static final String USER = "CONSULTORES";
    private static final String PASSWORD = "MatiiZeus90..";
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                
                // Propiedades para conexión SSL
                java.util.Properties props = new java.util.Properties();
                props.setProperty("user", USER);
                props.setProperty("password", PASSWORD);
                props.setProperty("oracle.net.ssl_server_dn_match", "true");
                props.setProperty("oracle.net.ssl_version", "1.2");
                
                System.out.println("🔌 Conectando a: " + DB_URL);
                connection = DriverManager.getConnection(DB_URL, props);
                System.out.println("✅ Conexión REAL establecida con Oracle Cloud");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver Oracle no encontrado", e);
            }
        }
        return connection;
    }
    
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean conectado = conn != null && !conn.isClosed();
            if (conectado) {
                System.out.println("🎉 ¡CONEXIÓN REAL EXITOSA!");
                System.out.println("📊 BD: " + conn.getMetaData().getDatabaseProductName());
            }
            return conectado;
        } catch (SQLException e) {
            System.err.println("❌ Error conexión: " + e.getMessage());
            return false;
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("🔌 Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }
}