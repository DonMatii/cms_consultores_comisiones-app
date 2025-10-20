import View.MainFrame;
import BD.DatabaseConnection;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Comisiones");
        
        if (BD.DatabaseConnection.testConnection()) {
            System.out.println("✅ Conectado a BD REAL");
            ejecutarAplicacionReal();
        } else {
            mostrarInstruccionesCIDR();
        }
    }
    
    private static void mostrarInstruccionesCIDR() {
        String mensaje = 
            "❌ No se pudo conectar\n\n" +
            "CONFIGURACIÓN en Oracle Cloud:\n\n" +
            "1. 'Lista de control de acceso' → EDITAR\n" +
            "2. 'Tipo de notación IP' → 'Bloque de CIDR'\n" +
            "3. 'Valores' → 0.0.0.0/0\n" +
            "4. GUARDAR\n\n" +
            "Si no acepta /0, prueba:\n" +
            "• 0.0.0.0/1\n" +
            "• 128.0.0.0/1\n\n" +
            "¿Ejecutar en MODO DEMO mientras tanto?";
        
        int option = JOptionPane.showConfirmDialog(null, mensaje,
            "Configurar Bloque CIDR", JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            ejecutarModoDemo();
        }
    }
    
    private static void ejecutarAplicacionReal() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
    
    private static void ejecutarModoDemo() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setTitle("Sistema de Comisiones - [MODO DEMO]");
            frame.setVisible(true);
        });
    }
}