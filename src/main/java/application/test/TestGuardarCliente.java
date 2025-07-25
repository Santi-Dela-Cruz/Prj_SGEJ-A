package application.test;

import application.service.ClienteService;
import application.database.DatabaseConnection;
import application.model.Cliente;

/**
 * Prueba específica para verificar que se están guardando los clientes
 */
public class TestGuardarCliente {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE GUARDADO DE CLIENTE ===\n");
        
        try {
            // Inicializar base de datos
            DatabaseConnection.initializeDatabase();
            
            // Crear servicio
            ClienteService clienteService = new ClienteService();
            
            // Mostrar clientes existentes
            System.out.println("1. Clientes existentes:");
            var clientesExistentes = clienteService.obtenerTodosLosClientes();
            System.out.println("   Total: " + clientesExistentes.size());
            
            for (Cliente c : clientesExistentes) {
                System.out.println("   - " + c.getNombreCompleto() + " (" + c.getNumeroIdentificacion() + ")");
            }
            
            // Crear cliente de prueba
            System.out.println("\n2. Creando cliente de prueba...");
            Cliente clientePrueba = new Cliente();
            clientePrueba.setNombreCompleto("Juan Pérez Test");
            clientePrueba.setTipoIdentificacion(Cliente.TipoIdentificacion.CEDULA);
            clientePrueba.setTipoPersona(Cliente.TipoPersona.NATURAL);
            clientePrueba.setNumeroIdentificacion("1234567890");
            clientePrueba.setDireccion("Calle Falsa 123");
            clientePrueba.setTelefono("0987654321");
            clientePrueba.setCorreoElectronico("juan.perez@test.com");
            clientePrueba.setEstado(Cliente.Estado.ACTIVO);
            clientePrueba.setEstadoCivil("Soltero");
            clientePrueba.setFechaRegistro(java.time.LocalDate.now());
            
            // Guardar cliente
            ClienteService.ResultadoOperacion resultado = clienteService.registrarCliente(clientePrueba);
            
            if (resultado.esExitoso()) {
                System.out.println("   ✓ Cliente guardado: " + resultado.getMensaje());
            } else {
                System.out.println("   ✗ Error: " + resultado.getMensaje());
            }
            
            // Verificar que se guardó
            System.out.println("\n3. Verificando guardado...");
            var clientesActualizados = clienteService.obtenerTodosLosClientes();
            System.out.println("   Total después del guardado: " + clientesActualizados.size());
            
            // Buscar el cliente recién creado
            Cliente clienteEncontrado = clienteService.obtenerClientePorIdentificacion("1234567890");
            if (clienteEncontrado != null) {
                System.out.println("   ✓ Cliente encontrado: " + clienteEncontrado.getNombreCompleto());
            } else {
                System.out.println("   ✗ Cliente NO encontrado");
            }
            
            System.out.println("\n=== PRUEBA COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
