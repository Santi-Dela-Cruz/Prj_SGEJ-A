package application.service;

import application.dao.ClienteDAO;
import application.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Servicio para la gestión de clientes
 * Actúa como intermediario entre la vista y el DAO
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Registrar un nuevo cliente
     * 
     * @param cliente el cliente a registrar
     * @return resultado de la operación
     */
    public ResultadoOperacion registrarCliente(Cliente cliente) {
        // Validaciones
        ResultadoOperacion validacion = validarCliente(cliente);
        if (!validacion.esExitoso()) {
            return validacion;
        }

        // Verificar si ya existe un cliente con la misma identificación
        if (clienteDAO.existeCliente(cliente.getNumeroIdentificacion())) {
            return new ResultadoOperacion(false,
                    "Ya existe un cliente con el número de identificación: " + cliente.getNumeroIdentificacion());
        }

        // Insertar el cliente
        boolean resultado = clienteDAO.insertarCliente(cliente);
        if (resultado) {
            return new ResultadoOperacion(true, "Cliente registrado exitosamente");
        } else {
            return new ResultadoOperacion(false, "Error al registrar el cliente");
        }
    }

    /**
     * Actualizar un cliente existente
     * 
     * @param cliente el cliente con los datos actualizados
     * @return resultado de la operación
     */
    public ResultadoOperacion actualizarCliente(Cliente cliente) {
        // Validaciones básicas
        if (cliente.getId() <= 0) {
            return new ResultadoOperacion(false, "ID de cliente inválido");
        }

        ResultadoOperacion validacion = validarDatosActualizables(cliente);
        if (!validacion.esExitoso()) {
            return validacion;
        }

        // Actualizar el cliente
        boolean resultado = clienteDAO.actualizarCliente(cliente);
        if (resultado) {
            return new ResultadoOperacion(true, "Cliente actualizado exitosamente");
        } else {
            return new ResultadoOperacion(false, "Error al actualizar el cliente");
        }
    }

    /**
     * Obtener un cliente por su ID
     * 
     * @param id el ID del cliente
     * @return el cliente encontrado o null
     */
    public Cliente obtenerClientePorId(int id) {
        return clienteDAO.obtenerClientePorId(id);
    }

    /**
     * Obtener un cliente por su número de identificación
     * 
     * @param numeroIdentificacion el número de identificación
     * @return el cliente encontrado o null
     */
    public Cliente obtenerClientePorIdentificacion(String numeroIdentificacion) {
        return clienteDAO.obtenerClientePorIdentificacion(numeroIdentificacion);
    }

    /**
     * Obtener todos los clientes como ObservableList para JavaFX
     * 
     * @return lista observable de clientes
     */
    public ObservableList<Cliente> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
        return FXCollections.observableArrayList(clientes);
    }

    /**
     * Obtener solo clientes activos (borrado lógico)
     * 
     * @return lista observable de clientes activos
     */
    public ObservableList<Cliente> obtenerClientesActivos() {
        List<Cliente> clientes = clienteDAO.obtenerClientesPorEstado(Cliente.Estado.ACTIVO);
        return FXCollections.observableArrayList(clientes);
    }

    /**
     * Obtener todos los clientes incluyendo inactivos (para administradores)
     * 
     * @return lista observable de todos los clientes
     */
    public ObservableList<Cliente> obtenerTodosLosClientesIncluirInactivos() {
        List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
        return FXCollections.observableArrayList(clientes);
    }

    /**
     * Buscar clientes por nombre
     * 
     * @param nombre el nombre a buscar
     * @return lista observable de clientes encontrados
     */
    public ObservableList<Cliente> buscarClientesPorNombre(String nombre) {
        List<Cliente> clientes = clienteDAO.buscarClientesPorNombre(nombre);
        return FXCollections.observableArrayList(clientes);
    }

    /**
     * Buscar clientes activos por nombre (borrado lógico)
     * 
     * @param nombre el nombre a buscar
     * @return lista observable de clientes activos encontrados
     */
    public ObservableList<Cliente> buscarClientesActivosPorNombre(String nombre) {
        List<Cliente> todosLosClientes = clienteDAO.buscarClientesPorNombre(nombre);
        // Filtrar solo los activos
        List<Cliente> clientesActivos = todosLosClientes.stream()
                .filter(cliente -> cliente.getEstado() == Cliente.Estado.ACTIVO)
                .toList();
        return FXCollections.observableArrayList(clientesActivos);
    }

    /**
     * Obtener clientes por estado
     * 
     * @param estado el estado a filtrar
     * @return lista observable de clientes con el estado especificado
     */
    public ObservableList<Cliente> obtenerClientesPorEstado(Cliente.Estado estado) {
        List<Cliente> clientes = clienteDAO.obtenerClientesPorEstado(estado);
        return FXCollections.observableArrayList(clientes);
    }

    /**
     * Verificar si existe un cliente con el número de identificación dado
     * 
     * @param numeroIdentificacion el número de identificación a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeCliente(String numeroIdentificacion) {
        return clienteDAO.existeCliente(numeroIdentificacion);
    }

    /**
     * Validar los datos de un cliente completo
     * 
     * @param cliente el cliente a validar
     * @return resultado de la validación
     */
    private ResultadoOperacion validarCliente(Cliente cliente) {
        if (cliente == null) {
            return new ResultadoOperacion(false, "El cliente no puede ser nulo");
        }

        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().trim().isEmpty()) {
            return new ResultadoOperacion(false, "El nombre completo es obligatorio");
        }

        if (cliente.getTipoIdentificacion() == null) {
            return new ResultadoOperacion(false, "El tipo de identificación es obligatorio");
        }

        if (cliente.getTipoPersona() == null) {
            return new ResultadoOperacion(false, "El tipo de persona es obligatorio");
        }

        if (cliente.getNumeroIdentificacion() == null || cliente.getNumeroIdentificacion().trim().isEmpty()) {
            return new ResultadoOperacion(false, "El número de identificación es obligatorio");
        }

        /*
         * if (cliente.getDireccion() == null ||
         * cliente.getDireccion().trim().isEmpty()) {
         * return new ResultadoOperacion(false, "La dirección es obligatoria");
         * }
         */

        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            return new ResultadoOperacion(false, "El teléfono es obligatorio");
        }

        if (cliente.getCorreoElectronico() == null || cliente.getCorreoElectronico().trim().isEmpty()) {
            return new ResultadoOperacion(false, "El correo electrónico es obligatorio");
        }

        // Validar formato de correo electrónico
        if (!cliente.getCorreoElectronico().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new ResultadoOperacion(false, "El formato del correo electrónico es inválido");
        }

        if (cliente.getEstado() == null) {
            return new ResultadoOperacion(false, "El estado es obligatorio");
        }

        // Validaciones específicas por tipo de persona
        /*
         * if (cliente.getTipoPersona() == Cliente.TipoPersona.NATURAL) {
         * if (cliente.getEstadoCivil() == null ||
         * cliente.getEstadoCivil().trim().isEmpty()) {
         * return new ResultadoOperacion(false,
         * "El estado civil es obligatorio para personas naturales");
         * }
         * } else
         */
        if (cliente.getTipoPersona() == Cliente.TipoPersona.JURIDICA) {
            if (cliente.getRepresentanteLegal() == null || cliente.getRepresentanteLegal().trim().isEmpty()) {
                return new ResultadoOperacion(false, "El representante legal es obligatorio para personas jurídicas");
            }
            if (cliente.getDireccionFiscal() == null || cliente.getDireccionFiscal().trim().isEmpty()) {
                return new ResultadoOperacion(false, "La dirección fiscal es obligatoria para personas jurídicas");
            }
        }

        return new ResultadoOperacion(true, "Validación exitosa");
    }

    /**
     * Validar solo los datos que se pueden actualizar
     * 
     * @param cliente el cliente a validar
     * @return resultado de la validación
     */
    private ResultadoOperacion validarDatosActualizables(Cliente cliente) {
        if (cliente == null) {
            return new ResultadoOperacion(false, "El cliente no puede ser nulo");
        }

        /*
         * if (cliente.getDireccion() == null ||
         * cliente.getDireccion().trim().isEmpty()) {
         * return new ResultadoOperacion(false, "La dirección es obligatoria");
         * }
         */

        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            return new ResultadoOperacion(false, "El teléfono es obligatorio");
        }

        if (cliente.getCorreoElectronico() == null || cliente.getCorreoElectronico().trim().isEmpty()) {
            return new ResultadoOperacion(false, "El correo electrónico es obligatorio");
        }

        // Validar formato de correo electrónico
        if (!cliente.getCorreoElectronico().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new ResultadoOperacion(false, "El formato del correo electrónico es inválido");
        }

        if (cliente.getEstado() == null) {
            return new ResultadoOperacion(false, "El estado es obligatorio");
        }

        // Validaciones específicas por tipo de persona (solo si están definidas)
        /*
         * if (cliente.getTipoPersona() == Cliente.TipoPersona.NATURAL) {
         * if (cliente.getEstadoCivil() != null &&
         * cliente.getEstadoCivil().trim().isEmpty()) {
         * return new ResultadoOperacion(false,
         * "El estado civil no puede estar vacío para personas naturales");
         * }
         * } else
         */
        if (cliente.getTipoPersona() == Cliente.TipoPersona.JURIDICA) {
            if (cliente.getRepresentanteLegal() != null && cliente.getRepresentanteLegal().trim().isEmpty()) {
                return new ResultadoOperacion(false,
                        "El representante legal no puede estar vacío para personas jurídicas");
            }
            if (cliente.getDireccionFiscal() != null && cliente.getDireccionFiscal().trim().isEmpty()) {
                return new ResultadoOperacion(false,
                        "La dirección fiscal no puede estar vacía para personas jurídicas");
            }
        }

        return new ResultadoOperacion(true, "Validación exitosa");
    }

    /**
     * Clase interna para manejar el resultado de las operaciones
     */
    public static class ResultadoOperacion {
        private final boolean exitoso;
        private final String mensaje;

        public ResultadoOperacion(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public boolean esExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
