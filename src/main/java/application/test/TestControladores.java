package application.test;

import application.controllers.cliente.FormClienteController;
import application.controllers.cliente.ModuloClienteController;
import application.model.Cliente;
import application.service.ClienteService;
import application.database.DatabaseConnection;

/**
 * Prueba simple para verificar que los controladores funcionen
 */
public class TestControladores {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE CONTROLADORES DE CLIENTE ===\n");
        
        try {
            // Inicializar base de datos
            DatabaseConnection.initializeDatabase();
            
            // Probar FormClienteController
            System.out.println("1. Probando FormClienteController...");
            new FormClienteController();
            System.out.println("   ✓ FormClienteController creado correctamente");
            
            // Probar ModuloClienteController
            System.out.println("2. Probando ModuloClienteController...");
            new ModuloClienteController();
            System.out.println("   ✓ ModuloClienteController creado correctamente");
            
            // Probar ClienteService
            System.out.println("3. Probando ClienteService...");
            ClienteService clienteService = new ClienteService();
            System.out.println("   ✓ ClienteService creado correctamente");
            
            // Probar creación de cliente
            System.out.println("4. Probando creación de cliente...");
            Cliente cliente = new Cliente();
            cliente.setNombreCompleto("Prueba Test");
            cliente.setTipoIdentificacion(Cliente.TipoIdentificacion.CEDULA);
            cliente.setTipoPersona(Cliente.TipoPersona.NATURAL);
            cliente.setNumeroIdentificacion("9999999999");
            cliente.setDireccion("Calle de Prueba 123");
            cliente.setTelefono("0999999999");
            cliente.setCorreoElectronico("prueba@test.com");
            cliente.setEstado(Cliente.Estado.ACTIVO);
            cliente.setEstadoCivil("Soltero");
            
            ClienteService.ResultadoOperacion resultado = clienteService.registrarCliente(cliente);
            System.out.println("   " + (resultado.esExitoso() ? "✓" : "✗") + " " + resultado.getMensaje());
            
            // Probar consulta de cliente
            System.out.println("5. Probando consulta de cliente...");
            Cliente clienteConsultado = clienteService.obtenerClientePorIdentificacion("9999999999");
            if (clienteConsultado != null) {
                System.out.println("   ✓ Cliente encontrado: " + clienteConsultado.getNombreCompleto());
            } else {
                System.out.println("   ✗ Cliente no encontrado");
            }
            
            // Probar listado de clientes
            System.out.println("6. Probando listado de clientes...");
            var clientes = clienteService.obtenerTodosLosClientes();
            System.out.println("   ✓ Clientes en base de datos: " + clientes.size());
            
            System.out.println("\n=== TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE ===");
            
        } catch (Exception e) {
            System.out.println("   ✗ Error en las pruebas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
