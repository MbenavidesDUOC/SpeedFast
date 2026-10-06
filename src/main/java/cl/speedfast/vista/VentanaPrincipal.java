package cl.speedfast.vista;

import cl.speedfast.config.ConexionDB;
import cl.speedfast.dao.*;
import cl.speedfast.modelo.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.table.*;

public class VentanaPrincipal extends JFrame {
 private final RepartidorDAO repartidorDAO = new RepartidorDAO();
 private final PedidoDAO pedidoDAO = new PedidoDAO();
 private final EntregaDAO entregaDAO = new EntregaDAO();
 private final JTextField nombre = new JTextField(22), direccion = new JTextField(25);
 private final JTextField fecha = new JTextField(17);
 private final JComboBox<TipoPedido> tipo = new JComboBox<>(TipoPedido.values());
 private final JComboBox<EstadoPedido> estado = new JComboBox<>(EstadoPedido.values());
 private final JComboBox<Pedido> pedido = new JComboBox<>();
 private final JComboBox<Repartidor> repartidor = new JComboBox<>();
 private final JComboBox<String> filtroTipo = new JComboBox<>(new String[]{"Todos", "COMIDA", "ENCOMIENDA", "EXPRESS"});
 private final JComboBox<String> filtroEstado = new JComboBox<>(new String[]{"Todos", "PENDIENTE", "EN_REPARTO", "ENTREGADO"});
 private final JComboBox<String> filtroPedido = new JComboBox<>(), filtroRepartidor = new JComboBox<>();
 private final DefaultTableModel mr = modelo("ID", "Nombre");
 private final DefaultTableModel mp = modelo("ID", "Dirección", "Tipo", "Estado");
 private final DefaultTableModel me = modelo("ID", "Pedido ID", "Dirección", "Repartidor ID", "Nombre", "Fecha y hora");
 private final JTable tr = tabla(mr), tp = tabla(mp), te = tabla(me);
 private final JLabel status = new JLabel("Conectando con MySQL...");
 private int idRepartidor, idPedido, idEntrega;
 private boolean cargando;
 private record Datos(List<Repartidor> repartidores, List<Pedido> pedidos, List<Entrega> entregas) { }
 @FunctionalInterface private interface AccionSQL { void ejecutar() throws SQLException; }

