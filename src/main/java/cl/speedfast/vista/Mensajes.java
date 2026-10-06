package cl.speedfast.vista;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;
public final class Mensajes {
 private Mensajes() { }
 public static void error(Component padre, Exception e) {
  String mensaje = e.getMessage();
  if (e instanceof SQLException sql) {
   String estado = sql.getSQLState();
   if (sql.getErrorCode() == 1451) mensaje = "No se puede eliminar: todavía tiene entregas asociadas. Primero debe eliminar o reasignar esas entregas.";
   else if (sql.getErrorCode() == 1452) mensaje = "El pedido o el repartidor ya no existe. Por favor, actualice los datos y vuelva a seleccionarlos.";
   else if ("28000".equals(estado)) mensaje = "Usuario o contraseña de MySQL incorrectos. Revise db.properties o reintente.";
   else if (sql.getErrorCode() == 1049 || sql.getErrorCode() == 1146) mensaje = "Falta la base de datos o sus tablas. Ejecute sql/01_schema.sql.";
   else if ((estado != null && estado.startsWith("08")) || mensaje.contains("No suitable driver")) mensaje = "No fue posible conectar. Revise que MySQL esté iniciado, la configuración y la dependencia Maven.";
   else if (estado != null && estado.startsWith("23")) mensaje = "La operación incumple una restricción de la base de datos.";
   else mensaje = "No se pudo completar la operación en la base de datos. " + mensaje;
   System.err.println("Error SQL: " + estado + " / " + sql.getErrorCode() + " / " + sql.getMessage());
  }
  JOptionPane.showMessageDialog(padre, mensaje, "SpeedFast", JOptionPane.ERROR_MESSAGE);
 }
}
