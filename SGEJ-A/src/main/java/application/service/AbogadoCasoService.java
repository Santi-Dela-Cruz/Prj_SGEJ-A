package application.service;

import application.dao.AbogadoCasoDAO;
import application.database.DatabaseConnection;
import application.database.MigracionAbogadoCaso;
import application.model.Personal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con la asignación de abogados a casos
 */
public class AbogadoCasoService {
    private AbogadoCasoDAO dao;
    
    public AbogadoCasoService() {
        try {
            // Ejecutamos la migración para asegurar que las tablas estén correctamente configuradas
            MigracionAbogadoCaso.ejecutarMigracion();
            
            Connection conn = DatabaseConnection.getConnection();
            dao = new AbogadoCasoDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si un abogado está asignado a un caso específico
     * 
     * @param casoId ID del caso
     * @param abogadoId ID del abogado
     * @return true si el abogado está asignado al caso, false en caso contrario
     */
    public boolean verificarAbogadoAsignadoACaso(int casoId, int abogadoId) {
        return dao.verificarAbogadoAsignadoACaso(casoId, abogadoId);
    }
    
    /**
     * Obtiene todos los abogados asignados a un caso específico
     * 
     * @param casoId ID del caso
     * @return Lista de objetos Personal (abogados)
     */
    public List<Personal> obtenerAbogadosPorCaso(int casoId) {
        return dao.obtenerAbogadosPorCaso(casoId);
    }
    
    /**
     * Asigna un abogado como principal a un caso
     * 
     * @param casoId ID del caso
     * @param abogadoId ID del abogado
     * @return true si la asignación fue exitosa, false en caso contrario
     */
    public boolean asignarAbogadoPrincipal(int casoId, int abogadoId) {
        return dao.asignarAbogadoPrincipal(casoId, abogadoId);
    }
    
    /**
     * Asigna un abogado a un caso con un rol específico
     * 
     * @param casoId ID del caso
     * @param abogadoId ID del abogado
     * @param rol Rol del abogado en el caso (ej. "Principal", "Asistente", etc.)
     * @return true si la asignación fue exitosa, false en caso contrario
     */
    public boolean asignarAbogadoACaso(int casoId, int abogadoId, String rol) {
        return dao.asignarAbogadoACaso(casoId, abogadoId, rol);
    }
}
