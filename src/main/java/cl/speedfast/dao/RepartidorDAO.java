package cl.speedfast.dao;
import cl.speedfast.config.ConexionDB;
import cl.speedfast.modelo.*;
import java.sql.*;
import java.util.*;

public class RepartidorDAO implements CrudDAO<Repartidor> {
 @Override public int create(Repartidor e) throws SQLException {
  String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
   bind(ps, e); ps.executeUpdate();
   try (ResultSet rs = ps.getGeneratedKeys()) {
    if (!rs.next()) throw new SQLException("No se obtuvo el ID creado.");
    int id = rs.getInt(1); e.setId(id); return id;
   }
  }
 }
 @Override public List<Repartidor> readAll() throws SQLException {
  List<Repartidor> lista = new ArrayList<>();
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM repartidores ORDER BY id"); ResultSet rs = ps.executeQuery()) {
   while (rs.next()) lista.add(new Repartidor(rs.getInt("id"), rs.getString("nombre")));
  }
  return lista;
 }
 @Override public void update(Repartidor e) throws SQLException {
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE repartidores SET nombre = ? WHERE id = ?")) {
   bind(ps, e); ps.setInt(2, e.getId()); comprobar(ps.executeUpdate());
  }
 }
 @Override public void delete(int id) throws SQLException {
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM repartidores WHERE id = ?")) {
   ps.setInt(1, id); comprobar(ps.executeUpdate());
  }
 }
 private void bind(PreparedStatement ps, Repartidor e) throws SQLException {
  ps.setString(1, e.getNombre());
 }
 private void comprobar(int filas) throws SQLException {
  if (filas == 0) throw new SQLException("El registro ya no existe. Por favor, actualice la tabla.");
 }
}
