
| Caso | Acción | Resultado esperado |
|---|---|---|
| Conexión | Iniciar MySQL y abrir Main | Barra con conexión activa y tres pestañas |
| Repartidor crear | Registrar Ana | Fila nueva con ID |
| Repartidor leer | Pulsar Actualizar todo | Ana sigue en la tabla y en combo de entregas |
| Repartidor editar | Seleccionar Ana y cambiar a Andrea | Tabla y combo muestran Andrea |
| Nombre obligatorio | Registrar espacios | Mensaje; ningún registro nuevo |
| Longitud nombre | Registrar más de 100 caracteres | Mensaje de validación |
| Pedido crear | Registrar dirección, COMIDA y PENDIENTE | Fila y combo nuevos |
| Dirección obligatoria | Registrar dirección vacía | Mensaje; ningún registro nuevo |
| Pedido editar | Cambiar tipo a EXPRESS y estado a EN_REPARTO | Cambio persistido |
| Filtros pedidos | Elegir tipo y estado | Solo filas que cumplen ambos filtros |
| Entrega crear | Elegir pedido, repartidor y fecha válida | Entrega asociada visible en tabla |
| Fecha incorrecta | Usar 2026-02-30 17:30 o 25:30 como hora | Mensaje; no guardar |
| Entrega editar | Seleccionar entrega, cambiar fecha y repartidor | Cambios persistidos |
| Filtros entregas | Filtrar por pedido y repartidor | Solo relaciones coincidentes |
| Restricción pedido | Eliminar pedido con entrega | Mensaje; conserva pedido y entrega |
| Restricción repartidor | Eliminar repartidor con entrega | Mensaje; conserva ambos |
| Entrega eliminar | Seleccionar entrega y confirmar eliminar | Entrega desaparece |
| Cancelar eliminación | Seleccionar fila, eliminar y responder No | Conserva el registro |
| Pedido eliminar | Eliminar pedido sin entregas | Se elimina y desaparece del combo |
| Repartidor eliminar | Eliminar repartidor sin entregas | Se elimina y desaparece del combo |
| Selección obligatoria | Guardar cambios o eliminar sin selección | Mensaje de validación |
| Orden tabla | Ordenar por encabezado y editar fila | Modifica la fila seleccionada según su ID |
| Persistencia | Registrar datos, cerrar y reabrir | Datos siguen disponibles |
| Error conexión | Detener MySQL y actualizar | Mensaje claro; ventana sigue abierta |
| Recuperación | Reiniciar MySQL y actualizar | Recarga tablas y combos |
| Credenciales | Usar contraseña incorrecta | Mensaje sobre acceso o conexión |
| Parámetros seguros | Usar apóstrofe en nombre y texto SQL en dirección | Texto se almacena como dato |

