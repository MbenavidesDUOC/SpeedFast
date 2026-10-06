package cl.speedfast.vista;
import java.time.LocalDateTime;
import java.time.format.*;
public final class Validaciones {
 public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
 private Validaciones() { }
 public static String texto(String valor, String campo, int max) {
  String limpio = valor.strip();
  if (limpio.isEmpty()) throw new IllegalArgumentException("Debe ingresar " + campo + ".");
  if (limpio.length() > max) throw new IllegalArgumentException(campo + ": máximo " + max + " caracteres.");
  return limpio;
 }
 public static LocalDateTime fecha(String valor) {
  try {
   LocalDateTime fecha = LocalDateTime.parse(valor.strip(), FECHA);
   if (fecha.getYear() < 1000 || fecha.getYear() > 9999) throw new IllegalArgumentException("El año debe estar entre 1000 y 9999 para MySQL.");
   return fecha;
  }
  catch (DateTimeParseException e) { throw new IllegalArgumentException("Fecha y hora inválidas. Use yyyy-MM-dd HH:mm (ejemplo: 2026-10-05 17:30)."); }
 }
}
