-- Script para crear la tabla de usuarios en la base de datos Gestordegastos
-- Ejecutar este script en MySQL para crear la estructura necesaria

USE Gestordegastos;

-- Crear tabla de usuarios si no existe (usando tu estructura)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE
);

-- Insertar algunos usuarios de prueba
INSERT INTO usuario (nombre_usuario, contrasena, correo) VALUES
('admin', 'admin123', 'admin@paywise.com'),
('usuario1', 'password123', 'usuario1@paywise.com'),
('phamela', 'phamela123', 'phamela@paywise.com');

-- Verificar que se crearon los usuarios
SELECT id_usuario, nombre_usuario, correo FROM usuario;
