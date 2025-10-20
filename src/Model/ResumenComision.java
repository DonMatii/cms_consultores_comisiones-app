package Model;

import java.math.BigDecimal;

public class ResumenComision {
    private String nombreProfesion;
    private int totalAuditores;
    private BigDecimal montoTotalComisiones;
    private String evaluacionStatus;
    
    public ResumenComision() {}
    
    public ResumenComision(String nombreProfesion, int totalAuditores, 
                          BigDecimal montoTotalComisiones, String evaluacionStatus) {
        this.nombreProfesion = nombreProfesion;
        this.totalAuditores = totalAuditores;
        this.montoTotalComisiones = montoTotalComisiones;
        this.evaluacionStatus = evaluacionStatus;
    }
    
    // Getters y Setters
    public String getNombreProfesion() { return nombreProfesion; }
    public void setNombreProfesion(String nombreProfesion) { this.nombreProfesion = nombreProfesion; }
    
    public int getTotalAuditores() { return totalAuditores; }
    public void setTotalAuditores(int totalAuditores) { this.totalAuditores = totalAuditores; }
    
    public BigDecimal getMontoTotalComisiones() { return montoTotalComisiones; }
    public void setMontoTotalComisiones(BigDecimal montoTotalComisiones) { this.montoTotalComisiones = montoTotalComisiones; }
    
    public String getEvaluacionStatus() { return evaluacionStatus; }
    public void setEvaluacionStatus(String evaluacionStatus) { this.evaluacionStatus = evaluacionStatus; }
    
    @Override
    public String toString() {
        return String.format("%s | %d auditores | $%,.2f | %s", 
                nombreProfesion, totalAuditores, montoTotalComisiones, evaluacionStatus);
    }
}