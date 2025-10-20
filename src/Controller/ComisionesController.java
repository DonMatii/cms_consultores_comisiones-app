package Controller;

import Model.ResumenComision;
import BD.StoredProcedures;
import BD.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComisionesController {
    
    public boolean procesarComisiones(int mes, int anno) {
        return StoredProcedures.calcularComisionesMes(mes, anno);
    }
    
    public List<ResumenComision> obtenerResumenComisiones(int mes, int anno) {
        List<ResumenComision> comisiones = new ArrayList<>();
        String sql = "SELECT NOMBRE_PROFESION, TOTAL_AUDITORES, MONTO_TOTAL_COMISIONES, EVALUACION_STATUS " +
                    "FROM RESUMEN_COMISIONES_AUDIT_MES " +
                    "WHERE MES_PROCESO = ? AND ANNO_PROCESO = ? " +
                    "ORDER BY NOMBRE_PROFESION";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mes);
            stmt.setInt(2, anno);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ResumenComision comision = new ResumenComision(
                    rs.getString("NOMBRE_PROFESION"),
                    rs.getInt("TOTAL_AUDITORES"),
                    rs.getBigDecimal("MONTO_TOTAL_COMISIONES"),
                    rs.getString("EVALUACION_STATUS")
                );
                comisiones.add(comision);
            }
            
            System.out.println("✅ Datos REALES obtenidos: " + comisiones.size() + " registros");
            
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo resumen REAL: " + e.getMessage());
        }
        
        return comisiones;
    }
    
    public double obtenerComisionAuditor(String run, int mes, int anno) {
        return StoredProcedures.obtenerTotalComisionAuditor(run, mes, anno);
    }
}