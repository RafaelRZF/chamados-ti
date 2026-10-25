-- Sistema de controle de chamados de TI - TechServ
-- Execute este script uma vez antes de rodar a aplicação.
-- Compatível com MySQL 8.0.16+ (CHECK é aplicado a partir desta versão).

CREATE DATABASE IF NOT EXISTS techserv_chamados
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE techserv_chamados;

CREATE TABLE IF NOT EXISTS chamados (
    id            INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    solicitante   VARCHAR(100)  NOT NULL,
    descricao     VARCHAR(500)  NOT NULL,
    prioridade    ENUM('BAIXA','MEDIA','ALTA') NOT NULL,
    status        ENUM('ABERTO','EM_ATENDIMENTO','CONCLUIDO') NOT NULL DEFAULT 'ABERTO',
    data_abertura DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_chamados_status (status),
    INDEX idx_chamados_prioridade (prioridade),
    CONSTRAINT ck_solicitante_min CHECK (CHAR_LENGTH(TRIM(solicitante)) >= 3),
    CONSTRAINT ck_descricao_min   CHECK (CHAR_LENGTH(TRIM(descricao))   >= 10)
) ENGINE = InnoDB;
