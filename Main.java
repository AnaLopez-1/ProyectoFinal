package ProyectoFinal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import pruebas.PruebasProyecto;
import servicio.GestorMonedero;
import vista.VentanaMonedero;

public class Main {

    public static void main(String[] args) {
        // 1. Ejecuta las pruebas en consola y carga los datos semilla en todas las estructuras
        GestorMonedero gestor = PruebasProyecto.ejecutarSuiteDePruebasSemilla();

        // 2. Lanza la Interfaz Gráfica de Usuario Avanzada en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            VentanaMonedero ventana = new VentanaMonedero(gestor);
            ventana.setVisible(true);
        });
    }
}