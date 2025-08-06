package application.service;

import application.dao.PersonalDAO;
import application.model.Personal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para operaciones con abogados
 */
public class AbogadoService {

    /**
     * Obtiene la lista de abogados (personal con rol 'abogado')
     * 
     * @return Lista de objetos Personal que son abogados
     */
    public static List<Personal> obtenerAbogados() {
        PersonalDAO dao = new PersonalDAO();

        // Buscar personal con rol 'abogado'
        List<Personal> abogados = dao.obtenerPersonalPorRol("abogado");

        // Si no hay resultados, devolver lista vacía
        if (abogados == null) {
            return new ArrayList<>();
        }

        return abogados;
    }

    /**
     * Obtiene un abogado por su ID
     * 
     * @param id ID del abogado a buscar
     * @return El objeto Personal si se encuentra, null en caso contrario
     */
    public static Personal obtenerAbogadoPorId(int id) {
        PersonalDAO dao = new PersonalDAO();
        return dao.obtenerPersonalPorId(id);
    }

    /**
     * Formatea el nombre completo de un abogado
     * 
     * @param abogado Objeto Personal
     * @return Nombre completo formateado
     */
    public static String formatearNombreCompleto(Personal abogado) {
        if (abogado == null) {
            return "Desconocido";
        }

        String nombres = abogado.getNombres() != null ? abogado.getNombres() : "";
        String apellidos = abogado.getApellidos() != null ? abogado.getApellidos() : "";

        return nombres + " " + apellidos;
    }
}
