package BD;

import java.sql.*;

public class StoredProcedures {
    
    public static boolean calcularComisionesMes(int mes, int anno) {
        String sql = "BEGIN CALCULAR_COMISIONES_MES(?, ?); END;";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, mes);
            stmt.setInt(2, anno);
            stmt.execute();
            
            System.out.println("✅ Procedimiento REAL ejecutado: CALCULAR_COMISIONES_MES(" + mes + ", " + anno + ")");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error ejecutando procedimiento: " + e.getMessage());
            return false;
        }
    }
    
    public static double obtenerTotalComisionAuditor(String runAuditor, int mes, int anno) {
        String sql = "SELECT OBTENER_TOTAL_COMISION_AUDITOR(?, ?, ?) FROM DUAL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, runAuditor);
            stmt.setInt(2, mes);
            stmt.setInt(3, anno);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo comisión: " + e.getMessage());
            return 0;
        }
    }
}