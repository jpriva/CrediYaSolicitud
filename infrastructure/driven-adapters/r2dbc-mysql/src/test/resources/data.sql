-- Insertar estados
INSERT INTO estados (id_estado, nombre, descripcion) VALUES (1, 'PENDIENTE', 'Solicitud pendiente de revisión');
INSERT INTO estados (id_estado, nombre, descripcion) VALUES (2, 'APROBADO', 'Solicitud aprobada');
INSERT INTO estados (id_estado, nombre, descripcion) VALUES (3, 'RECHAZADO', 'Solicitud rechazada');

-- Insertar tipos de préstamo
INSERT INTO tipo_prestamo (id_tipo_prestamo, nombre, tasa_interes, monto_minimo, monto_maximo) VALUES (1, 'PERSONAL', 9.5, 1000.00, 50000.00);
INSERT INTO tipo_prestamo (id_tipo_prestamo, nombre, tasa_interes, monto_minimo, monto_maximo) VALUES (2, 'VEHICULO', 7.2, 10000.00, 150000.00);

-- Insertar solicitudes de prueba
INSERT INTO solicitud (id_solicitud, monto, plazo, email, id_tipo_prestamo, id_estado) VALUES (100, 5000.00, 12, 'john.doe@example.com', 1, 2); -- APROBADO
INSERT INTO solicitud (id_solicitud, monto, plazo, email, id_tipo_prestamo, id_estado) VALUES (101, 15000.00, 24, 'jane.doe@example.com', 1, 1); -- PENDIENTE
INSERT INTO solicitud (id_solicitud, monto, plazo, email, id_tipo_prestamo, id_estado) VALUES (102, 25000.00, 36, 'peter.jones@example.com', 2, 3); -- RECHAZADO