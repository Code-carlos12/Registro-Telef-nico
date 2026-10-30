CREATE DATABASE IF NOT EXISTS registrotelefonico;
USE registrotelefonico;

CREATE TABLE IF NOT EXISTS persona (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    edad     INT          NOT NULL,
    telefono VARCHAR(20)  NOT NULL
);
