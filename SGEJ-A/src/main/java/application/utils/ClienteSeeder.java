package application.utils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import application.model.Cliente;

public class ClienteSeeder {

    static String[] nombres = { "Juan", "María", "Carlos", "Ana", "Pedro", "Luisa", "Andrés", "Carmen", "José",
            "Diana" };
    static String[] apellidos = { "García", "Pérez", "Rodríguez", "Mendoza", "Vega", "Torres", "Cordero", "Benítez",
            "Castro", "Ruiz" };
    static String[] provincias = { "Quito", "Guayaquil", "Cuenca", "Ambato", "Loja", "Manta", "Riobamba", "Ibarra" };
    static String[] estadosCiviles = { "SOLTERO", "CASADO", "DIVORCIADO", "VIUDO" };

    // Arrays que faltaban
    private static final String[] NOMBRES_MASCULINOS = {
            "Carlos", "Luis", "José", "Juan", "Miguel", "Francisco", "Antonio", "Manuel",
            "Pedro", "Alejandro", "Diego", "Fernando", "Rafael", "Roberto", "Eduardo",
            "Andrés", "Gabriel", "Ricardo", "Sebastián", "Nicolás", "Mateo", "Santiago"
    };

    private static final String[] NOMBRES_FEMENINOS = {
            "María", "Ana", "Carmen", "Rosa", "Isabel", "Patricia", "Laura", "Sandra",
            "Mónica", "Andrea", "Gabriela", "Alejandra", "Cristina", "Verónica", "Diana",
            "Silvia", "Maritza", "Paola", "Esperanza", "Soledad", "Claudia", "Natalia"
    };

    private static final String[] APELLIDOS = {
            "García", "González", "Rodríguez", "Fernández", "López", "Martínez", "Sánchez",
            "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández", "Díaz", "Moreno",
            "Álvarez", "Muñoz", "Romero", "Alonso", "Gutiérrez", "Navarro", "Torres",
            "Domínguez", "Vázquez", "Ramos", "Gil", "Ramírez", "Serrano", "Blanco", "Suárez"
    };

    // Agregar array de estados civiles
    private static final String[] ESTADOS_CIVILES = { "Soltero", "Casado", "Divorciado", "Viudo", "Unión Libre" };

    static Random random = new Random();

