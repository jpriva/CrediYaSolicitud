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
    plazo VARCHAR(50) NOT NULL,
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

INSERT INTO estados (nombre, descripcion) VALUES
('PENDIENTE', 'La solicitud está pendiente de revisión.'),
('APROBADO', 'La solicitud ha sido aprobada.'),
('RECHAZADO', 'La solicitud ha sido rechazada.');