-- HogarYA - DAOS - S01 Facturas, S02 Pagos, S03 Incidentes
-- Datos de prueba reconstruidos de las capturas de MySQL Workbench.
-- Ejecutar una sola vez sobre tablas vacias, creadas previamente por Hibernate.
-- No elimina ni modifica registros existentes.
CREATE DATABASE IF NOT EXISTS `01-Ventura-Bertole-Ortega`;
USE `01-Ventura-Bertole-Ortega`;
START TRANSACTION;

INSERT INTO contratos (id, dia_vencimiento, duracion_meses, eliminado, estado, fecha_inicio, importe_mensual) VALUES
(1,20,12,0,'ACTIVO','2026-11-01',350000.00),
(2,31,12,0,'ACTIVO','2026-01-01',450000.00),
(3,20,12,0,'BORRADOR','2026-11-01',300000.00),
(4,20,12,1,'ACTIVO','2026-11-01',300000.00),
(5,20,12,0,'ACTIVO','2024-01-01',300000.00);

INSERT INTO facturas (id, concepto, estado, facturacion_mensual, fecha_emision, fecha_vencimiento, importe, periodo, contrato_id) VALUES
(1,'Alquiler noviembre 2026','ANULADA',1,'2026-11-13','2026-11-20',350000.00,'2026-11',1),
(2,'Nueva factura alquiler noviembre 2026','PAGADA',1,'2026-11-20','2026-11-20',350000.00,'2026-11',1),
(3,'Cargo adicional de noviembre','PENDIENTE',0,'2026-11-02','2026-11-10',15000.00,'2026-11',1),
(4,'Alquiler febrero 2026','PENDIENTE',1,'2026-02-01','2026-02-28',350000.00,'2026-02',2);
-- El estado VENCIDA se calcula en la aplicacion segun la fecha de consulta.

INSERT INTO pagos (id, fecha_pago, importe_pagado, interes, medio_pago, factura_id) VALUES
(1,'2026-11-24',350000.00,0.00,'TRANSFERENCIA',2);

-- Incidente 1: datos historicos de pruebas anteriores a la correccion de transiciones.
-- La descripcion completa se recupera del texto de la prueba previa.
INSERT INTO incidentes (id, categoria, contrato_id, descripcion, estado, fecha_hora_alta, prioridad, titulo) VALUES
(1,'PLOMERIA',1,'Se detecta una perdida de agua debajo del lavamanos.','REABIERTO','2026-11-24 20:29:41.458809','ALTA','Perdida de agua en el baño'),
(2,'ELECTRICIDAD',1,'No funciona un tomacorriente de la cocina','CANCELADO','2026-11-25 18:09:06.174067','MEDIA','Problema electrico');

-- Las filas 3 y 4 conservan pruebas historicas, no demuestran una resolucion mediante S04.
INSERT INTO historial_estado_incidente (id, estado_anterior, estado_nuevo, fecha_hora, incidente_id) VALUES
(1,NULL,'ABIERTO','2026-11-24 20:29:41.458809',1),
(2,'ABIERTO','EN_PROCESO','2026-11-25 17:35:34.873136',1),
(3,'EN_PROCESO','RESUELTO','2026-11-25 17:37:36.701740',1),
(4,'RESUELTO','REABIERTO','2026-11-25 17:37:45.945670',1),
(5,NULL,'ABIERTO','2026-11-25 18:09:06.174067',2),
(6,'ABIERTO','EN_PROCESO','2026-11-25 18:09:41.461227',2),
(7,'EN_PROCESO','CANCELADO','2026-11-25 18:10:08.953702',2);
COMMIT;

SELECT * FROM contratos;
SELECT * FROM facturas;
SELECT * FROM pagos;
SELECT * FROM incidentes;
SELECT * FROM historial_estado_incidente;
