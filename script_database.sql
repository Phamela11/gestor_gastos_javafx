CREATE DATABASE gestordegasto;
use gestordegasto;

-- crear tabla usuario 
CREATE TABLE usuario(
	id_usuario INT AUTO_INCREMENT PRIMARY KEY,
	nombre_usuario VARCHAR(50) NOT NULL,
	contrasena VARCHAR (100) NOT NULL
);

-- CREAR TABLA CATEGORIA 
CREATE TABLE categoria(
	id_categoria INT AUTO_INCREMENT PRIMARY KEY,
	id_usuario INT NOT NULL,
	nombre VARCHAR(50) NOT null,
	CONSTRAINT fk_categoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- Crear TABLA TRANSACCIONES 
CREATE TABLE transaccion(
	id_transaccion INT AUTO_INCREMENT PRIMARY KEY,
	id_usuario INT NOT NULL,
    id_categoria INT NOT NULL,
	tipo VARCHAR(50) NOT NULL,
	fecha DATE NOT NULL,
	descripcion VARCHAR(100),
	monto DECIMAL(10,2) NOT null,
	CONSTRAINT fk_transaccion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_transaccion_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE CASCADE
);

CREATE TABLE nota (
    id_nota INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_categoria_nota INT NULL,
    titulo VARCHAR(100) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    fecha_edicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_nota_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_nota_categoria FOREIGN KEY (id_categoria_nota) REFERENCES categoria_nota(id_categoria_nota) ON DELETE CASCADE
);



CREATE TABLE categoria_nota (
    id_categoria_nota INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    CONSTRAINT fk_categoria_nota_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

ALTER TABLE usuario
ADD COLUMN correo VARCHAR(100) NOT NULL;

ALTER TABLE usuario
DROP COLUMN email;


INSERT INTO usuario (nombre_usuario, contrasena, correo)
VALUES 
('Juan Pérez', 'clave123', 'juanperez@mail.com'),
('María Gómez', 'maria456', 'mariagomez@mail.com'),
('Carlos Ruiz', 'carlitox', 'carlosruiz@mail.com');

select * from usuario;