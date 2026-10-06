package cl.speedfast;
import cl.speedfast.vista.VentanaPrincipal;
import javax.swing.*;
public final class Main {
 private Main() { }
 public static void main(String[] args) {
  SwingUtilities.invokeLater(() -> {
   try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
   catch (Exception e) { System.err.println("Se usará el estilo predeterminado de Swing."); }
   new VentanaPrincipal().setVisible(true);
  });
 }
}
