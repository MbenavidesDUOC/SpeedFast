package cl.speedfast.modelo;

public class Entrega {
 private int id;
 private int pedidoId;
 private int repartidorId;
 private java.time.LocalDateTime fechaHora;
 public Entrega(int id, int pedidoId, int repartidorId, java.time.LocalDateTime fechaHora) {
  this.id = id;
  this.pedidoId = pedidoId;
  this.repartidorId = repartidorId;
  this.fechaHora = fechaHora;
 }
 public int getId() { return id; }
 public void setId(int id) { this.id = id; }
 public int getPedidoId() { return pedidoId; }
 public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
 public int getRepartidorId() { return repartidorId; }
 public void setRepartidorId(int repartidorId) { this.repartidorId = repartidorId; }
 public java.time.LocalDateTime getFechaHora() { return fechaHora; }
 public void setFechaHora(java.time.LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
