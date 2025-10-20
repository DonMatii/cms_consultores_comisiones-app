package View;

import Controller.ComisionesController;
import Model.ResumenComision;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private ComisionesController controller;
    private JTable tablaComisiones;
    private DefaultTableModel tableModel;
    private JTextField txtMes, txtAnno, txtRunAuditor;
    private JTextArea txtLog;
    
    public MainFrame() {
        this.controller = new ComisionesController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Sistema de Comisiones CMS - Auditorías ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout());
        panelControles.add(new JLabel("Mes:"));
        txtMes = new JTextField("9", 3);
        // Hacer la tabla NO editable
        tablaComisiones = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Todas las celdas NO editables
            }
        };
        
        panelControles.add(txtMes);
        
        panelControles.add(new JLabel("Año:"));
        txtAnno = new JTextField("2025", 5);
        panelControles.add(txtAnno);
        
        JButton btnCalcular = new JButton("Calcular Comisiones");
        JButton btnMostrar = new JButton("Mostrar Resumen");
        
        panelControles.add(btnCalcular);
        panelControles.add(btnMostrar);
        
        // Panel para consulta individual
        JPanel panelAuditor = new JPanel(new FlowLayout());
        panelAuditor.add(new JLabel("RUN Auditor:"));
        txtRunAuditor = new JTextField("12345678-9", 12);
        panelAuditor.add(txtRunAuditor);
        
        JButton btnConsultarAuditor = new JButton("Consultar Auditor");
        panelAuditor.add(btnConsultarAuditor);
        
        // Tabla de resultados - NO EDITABLE
        String[] columnNames = {"Profesión", "Total Auditores", "Monto Total", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla de solo lectura
            }
        };
        
        tablaComisiones = new JTable(tableModel);
        
        // Mejorar apariencia de la tabla
        tablaComisiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaComisiones.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaComisiones);
        
        // Área de log
        txtLog = new JTextArea(6, 80);
        txtLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        
        // Layout principal
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(panelControles);
        topPanel.add(panelAuditor);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(scrollLog, BorderLayout.SOUTH);
        
        // Listeners con validaciones mejoradas
        btnCalcular.addActionListener(e -> calcularComisiones());
        btnMostrar.addActionListener(e -> mostrarResumen());
        btnConsultarAuditor.addActionListener(e -> consultarAuditor());
        
        log("🎉 ¡CONEXIÓN REAL ACTIVADA!");
        log("💡 Conectado a Oracle Cloud Autonomous Database");
        log("📊 Listo para trabajar con datos reales");
    }
    
    private void calcularComisiones() {
        try {
            int mes = Integer.parseInt(txtMes.getText());
            int anno = Integer.parseInt(txtAnno.getText());
            
            // Validar mes
            if (mes < 1 || mes > 12) {
                JOptionPane.showMessageDialog(this, 
                    "El mes debe estar entre 1 y 12", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar año
            if (anno < 2000 || anno > 2100) {
                JOptionPane.showMessageDialog(this, 
                    "El año debe estar entre 2000 y 2100", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            log("🔄 Ejecutando CALCULAR_COMISIONES_MES(" + mes + ", " + anno + ")...");
            boolean exito = controller.procesarComisiones(mes, anno);
            
            if (exito) {
                log("✅ Procedimiento ejecutado exitosamente");
                JOptionPane.showMessageDialog(this, 
                    "Comisiones calculadas exitosamente\nDatos actualizados en la base de datos",
                    "Proceso Completado",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                log("❌ Error al ejecutar el procedimiento");
                JOptionPane.showMessageDialog(this, 
                    "Error al calcular comisiones\nRevise los logs para más información",
                    "Error en Proceso",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            log("❌ Error: Mes y año deben ser números válidos");
            JOptionPane.showMessageDialog(this, 
                "Mes y año deben ser números válidos", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarResumen() {
        try {
            int mes = Integer.parseInt(txtMes.getText());
            int anno = Integer.parseInt(txtAnno.getText());
            
            // Validaciones
            if (mes < 1 || mes > 12) {
                JOptionPane.showMessageDialog(this, "Mes inválido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (anno < 2000 || anno > 2100) {
                JOptionPane.showMessageDialog(this, "Año inválido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            log("📊 Cargando datos REALES para " + mes + "/" + anno + "...");
            List<ResumenComision> comisiones = controller.obtenerResumenComisiones(mes, anno);
            
            // Limpiar tabla
            tableModel.setRowCount(0);
            
            if (comisiones.isEmpty()) {
                log("ℹ️ No hay datos para el período " + mes + "/" + anno);
                JOptionPane.showMessageDialog(this, 
                    "No hay datos de comisiones para " + mes + "/" + anno + "\nEjecute 'Calcular Comisiones' primero",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Llenar tabla con datos REALES
            for (ResumenComision comision : comisiones) {
                tableModel.addRow(new Object[]{
                    comision.getNombreProfesion(),
                    comision.getTotalAuditores(),
                    comision.getMontoTotalComisiones(),
                    comision.getEvaluacionStatus()
                });
            }
            
            log("✅ Datos REALES cargados: " + comisiones.size() + " registros");
            
        } catch (NumberFormatException e) {
            log("❌ Error: Mes y año deben ser números válidos");
            JOptionPane.showMessageDialog(this, 
                "Mes y año deben ser números válidos", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void consultarAuditor() {
        String run = txtRunAuditor.getText().trim();
        
        // Validar RUN
        if (run.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese un RUN válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar formato básico de RUN (ej: 12345678-9)
        if (!run.matches("^\\d{7,8}-[\\dkK]$")) {
            JOptionPane.showMessageDialog(this, 
                "Formato de RUN inválido.\nUse: 12345678-9", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int mes = Integer.parseInt(txtMes.getText());
            int anno = Integer.parseInt(txtAnno.getText());
            
            // Validar mes y año
            if (mes < 1 || mes > 12 || anno < 2000 || anno > 2100) {
                JOptionPane.showMessageDialog(this, 
                    "Mes (1-12) y Año (2000-2100) deben ser válidos", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            log("🔍 Consultando comisión REAL para: " + run);
            double comision = controller.obtenerComisionAuditor(run, mes, anno);
            
            if (comision > 0) {
                log("💰 Comisión REAL: " + run + " = $" + String.format("%,.2f", comision));
                JOptionPane.showMessageDialog(this, 
                    "RUN: " + run + 
                    "\nPeríodo: " + mes + "/" + anno +
                    "\nComisión: $" + String.format("%,.2f", comision),
                    "Consulta Individual - DATOS REALES",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                log("ℹ️ No se encontró comisión para: " + run);
                JOptionPane.showMessageDialog(this, 
                    "No se encontró comisión para el RUN: " + run + 
                    "\nEn el período: " + mes + "/" + anno,
                    "Sin Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            log("❌ Error: Mes y año deben ser números válidos");
            JOptionPane.showMessageDialog(this, 
                "Mes y año deben ser números válidos", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void log(String mensaje) {
        txtLog.append(mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}