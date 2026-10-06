package cl.speedfast.dao;
import cl.speedfast.config.ConexionDB;
import cl.speedfast.modelo.*;
import java.sql.*;
import java.util.*;

public class EntregaDAO implements CrudDAO<Entrega> {
 @Override public int create(Entrega e) throws SQLException {
  String sql = "INSERT INTO entregas (pedido_id, repartidor_id, fecha_hora) VALUES (?, ?, ?)";
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
   bind(ps, e); ps.executeUpdate();
   try (ResultSet rs = ps.getGeneratedKeys()) {
    if (!rs.next()) throw new SQLException("No se obtuvo el ID creado.");
    int id = rs.getInt(1); e.setId(id); return id;
   }
  }
 }
 @Override public List<Entrega> readAll() throws SQLException {
  List<Entrega> lista = new ArrayList<>();
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM entregas ORDER BY id"); ResultSet rs = ps.executeQuery()) {
   while (rs.next()) lista.add(new Entrega(rs.getInt("id"), rs.getInt("pedido_id"), rs.getInt("repartidor_id"), rs.getTimestamp("fecha_hora").toLocalDateTime()));
  }
  return lista;
 }
 @Override public void update(Entrega e) throws SQLException {
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE entregas SET pedido_id = ?, repartidor_id = ?, fecha_hora = ? WHERE id = ?")) {
   bind(ps, e); ps.setInt(4, e.getId()); comprobar(ps.executeUpdate());
  }
 }
 @Override public void delete(int id) throws SQLException {
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM entregas WHERE id = ?")) {
   ps.setInt(1, id); comprobar(ps.executeUpdate());
  }
 }
 private void bind(PreparedStatement ps, Entrega e) throws SQLException {
  ps.setInt(1, e.getPedidoId()); ps.setInt(2, e.getRepartidorId()); ps.setTimestamp(3, Timestamp.valueOf(e.getFechaHora()));
 }
 private void comprobar(int filas) throws SQLException {
  if (filas == 0) throw new SQLException("El registro ya no existe. Por favor, actualice la tabla.");
 }
}