 public VentanaPrincipal() {
  super("SpeedFast — Gestión de pedidos");
  setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1080, 650); setMinimumSize(new Dimension(920, 540));
  JPanel contenido = new JPanel(new BorderLayout(12, 12)); contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
  JLabel titulo = new JLabel("SpeedFast | Gestión de pedidos y entregas"); titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
  contenido.add(titulo, BorderLayout.NORTH);
  JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Repartidores", panelRepartidores()); tabs.addTab("Pedidos", panelPedidos()); tabs.addTab("Entregas", panelEntregas());
  contenido.add(tabs, BorderLayout.CENTER);
  JPanel pie = new JPanel(new BorderLayout()); pie.add(status, BorderLayout.CENTER);
  JButton actualizar = new JButton("Actualizar todo"); actualizar.addActionListener(e -> ejecutar(() -> {}, null)); pie.add(actualizar, BorderLayout.EAST);
  contenido.add(pie, BorderLayout.SOUTH);
  aplicarColores(contenido);
  titulo.setForeground(new Color(96, 65, 145));
  setContentPane(contenido);
  JPanel bloqueo = new JPanel(); bloqueo.setOpaque(false);
  bloqueo.addMouseListener(new MouseAdapter() {}); bloqueo.addKeyListener(new KeyAdapter() {}); bloqueo.setFocusable(true); setGlassPane(bloqueo);
  setLocationRelativeTo(null); limpiarEntrega();
  SwingUtilities.invokeLater(() -> ejecutar(() -> { try (Connection c = ConexionDB.getConnection()) { if (!c.isValid(3)) throw new SQLException("La conexión no es válida."); } }, null));
 }
 private JPanel panelRepartidores() {
  JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT)); formulario.add(new JLabel("Nombre")); formulario.add(nombre);
  JPanel botones = botones(() -> guardarRepartidor(false), () -> guardarRepartidor(true), () -> eliminar("repartidor", idRepartidor, () -> repartidorDAO.delete(idRepartidor)), this::limpiarRepartidor);
  tr.getSelectionModel().addListSelectionListener(e -> {
   if (!e.getValueIsAdjusting() && tr.getSelectedRow() >= 0) {
    int fila = tr.convertRowIndexToModel(tr.getSelectedRow()); idRepartidor = (int) mr.getValueAt(fila, 0); nombre.setText((String) mr.getValueAt(fila, 1));
   }
  });
  return panel(formulario, tr, botones);
 }
 private JPanel panelPedidos() {
  JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT)); campos.add(new JLabel("Dirección")); campos.add(direccion); campos.add(new JLabel("Tipo")); campos.add(tipo); campos.add(new JLabel("Estado")); campos.add(estado);
  JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT)); filtros.add(new JLabel("Filtrar tipo")); filtros.add(filtroTipo); filtros.add(new JLabel("Filtrar estado")); filtros.add(filtroEstado);
  filtroTipo.addActionListener(e -> filtrarPedidos()); filtroEstado.addActionListener(e -> filtrarPedidos());
  JPanel formulario = new JPanel(new GridLayout(2, 1)); formulario.add(campos); formulario.add(filtros);
  tp.getSelectionModel().addListSelectionListener(e -> {
   if (!e.getValueIsAdjusting() && tp.getSelectedRow() >= 0) {
    int f = tp.convertRowIndexToModel(tp.getSelectedRow()); idPedido = (int) mp.getValueAt(f, 0); direccion.setText((String) mp.getValueAt(f, 1)); tipo.setSelectedItem(TipoPedido.valueOf((String) mp.getValueAt(f, 2))); estado.setSelectedItem(EstadoPedido.valueOf((String) mp.getValueAt(f, 3)));
   }
  });
  return panel(formulario, tp, botones(() -> guardarPedido(false), () -> guardarPedido(true), () -> eliminar("pedido", idPedido, () -> pedidoDAO.delete(idPedido)), this::limpiarPedido));
 }
 private JPanel panelEntregas() {
  pedido.setPreferredSize(new Dimension(300, 27)); repartidor.setPreferredSize(new Dimension(230, 27));
  JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT)); campos.add(new JLabel("Pedido")); campos.add(pedido); campos.add(new JLabel("Repartidor")); campos.add(repartidor);
  JPanel tiempo = new JPanel(new FlowLayout(FlowLayout.LEFT)); tiempo.add(new JLabel("Fecha y hora (yyyy-MM-dd HH:mm)")); tiempo.add(fecha);
  JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT)); filtros.add(new JLabel("Por pedido")); filtros.add(filtroPedido); filtros.add(new JLabel("Por repartidor")); filtros.add(filtroRepartidor);
  filtroPedido.addActionListener(e -> filtrarEntregas()); filtroRepartidor.addActionListener(e -> filtrarEntregas());
  JPanel formulario = new JPanel(new GridLayout(3, 1)); formulario.add(campos); formulario.add(tiempo); formulario.add(filtros);
  te.getSelectionModel().addListSelectionListener(e -> {
   if (!e.getValueIsAdjusting() && te.getSelectedRow() >= 0) {
    int f = te.convertRowIndexToModel(te.getSelectedRow()); idEntrega = (int) me.getValueAt(f, 0);
    seleccionarPedido((int) me.getValueAt(f, 1)); seleccionarRepartidor((int) me.getValueAt(f, 3)); fecha.setText((String) me.getValueAt(f, 5));
   }
  });
  return panel(formulario, te, botones(() -> guardarEntrega(false), () -> guardarEntrega(true), () -> eliminar("entrega", idEntrega, () -> entregaDAO.delete(idEntrega)), this::limpiarEntrega));
 }
 private void guardarRepartidor(boolean editar) {
  try {
   if (editar) seleccionado(idRepartidor);
   Repartidor r = new Repartidor(editar ? idRepartidor : 0, Validaciones.texto(nombre.getText(), "nombre", 100));
   ejecutar(() -> { if (editar) repartidorDAO.update(r); else repartidorDAO.create(r); }, editar ? "Repartidor actualizado." : "Repartidor registrado.");
  } catch (IllegalArgumentException e) { Mensajes.error(this, e); }
 }
 private void guardarPedido(boolean editar) {
  try {
   if (editar) seleccionado(idPedido);
   Pedido p = new Pedido(editar ? idPedido : 0, Validaciones.texto(direccion.getText(), "dirección", 255), (TipoPedido) tipo.getSelectedItem(), (EstadoPedido) estado.getSelectedItem());
   if (p.getTipo() == null || p.getEstado() == null) throw new IllegalArgumentException("Seleccione tipo y estado.");
   ejecutar(() -> { if (editar) pedidoDAO.update(p); else pedidoDAO.create(p); }, editar ? "Pedido actualizado." : "Pedido registrado.");
  } catch (IllegalArgumentException e) { Mensajes.error(this, e); }
 }
 private void guardarEntrega(boolean editar) {
  try {
   if (editar) seleccionado(idEntrega);
   Pedido p = (Pedido) pedido.getSelectedItem(); Repartidor r = (Repartidor) repartidor.getSelectedItem();
   if (p == null || r == null) throw new IllegalArgumentException("Primero registre y seleccione un pedido y un repartidor.");
   Entrega entrega = new Entrega(editar ? idEntrega : 0, p.getId(), r.getId(), Validaciones.fecha(fecha.getText()));
   ejecutar(() -> { if (editar) entregaDAO.update(entrega); else entregaDAO.create(entrega); }, editar ? "Entrega actualizada." : "Entrega registrada.");
  } catch (IllegalArgumentException e) { Mensajes.error(this, e); }
 }
 private void eliminar(String entidad, int id, AccionSQL accion) {
  try { seleccionado(id); } catch (IllegalArgumentException e) { Mensajes.error(this, e); return; }
  if (JOptionPane.showConfirmDialog(this, "¿Eliminar " + entidad + " ID " + id + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) ejecutar(accion, "Registro eliminado.");
 }
 /** Ejecuta persistencia en segundo plano y aplica la nueva información en el EDT. */
 private void ejecutar(AccionSQL accion, String exito) {
  if (getGlassPane().isVisible()) return;
  getGlassPane().setVisible(true); getGlassPane().requestFocusInWindow(); setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); status.setText("Procesando...");
  new SwingWorker<Datos, Void>() {
   private boolean realizada;
   @Override protected Datos doInBackground() throws Exception {
    accion.ejecutar(); realizada = true;
    return new Datos(repartidorDAO.readAll(), pedidoDAO.readAll(), entregaDAO.readAll());
   }
   @Override protected void done() {
    try {
     mostrar(get()); status.setText("Conexión activa | Datos actualizados desde MySQL");
     if (exito != null) { limpiarRepartidor(); limpiarPedido(); limpiarEntrega(); JOptionPane.showMessageDialog(VentanaPrincipal.this, exito); }
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Operación interrumpida."); }
    catch (ExecutionException e) {
     status.setText("Revise la conexión y pulse Actualizar todo.");
     if (realizada && exito != null) { limpiarRepartidor(); limpiarPedido(); limpiarEntrega(); JOptionPane.showMessageDialog(VentanaPrincipal.this, exito + " No se pudo refrescar la vista. Pulse Actualizar todo."); }
     Throwable causa = e.getCause(); Mensajes.error(VentanaPrincipal.this, causa instanceof Exception ? (Exception) causa : new Exception(causa));
    } finally { getGlassPane().setVisible(false); setCursor(Cursor.getDefaultCursor()); }
   }
  }.execute();
 }
 private void mostrar(Datos d) {
  cargando = true;
  try {
   int previoP = pedido.getSelectedItem() instanceof Pedido p ? p.getId() : 0;
   int previoR = repartidor.getSelectedItem() instanceof Repartidor r ? r.getId() : 0;
   String fp = (String) filtroPedido.getSelectedItem(), fr = (String) filtroRepartidor.getSelectedItem();
   mr.setRowCount(0); mp.setRowCount(0); me.setRowCount(0); idRepartidor = idPedido = idEntrega = 0;
   pedido.removeAllItems(); repartidor.removeAllItems(); filtroPedido.removeAllItems(); filtroRepartidor.removeAllItems(); filtroPedido.addItem("Todos"); filtroRepartidor.addItem("Todos");
   for (Repartidor r : d.repartidores()) { mr.addRow(new Object[]{r.getId(), r.getNombre()}); repartidor.addItem(r); filtroRepartidor.addItem(r.toString()); }
   for (Pedido p : d.pedidos()) { mp.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo().name(), p.getEstado().name()}); pedido.addItem(p); filtroPedido.addItem(p.toString()); }
   for (Entrega e : d.entregas()) {
    String dir = d.pedidos().stream().filter(p -> p.getId() == e.getPedidoId()).map(Pedido::getDireccion).findFirst().orElse("Eliminado");
    String nom = d.repartidores().stream().filter(r -> r.getId() == e.getRepartidorId()).map(Repartidor::getNombre).findFirst().orElse("Eliminado");
    me.addRow(new Object[]{e.getId(), e.getPedidoId(), dir, e.getRepartidorId(), nom, e.getFechaHora().format(Validaciones.FECHA)});
   }
   seleccionarPedido(previoP); seleccionarRepartidor(previoR);
   restaurarFiltro(filtroPedido, fp); restaurarFiltro(filtroRepartidor, fr);
  } finally { cargando = false; }
  filtrarPedidos(); filtrarEntregas();
 }
 private void restaurarFiltro(JComboBox<String> combo, String valor) {
  for (int i = 0; i < combo.getItemCount(); i++) if (combo.getItemAt(i).equals(valor)) { combo.setSelectedIndex(i); return; }
  combo.setSelectedIndex(0);
 }
 private void seleccionarPedido(int id) { for (int i = 0; i < pedido.getItemCount(); i++) if (pedido.getItemAt(i).getId() == id) { pedido.setSelectedIndex(i); return; } }
 private void seleccionarRepartidor(int id) { for (int i = 0; i < repartidor.getItemCount(); i++) if (repartidor.getItemAt(i).getId() == id) { repartidor.setSelectedIndex(i); return; } }
 private void filtrarPedidos() {
  if (cargando) return;
  @SuppressWarnings("unchecked") TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tp.getRowSorter();
  sorter.setRowFilter(new RowFilter<>() { public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) { return (filtroTipo.getSelectedIndex() == 0 || e.getStringValue(2).equals(filtroTipo.getSelectedItem())) && (filtroEstado.getSelectedIndex() == 0 || e.getStringValue(3).equals(filtroEstado.getSelectedItem())); } });
  limpiarPedido();
 }
 private void filtrarEntregas() {
  if (cargando) return;
  String p = (String) filtroPedido.getSelectedItem(), r = (String) filtroRepartidor.getSelectedItem();
  @SuppressWarnings("unchecked") TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) te.getRowSorter();
  sorter.setRowFilter(new RowFilter<>() { public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) { return (p == null || p.equals("Todos") || p.startsWith(e.getStringValue(1) + " - ")) && (r == null || r.equals("Todos") || r.startsWith(e.getStringValue(3) + " - ")); } });
  limpiarEntrega();
 }
 private void limpiarRepartidor() { idRepartidor = 0; tr.clearSelection(); nombre.setText(""); }
 private void limpiarPedido() { idPedido = 0; tp.clearSelection(); direccion.setText(""); tipo.setSelectedIndex(0); estado.setSelectedIndex(0); }
 private void limpiarEntrega() { idEntrega = 0; te.clearSelection(); fecha.setText(LocalDateTime.now().format(Validaciones.FECHA)); }
 private void seleccionado(int id) { if (id <= 0) throw new IllegalArgumentException("Seleccione un registro en la tabla."); }
 private static DefaultTableModel modelo(String... columnas) { return new DefaultTableModel(columnas, 0) { @Override public boolean isCellEditable(int fila, int col) { return false; } @Override public Class<?> getColumnClass(int col) { return col == 0 || getColumnName(col).endsWith(" ID") ? Integer.class : String.class; } }; }
 private static JTable tabla(DefaultTableModel modelo) { JTable t = new JTable(modelo); t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); t.setAutoCreateRowSorter(true); t.setRowHeight(26); t.getTableHeader().setReorderingAllowed(false); return t; }
 private static JPanel botones(Runnable crear, Runnable editar, Runnable eliminar, Runnable limpiar) {
  JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT)); Runnable[] acciones = {crear, editar, eliminar, limpiar}; String[] etiquetas = {"Registrar nuevo", "Guardar cambios", "Eliminar", "Limpiar selección"};
  for (int i = 0; i < acciones.length; i++) { JButton b = new JButton(etiquetas[i]); Runnable accion = acciones[i]; b.addActionListener(e -> accion.run()); p.add(b); } return p;
 }
 private static JPanel panel(JPanel formulario, JTable tabla, JPanel botones) { JPanel p = new JPanel(new BorderLayout(8, 8)); p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); p.add(formulario, BorderLayout.NORTH); p.add(new JScrollPane(tabla), BorderLayout.CENTER); p.add(botones, BorderLayout.SOUTH); return p; }
 private static void aplicarColores(Component componente) {
  Color fondo = new Color(247, 245, 252);
  Color morado = new Color(106, 76, 156);

  if (componente instanceof JPanel panel) {
   panel.setBackground(fondo);
  }

  if (componente instanceof JLabel etiqueta) {
   etiqueta.setForeground(new Color(55, 45, 70));
  }

  if (componente instanceof JButton boton) {
   Color color = switch (boton.getText()) {
    case "Registrar nuevo" -> new Color(45, 120, 90);
    case "Eliminar" -> new Color(180, 55, 70);
    case "Limpiar selección" -> new Color(95, 100, 115);
    default -> morado;
   };

   boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
   boton.setBackground(color);
   boton.setForeground(Color.WHITE);
   boton.setOpaque(true);
   boton.setFocusPainted(false);
   boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
   boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  if (componente instanceof JTable tabla) {
   tabla.setBackground(Color.WHITE);
   tabla.setForeground(new Color(55, 45, 70));
   tabla.setSelectionBackground(new Color(225, 213, 245));
   tabla.setSelectionForeground(new Color(55, 35, 85));
   tabla.setGridColor(new Color(230, 225, 238));
  }

  if (componente instanceof JViewport vista) {
   vista.setBackground(Color.WHITE);
  }

  if (componente instanceof JTabbedPane pestañas) {
   pestañas.setBackground(new Color(235, 226, 248));
   pestañas.setForeground(morado);
  }

  if (componente instanceof Container contenedor) {
   for (Component hijo : contenedor.getComponents()) {
    aplicarColores(hijo);
   }
  }
 }
}