    public static void seed(Connection conn) throws SQLException {
        String insertSql = """
                    INSERT INTO clientes (
                        nombre_completo, tipo_identificacion, tipo_persona,
                        numero_identificacion, direccion, telefono,
                        correo_electronico, estado, fecha_registro,
                        estado_civil, representante_legal, direccion_fiscal
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement stmt = conn.prepareStatement(insertSql);

        for (int i = 0; i < 100; i++) {
            String nombre = nombres[random.nextInt(nombres.length)];
            String apellido = apellidos[random.nextInt(apellidos.length)];
            String nombreCompleto = nombre + " " + apellido;
            String tipoPersona = random.nextBoolean() ? "NATURAL" : "JURIDICA";
            String tipoIdentificacion = tipoPersona.equals("NATURAL") ? "CEDULA" : "RUC";
            String identificacion = tipoIdentificacion.equals("CEDULA")
                    ? generarCedulaEcuatoriana()
                    : generarRucEcuatoriano();
            String direccion = provincias[random.nextInt(provincias.length)] + " Calle " + (random.nextInt(100) + 1);
            String telefono = "09" + (random.nextInt(90000000) + 10000000);
            String correo = (nombre + "." + apellido + i + "@mail.com").toLowerCase();
            String estado = random.nextBoolean() ? "ACTIVO" : "INACTIVO";
            String fechaRegistro = LocalDate.now().minusDays(random.nextInt(1000)).toString();
            String estadoCivil = tipoPersona.equals("NATURAL") ? estadosCiviles[random.nextInt(estadosCiviles.length)]
                    : null;
            String representanteLegal = tipoPersona.equals("JURIDICA")
                    ? nombre + " " + apellidos[random.nextInt(apellidos.length)]
                    : null;
            String direccionFiscal = tipoPersona.equals("JURIDICA") ? direccion + " Of. " + (random.nextInt(10) + 1)
                    : null;

            stmt.setString(1, nombreCompleto);
            stmt.setString(2, tipoIdentificacion);
            stmt.setString(3, tipoPersona);
            stmt.setString(4, identificacion);
            stmt.setString(5, direccion);
            stmt.setString(6, telefono);
            stmt.setString(7, correo);
            stmt.setString(8, estado);
            stmt.setString(9, fechaRegistro);
            stmt.setString(10, estadoCivil);
            stmt.setString(11, representanteLegal);
            stmt.setString(12, direccionFiscal);

            stmt.addBatch();
        }

        stmt.executeBatch();
        System.out.println("✔ Se han insertado 100 clientes de prueba correctamente.");
    }

    private static String generarCedulaEcuatoriana() {
        VerificationID validator = new VerificationID();
        String cedula;
        int intentos = 0;

        do {
            // Generar provincia (01-24)
            int provincia = random.nextInt(24) + 1;
            String provinciaStr = String.format("%02d", provincia);

            // Generar tercer dígito (0-5 para personas naturales)
            int tercerDigito = random.nextInt(6);

            // Generar siguientes 6 dígitos aleatorios
            StringBuilder cedulaBase = new StringBuilder(provinciaStr);
            cedulaBase.append(tercerDigito);
            for (int i = 0; i < 6; i++) {
                cedulaBase.append(random.nextInt(10));
            }

            // Calcular dígito verificador correcto
            int digitoVerificador = calcularDigitoVerificadorCedula(cedulaBase.toString());
            cedula = cedulaBase.toString() + digitoVerificador;

            intentos++;
        } while (!validator.validarCedula(cedula) && intentos < 10);

        return cedula;
    }

    private static String generarRucEcuatoriano() {
        String ruc;
        int intentos = 0;

        do {
            // Decidir tipo de sociedad
            if (random.nextBoolean()) {
                // Persona Natural (tercer dígito 0-5)
                String cedula = generarCedulaEcuatoriana();
                ruc = cedula + "001";
            } else {
                // Sociedad Privada (tercer dígito 9)
                int provincia = random.nextInt(24) + 1;
                String provinciaStr = String.format("%02d", provincia);

                StringBuilder rucBase = new StringBuilder(provinciaStr);
                rucBase.append("9"); // Tercer dígito para sociedad privada

                // Generar siguientes 6 dígitos
                for (int i = 0; i < 6; i++) {
                    rucBase.append(random.nextInt(10));
                }

                // Calcular dígito verificador para sociedad privada
                int digitoVerificador = calcularDigitoVerificadorRucPrivado(rucBase.toString());
                ruc = rucBase.toString() + digitoVerificador + "001";
            }

            intentos++;
        } while (!RucValidator.validarRuc(ruc) && intentos < 10);

        return ruc;
    }

    private static int calcularDigitoVerificadorCedula(String cedula) {
        int[] coeficientes = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            int resultado = digito * coeficientes[i];
            if (resultado >= 10) {
                resultado -= 9;
            }
            suma += resultado;
        }

        int digitoVerificador = (10 - (suma % 10)) % 10;
        return digitoVerificador;
    }

    private static int calcularDigitoVerificadorRucPrivado(String rucBase) {
        int[] coef = { 4, 3, 2, 7, 6, 5, 4, 3, 2 };
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            suma += Character.getNumericValue(rucBase.charAt(i)) * coef[i];
        }

        int modulo = suma % 11;
        return (modulo == 0) ? 0 : 11 - modulo;
    }

    public static List<Cliente> generarClientes(int cantidad) {
        List<Cliente> clientes = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Cliente cliente = new Cliente();

            // Determinar tipo de persona aleatoriamente
            boolean esPersonaNatural = random.nextDouble() < 0.8; // 80% personas naturales

            // Generar nombres según tipo de persona
            String nombreCompleto;
            String identificacion;
            Cliente.TipoIdentificacion tipoId;

            if (esPersonaNatural) {
                // Persona Natural
                boolean esMasculino = random.nextBoolean();
                String primerNombre = esMasculino
                        ? NOMBRES_MASCULINOS[random.nextInt(NOMBRES_MASCULINOS.length)]
                        : NOMBRES_FEMENINOS[random.nextInt(NOMBRES_FEMENINOS.length)];

                String segundoNombre = random.nextBoolean()
                        ? (esMasculino
                                ? NOMBRES_MASCULINOS[random.nextInt(NOMBRES_MASCULINOS.length)]
                                : NOMBRES_FEMENINOS[random.nextInt(NOMBRES_FEMENINOS.length)])
                        : "";
                String primerApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
                String segundoApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];

                nombreCompleto = primerNombre
                        + (segundoNombre.isEmpty() ? "" : " " + segundoNombre) + " " + primerApellido + " "
                        + segundoApellido;

                identificacion = generarCedulaEcuatoriana();
                tipoId = Cliente.TipoIdentificacion.CEDULA;
            } else {
                // Persona Jurídica
                String[] tiposEmpresa = { "CIA. LTDA.", "S.A.", "CORP.", "FUNDACIÓN", "ASOCIACIÓN" };
                String[] sectores = { "COMERCIAL", "INDUSTRIAL", "SERVICIOS", "TECNOLOGÍA", "CONSTRUCCIÓN" };

                String sector = sectores[random.nextInt(sectores.length)];
                String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
                String tipo = tiposEmpresa[random.nextInt(tiposEmpresa.length)];

                nombreCompleto = sector + " " + apellido + " " + tipo;
                identificacion = generarRucEcuatoriano();
                tipoId = Cliente.TipoIdentificacion.RUC;
            }

            // Generar otros datos
            String telefono = generarTelefono();
            String email = generarEmail(nombreCompleto.split(" ")[0],
                    nombreCompleto.split(" ")[nombreCompleto.split(" ").length - 1]);
            String direccion = generarDireccion();

            // Establecer datos del cliente
            try {
                cliente.setNombreCompleto(nombreCompleto);
                cliente.setNumeroIdentificacion(identificacion);
                cliente.setTipoIdentificacion(tipoId);
                cliente.setTelefono(telefono);
                cliente.setCorreoElectronico(email);
                cliente.setDireccion(direccion);
                cliente.setEstado(Cliente.Estado.ACTIVO);

                if (esPersonaNatural) {
                    cliente.setTipoPersona(Cliente.TipoPersona.NATURAL);
                    cliente.setEstadoCivil(ESTADOS_CIVILES[random.nextInt(ESTADOS_CIVILES.length)]);
                } else {
                    cliente.setTipoPersona(Cliente.TipoPersona.JURIDICA);
                    cliente.setRepresentanteLegal(NOMBRES_MASCULINOS[random.nextInt(NOMBRES_MASCULINOS.length)]
                            + " " + APELLIDOS[random.nextInt(APELLIDOS.length)]);
                    cliente.setDireccionFiscal(direccion + " - Oficina Principal");
                }

            } catch (Exception e) {
                System.err.println("Error al crear cliente " + i + ": " + e.getMessage());
                continue;
            }

            clientes.add(cliente);
        }

        return clientes;
    }

    // Métodos que faltaban
    private static String generarTelefono() {
        return "09" + (random.nextInt(90000000) + 10000000);
    }

    private static String generarEmail(String nombre, String apellido) {
        String usuario = nombre.toLowerCase().replaceAll("[áàä]", "a")
                .replaceAll("[éèë]", "e")
                .replaceAll("[íìï]", "i")
                .replaceAll("[óòö]", "o")
                .replaceAll("[úùü]", "u");

        String apellidoLimpio = apellido.toLowerCase().replaceAll("[áàä]", "a")
                .replaceAll("[éèë]", "e")
                .replaceAll("[íìï]", "i")
                .replaceAll("[óòö]", "o")
                .replaceAll("[úùü]", "u");

        String[] dominios = { "@gmail.com", "@hotmail.com", "@yahoo.com", "@outlook.com" };
        String dominio = dominios[random.nextInt(dominios.length)];

        return usuario + "." + apellidoLimpio + random.nextInt(99) + dominio;
    }

    private static String generarDireccion() {
        return provincias[random.nextInt(provincias.length)] + " Calle " + (random.nextInt(100) + 1);
    }

}
