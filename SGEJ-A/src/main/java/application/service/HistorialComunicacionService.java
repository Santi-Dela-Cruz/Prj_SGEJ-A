package application.service;

import application.dao.HistorialComunicacionDAO;
import application.model.HistorialComunicacion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;

public class HistorialComunicacionService {
    private HistorialComunicacionDAO dao;

    public HistorialComunicacionService(Connection conn) {
        this.dao = new HistorialComunicacionDAO(conn);
    }

    /**
     * Registra una nueva comunicación después de validar los datos
     * 
     * @param com Objeto HistorialComunicacion con los datos a registrar
     * @return Mensaje de resultado, vacío si todo está bien
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public String registrarComunicacion(HistorialComunicacion com) throws SQLException {
        // Validaciones
        if (com.getCasoId() <= 0) {
            return "El caso especificado no es válido";
        }

        if (com.getAbogadoId() <= 0) {
            return "El abogado especificado no es válido";
        }

        if (com.getTipo() == null || com.getTipo().trim().isEmpty()) {
            return "El tipo de comunicación es obligatorio";
        }

        if (com.getDescripcion() == null || com.getDescripcion().trim().isEmpty()) {
            return "La descripción de la comunicación es obligatoria";
        }

        // Verificar existencia del caso y abogado
        if (!dao.verificarExistenciaCaso(com.getCasoId())) {
            return "El caso especificado no existe";
        }

        if (!dao.verificarExistenciaAbogado(com.getAbogadoId())) {
            return "El abogado especificado no existe";
        }

        // Asegurar que la fecha sea la actual si no se proporciona
        if (com.getFecha() == null) {
            com.setFecha(new Date());
        }

        // Registrar la comunicación
        dao.insertarComunicacion(com);
        return ""; // Éxito - cadena vacía
    }

    /**
     * Obtiene las comunicaciones asociadas a un caso
     * 
     * @param casoId ID del caso
     * @return Lista de comunicaciones
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public List<HistorialComunicacion> obtenerPorCaso(int casoId) throws SQLException {
        return dao.consultarPorCaso(casoId);
    }
    
    /**
     * Obtiene todas las comunicaciones registradas en la base de datos
     * 
     * @return Lista de todas las comunicaciones
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public List<HistorialComunicacion> obtenerTodasLasComunicaciones() throws SQLException {
        return dao.consultarTodasLasComunicaciones();
    }

    /**
     * Obtiene el ID de un caso a partir del número de expediente
     * 
     * @param numeroExpediente Número de expediente a buscar
     * @return ID del caso o -1 si no se encuentra
     */
    public int obtenerIdCasoPorExpediente(String numeroExpediente) {
        return dao.obtenerIdCasoPorExpediente(numeroExpediente);
    }

    /**
     * Verifica si un abogado existe en la base de datos
     * 
     * @param abogadoId ID del abogado
     * @return true si existe, false en caso contrario
     */
    public boolean verificarExistenciaAbogado(int abogadoId) {
        return dao.verificarExistenciaAbogado(abogadoId);
    }
}
