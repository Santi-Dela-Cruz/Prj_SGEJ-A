package application.service;

import java.util.ArrayList;
import java.util.List;
import application.dao.PersonalDAO;
import application.model.Personal;

/**
 * Servicio para gestionar las operaciones relacionadas con los empleados.
 */
public class EmpleadoService {
    
    private PersonalDAO personalDAO;
    
    public EmpleadoService() {
        this.personalDAO = new PersonalDAO();
    }
    
    /**
     * Registra un nuevo empleado en el sistema.
     * 
     * @param personal El objeto Personal a registrar
     * @return El ID del empleado registrado o -1 si hubo error
     */
    public int registrarEmpleado(Personal personal) {
        return personalDAO.registrarPersonal(personal);
    }
    
    /**
     * Actualiza la información de un empleado existente.
     * 
     * @param personal El objeto Personal con la información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarEmpleado(Personal personal) {
        return personalDAO.actualizarPersonal(personal);
    }
    
    /**
     * Elimina un empleado del sistema.
     * 
     * @param id El ID del empleado a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminarEmpleado(int id) {
        return personalDAO.eliminarPersonal(id);
    }
    
    /**
     * Obtiene un empleado por su ID.
     * 
     * @param id El ID del empleado a buscar
     * @return El objeto Personal encontrado, o null si no existe
     */
    public Personal obtenerEmpleadoPorId(int id) {
        return personalDAO.obtenerPersonalPorId(id);
    }
    
    /**
     * Obtiene un empleado por su número de identificación.
     * 
     * @param numeroIdentificacion El número de identificación del empleado
     * @return El objeto Personal encontrado, o null si no existe
     */
    public Personal obtenerEmpleadoPorIdentificacion(String numeroIdentificacion) {
        return personalDAO.obtenerPersonalPorIdentificacion(numeroIdentificacion);
    }
    
    /**
     * Obtiene todos los empleados registrados en el sistema.
     * 
     * @return Lista de objetos Personal
     */
    public List<Personal> obtenerTodosLosEmpleados() {
        return personalDAO.obtenerTodosLosEmpleados();
    }
    
    /**
     * Obtiene una lista de todos los empleados con un rol específico.
     * 
     * @param rol El rol por el cual filtrar los empleados (ej. "Abogado", "Administrador", etc.)
     * @return Lista de empleados que tienen el rol especificado
     */
    public List<Empleado> getEmpleadosByRol(String rol) {
        List<Personal> personalList = personalDAO.obtenerPersonalPorRol(rol);
        List<Empleado> empleados = new ArrayList<>();
        
        for (Personal persona : personalList) {
            empleados.add(new Empleado(
                persona.getId(),
                persona.getNombres(),
                persona.getApellidos(),
                persona.getNumeroIdentificacion(),
                persona.getCorreo(),
                persona.getRol()
            ));
        }
        
        return empleados;
    }
    
    /**
     * Clase interna para representar datos básicos de un empleado.
     */
    public static class Empleado {
        private final int id;
        private final String nombres;
        private final String apellidos;
        private final String numeroIdentificacion;
        private final String correo;
        private final String rol;
        
        public Empleado(int id, String nombres, String apellidos, String numeroIdentificacion, 
                        String correo, String rol) {
            this.id = id;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.numeroIdentificacion = numeroIdentificacion;
            this.correo = correo;
            this.rol = rol;
        }
        
        public int getId() {
            return id;
        }
        
        public String getNombres() {
            return nombres;
        }
        
        public String getApellidos() {
            return apellidos;
        }
        
        public String getNumeroIdentificacion() {
            return numeroIdentificacion;
        }
        
        public String getCorreo() {
            return correo;
        }
        
        public String getRol() {
            return rol;
        }
    }
}
