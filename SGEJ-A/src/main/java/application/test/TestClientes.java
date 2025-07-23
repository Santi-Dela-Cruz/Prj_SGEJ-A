package application.test;

import application.database.DatabaseConnection;
import application.model.Cliente;
import application.service.ClienteService;
import javafx.collections.ObservableList;

import java.time.LocalDate;

/**
 * Clase de prueba para verificar el funcionamiento del módulo de clientes
 */
public class TestClientes {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DEL MÓDULO DE CLIENTES ===\n");
        
        // Inicializar base de datos
        DatabaseConnection.initializeDatabase();
        
        ClienteService clienteService = new ClienteService();
        
        // Crear clientes de prueba
        crearClientesPrueba(clienteService);
        
        // Listar todos los clientes
        listarClientes(clienteService);
        
        // Buscar cliente por identificación
        buscarClientePorIdentificacion(clienteService, "1234567890");
        
        // Buscar clientes por nombre
        buscarClientesPorNombre(clienteService, "Juan");
        
        // Actualizar cliente
        actualizarCliente(clienteService, "1234567890");
        
        // Listar clientes activos
        listarClientesActivos(clienteService);
        
        System.out.println("\n=== PRUEBA COMPLETADA ===");
        
        // Cerrar conexión
        DatabaseConnection.closeConnection();
    }
    
    private static void crearClientesPrueba(ClienteService clienteService) {
        System.out.println("1. Creando clientes de prueba...\n");
        
        // Cliente persona natural
        Cliente clienteNatural = new Cliente();
        clienteNatural.setNombreCompleto("Juan Pérez García");
        clienteNatural.setTipoIdentificacion(Cliente.TipoIdentificacion.CEDULA);
        clienteNatural.setTipoPersona(Cliente.TipoPersona.NATURAL);
        clienteNatural.setNumeroIdentificacion("1234567890");
        clienteNatural.setDireccion("Av. Principal 123, Quito");
        clienteNatural.setTelefono("0987654321");
        clienteNatural.setCorreoElectronico("juan.perez@email.com");
        clienteNatural.setEstado(Cliente.Estado.ACTIVO);
        clienteNatural.setFechaRegistro(LocalDate.now());
        clienteNatural.setEstadoCivil("Soltero");
        
        ClienteService.ResultadoOperacion resultado1 = clienteService.registrarCliente(clienteNatural);
        System.out.println("Cliente Natural: " + resultado1.getMensaje());
        
        // Cliente persona jurídica
        Cliente clienteJuridica = new Cliente();
        clienteJuridica.setNombreCompleto("Empresa ABC S.A.");
        clienteJuridica.setTipoIdentificacion(Cliente.TipoIdentificacion.RUC);
        clienteJuridica.setTipoPersona(Cliente.TipoPersona.JURIDICA);
        clienteJuridica.setNumeroIdentificacion("1234567890001");
        clienteJuridica.setDireccion("Av. Empresarial 456, Guayaquil");
        clienteJuridica.setTelefono("0987654322");
        clienteJuridica.setCorreoElectronico("info@empresaabc.com");
        clienteJuridica.setEstado(Cliente.Estado.ACTIVO);
        clienteJuridica.setFechaRegistro(LocalDate.now());
        clienteJuridica.setRepresentanteLegal("María González");
        clienteJuridica.setDireccionFiscal("Av. Fiscal 789, Guayaquil");
        
        ClienteService.ResultadoOperacion resultado2 = clienteService.registrarCliente(clienteJuridica);
        System.out.println("Cliente Jurídico: " + resultado2.getMensaje());
        
        // Cliente con pasaporte
        Cliente clientePasaporte = new Cliente();
        clientePasaporte.setNombreCompleto("John Smith");
        clientePasaporte.setTipoIdentificacion(Cliente.TipoIdentificacion.PASAPORTE);
        clientePasaporte.setTipoPersona(Cliente.TipoPersona.NATURAL);
        clientePasaporte.setNumeroIdentificacion("AB123456");
        clientePasaporte.setDireccion("Calle Internacional 321, Quito");
        clientePasaporte.setTelefono("0987654323");
        clientePasaporte.setCorreoElectronico("john.smith@email.com");
        clientePasaporte.setEstado(Cliente.Estado.ACTIVO);
        clientePasaporte.setFechaRegistro(LocalDate.now());
        clientePasaporte.setEstadoCivil("Casado");
        
        ClienteService.ResultadoOperacion resultado3 = clienteService.registrarCliente(clientePasaporte);
        System.out.println("Cliente con Pasaporte: " + resultado3.getMensaje());
        
        System.out.println();
    }
    
    private static void listarClientes(ClienteService clienteService) {
        System.out.println("2. Listando todos los clientes...\n");
        
        ObservableList<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.\n");
        } else {
            System.out.println("Total de clientes: " + clientes.size());
            System.out.println("-----------------------------------");
            
            for (Cliente cliente : clientes) {
                System.out.println("ID: " + cliente.getId());
                System.out.println("Nombre: " + cliente.getNombreCompleto());
                System.out.println("Tipo: " + cliente.getTipoPersona());
                System.out.println("Identificación: " + cliente.getNumeroIdentificacion() + " (" + cliente.getTipoIdentificacion() + ")");
                System.out.println("Teléfono: " + cliente.getTelefono());
                System.out.println("Email: " + cliente.getCorreoElectronico());
                System.out.println("Estado: " + cliente.getEstado());
                System.out.println("Fecha Registro: " + cliente.getFechaRegistro());
                
                if (cliente.esPersonaNatural()) {
                    System.out.println("Estado Civil: " + cliente.getEstadoCivil());
                } else {
                    System.out.println("Representante Legal: " + cliente.getRepresentanteLegal());
                    System.out.println("Dirección Fiscal: " + cliente.getDireccionFiscal());
                }
                
                System.out.println("-----------------------------------");
            }
        }
        
        System.out.println();
    }
    
    private static void buscarClientePorIdentificacion(ClienteService clienteService, String identificacion) {
        System.out.println("3. Buscando cliente por identificación: " + identificacion + "\n");
        
        Cliente cliente = clienteService.obtenerClientePorIdentificacion(identificacion);
        
        if (cliente != null) {
            System.out.println("Cliente encontrado:");
            System.out.println("Nombre: " + cliente.getNombreCompleto());
            System.out.println("Tipo: " + cliente.getTipoPersona());
            System.out.println("Teléfono: " + cliente.getTelefono());
            System.out.println("Email: " + cliente.getCorreoElectronico());
        } else {
            System.out.println("Cliente no encontrado.");
        }
        
        System.out.println();
    }
    
    private static void buscarClientesPorNombre(ClienteService clienteService, String nombre) {
        System.out.println("4. Buscando clientes por nombre: " + nombre + "\n");
        
        ObservableList<Cliente> clientes = clienteService.buscarClientesPorNombre(nombre);
        
        if (clientes.isEmpty()) {
            System.out.println("No se encontraron clientes con ese nombre.\n");
        } else {
            System.out.println("Clientes encontrados: " + clientes.size());
            for (Cliente cliente : clientes) {
                System.out.println("- " + cliente.getNombreCompleto() + " (" + cliente.getNumeroIdentificacion() + ")");
            }
        }
        
        System.out.println();
    }
    
    private static void actualizarCliente(ClienteService clienteService, String identificacion) {
        System.out.println("5. Actualizando cliente: " + identificacion + "\n");
        
        Cliente cliente = clienteService.obtenerClientePorIdentificacion(identificacion);
        
        if (cliente != null) {
            // Actualizar algunos campos
            cliente.setTelefono("0999888777");
            cliente.setCorreoElectronico("juan.perez.nuevo@email.com");
            cliente.setDireccion("Nueva Dirección 456, Quito");
            
            if (cliente.esPersonaNatural()) {
                cliente.setEstadoCivil("Casado");
            }
            
            ClienteService.ResultadoOperacion resultado = clienteService.actualizarCliente(cliente);
            System.out.println("Actualización: " + resultado.getMensaje());
        } else {
            System.out.println("Cliente no encontrado para actualizar.");
        }
        
        System.out.println();
    }
    
    private static void listarClientesActivos(ClienteService clienteService) {
        System.out.println("6. Listando clientes activos...\n");
        
        ObservableList<Cliente> clientesActivos = clienteService.obtenerClientesPorEstado(Cliente.Estado.ACTIVO);
        
        System.out.println("Clientes activos: " + clientesActivos.size());
        for (Cliente cliente : clientesActivos) {
            System.out.println("- " + cliente.getNombreCompleto() + " (" + cliente.getNumeroIdentificacion() + ")");
        }
        
        System.out.println();
    }
}
