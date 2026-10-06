package cl.speedfast.dao;
import cl.speedfast.config.ConexionDB;
import cl.speedfast.modelo.*;
import java.sql.*;
import java.util.*;

public class PedidoDAO implements CrudDAO<Pedido> {
 @Override public int create(Pedido e) throws SQLException {
  String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
   bind(ps, e); ps.executeUpdate();
   try (ResultSet rs = ps.getGeneratedKeys()) {
    if (!rs.next()) throw new SQLException("No se obtuvo el ID creado.");
    int id = rs.getInt(1); e.setId(id); return id;
   }
  }
 }
 @Override public List<Pedido> readAll() throws SQLException {
  List<Pedido> lista = new ArrayList<>();
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM pedidos ORDER BY id"); ResultSet rs = ps.executeQuery()) {
   while (rs.next()) lista.add(new Pedido(rs.getInt("id"), rs.getString("direccion"), TipoPedido.valueOf(rs.getString("tipo")), EstadoPedido.valueOf(rs.getString("estado"))));
  }
  return lista;
 }
 @Override public void update(Pedido e) throws SQLException {
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?")) {
   bind(ps, e); ps.setInt(4, e.getId()); comprobar(ps.executeUpdate());
  }
 }
 @Override public void delete(int id) throws SQLException {
  try (Connection c = ConexionDB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM pedidos WHERE id = ?")) {
   ps.setInt(1, id); comprobar(ps.executeUpdate());
  }
 }
 private void bind(PreparedStatement ps, Pedido e) throws SQLException {
  ps.setString(1, e.getDireccion()); ps.setString(2, e.getTipo().name()); ps.setString(3, e.getEstado().name());
 }
 private void comprobar(int filas) throws SQLException {
  if (filas == 0) throw new SQLException("El registro ya no existe. Por favor, actualice la tabla.");
 }
}
