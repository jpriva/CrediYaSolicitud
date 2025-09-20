-- V1__create_loan_type_solicitude_and_state_tables.sql

CREATE TABLE estados (
    id_estado INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    PRIMARY KEY (id_estado)
);

CREATE TABLE tipo_prestamo (
    id_tipo_prestamo INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    monto_minimo DECIMAL(18, 2) NOT NULL,
    monto_maximo DECIMAL(18, 2) NOT NULL,
    tasa_interes DECIMAL(8, 6) NOT NULL,
    validacion_automatica BIT(1) NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_tipo_prestamo)
);

CREATE TABLE solicitud (
    id_solicitud INT NOT NULL AUTO_INCREMENT,
    monto DECIMAL(18, 2) NOT NULL,
    plazo INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    id_estado INT NOT NULL,
    id_tipo_prestamo INT NOT NULL,

    PRIMARY KEY (id_solicitud),

    CONSTRAINT fk_estado
        FOREIGN KEY(id_estado)
        REFERENCES estados(id_estado),

    CONSTRAINT fk_tipo_prestamo
        FOREIGN KEY(id_tipo_prestamo)
        REFERENCES tipo_prestamo(id_tipo_prestamo)
);

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
('CREDI-HOGAR', 50000000.00, 800000000.00, 7.20, FALSE),
('CREDI-ESTUDIO', 2000000.00, 80000000.00, 4.95, TRUE),
('CREDI-VEHICULO', 10000000.00, 150000000.00, 8.50, FALSE),
('CREDI-LIBRE-INVERSION', 1000000.00, 50000000.00, 11.80, TRUE),
('CREDI-PYME', 20000000.00, 500000000.00, 9.10, FALSE),
('CREDI-AGRO', 10000000.00, 200000000.00, 6.90, FALSE),
('CREDI-EMPRESARIAL', 100000000.00, 2000000000.00, 8.75, FALSE),
('CREDI-CONSOLIDACION', 5000000.00, 100000000.00, 10.50, TRUE),
('CREDI-VIAJES', 1000000.00, 20000000.00, 13.10, TRUE),
('CREDI-TECNOLOGIA', 800000.00, 15000000.00, 12.45, TRUE);