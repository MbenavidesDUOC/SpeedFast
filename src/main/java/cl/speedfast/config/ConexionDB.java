package cl.speedfast.config;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

public final class ConexionDB {
 private ConexionDB() { }
 public static Connection getConnection() throws SQLException {
  Properties p = new Properties();
  Path archivo = Path.of("db.properties");
  if (Files.exists(archivo)) {
   try (InputStream in = Files.newInputStream(archivo)) { p.load(in); }
   catch (IOException e) { throw new SQLException("No se puede leer el db.properties.", e); }
  }
  String url = valor("SPEEDFAST_DB_URL", p, "db.url", "jdbc:mysql://localhost:3306/speedfast_db?connectionTimeZone=LOCAL");
  String user = valor("SPEEDFAST_DB_USER", p, "db.user", "root");
  String pass = valor("SPEEDFAST_DB_PASSWORD", p, "db.password", "");
  DriverManager.setLoginTimeout(5);
  return DriverManager.getConnection(url, user, pass);
 }
 private static String valor(String env, Properties p, String key, String def) {
  String value = System.getenv(env); return value != null ? value : p.getProperty(key, def);
 }
}
