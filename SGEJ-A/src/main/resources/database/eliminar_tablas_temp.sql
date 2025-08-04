-- Script para eliminar todas las tablas de respaldo y temporales

-- Eliminar tabla de respaldo de parámetros si existe
DROP TABLE IF EXISTS parametro_backup;
DROP TABLE IF EXISTS parametro_temp;

-- Verificar y eliminar otras tablas temporales que puedan existir
DROP TABLE IF EXISTS temp_parametro;
DROP TABLE IF EXISTS backup_parametro;
