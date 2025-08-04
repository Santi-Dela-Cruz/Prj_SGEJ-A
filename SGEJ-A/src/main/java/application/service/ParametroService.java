package application.service;

import application.dao.ParametroDAO;
import application.model.Parametro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para la gestión de parámetros del sistema implementando patrón
 * Singleton.
 * Proporciona acceso a los parámetros desde cualquier parte de la aplicación
 * y maneja su cache para mejorar el rendimiento.
 */
public class ParametroService {

    // Instancia única del servicio (Singleton)
    private static ParametroService instancia;

    // Data Access Object para operaciones de base de datos
    private ParametroDAO parametroDAO;

    // Caché de parámetros para reducir consultas a la base de datos
    // Utilizamos ConcurrentHashMap para seguridad en entornos multi-hilo
    private final Map<String, Parametro> cacheParametros = new ConcurrentHashMap<>();

    // Valores predeterminados para parámetros comunes
    private final Map<String, String> valoresPorDefecto = new HashMap<>();

    // Última actualización del caché
    private LocalDateTime ultimaActualizacionCache = null;

    // Tiempo máximo de vigencia del caché en minutos (configurable)
    private int tiempoVigenciaCache = 15;

    /**
     * Constructor privado para implementar Singleton
     */
    private ParametroService() {
        parametroDAO = new ParametroDAO();
        inicializarValoresPorDefecto();
    }

    /**
     * Obtiene la instancia única del servicio
     * 
     * @return instancia del servicio
     */
    public static synchronized ParametroService getInstance() {
        if (instancia == null) {
            instancia = new ParametroService();
        }
        return instancia;
    }

    /**
     * Método alternativo para mantener compatibilidad con código existente
     * 
     * @return instancia del servicio
     */
    public static synchronized ParametroService getInstancia() {
        return getInstance();
    }

    /**
     * Inicializa los valores por defecto para parámetros comunes
     */
    private void inicializarValoresPorDefecto() {
        // Valores por defecto para parámetros comunes del sistema
        valoresPorDefecto.put("iva", "12");
        valoresPorDefecto.put("porcentaje_descuento", "5");
        valoresPorDefecto.put("dias_pago_factura", "30");
        valoresPorDefecto.put("idioma_sistema", "es");
        valoresPorDefecto.put("tema_oscuro", "false");
        valoresPorDefecto.put("mostrar_novedades", "true");
        valoresPorDefecto.put("notificaciones_activas", "true");
        valoresPorDefecto.put("logo_empresa", "default_logo.png");
        valoresPorDefecto.put("ruta_documentos", "uploads/documentos/");

        // Parámetros para facturación
        valoresPorDefecto.put("porcentaje_iva", "12");
        valoresPorDefecto.put("codigo_documento_factura", "01");
        valoresPorDefecto.put("ruc_empresa", "9999999999001");
        valoresPorDefecto.put("razon_social", "EMPRESA DEMO");
        valoresPorDefecto.put("direccion_matriz", "DIRECCION MATRIZ");
        valoresPorDefecto.put("direccion_sucursal", "DIRECCION SUCURSAL");
        valoresPorDefecto.put("obligado_contabilidad", "false");
        valoresPorDefecto.put("codigo_establecimiento", "001");
        valoresPorDefecto.put("codigo_punto_emision", "001");
        valoresPorDefecto.put("ambiente_facturacion", "1");
        valoresPorDefecto.put("tipo_emision", "1");
        valoresPorDefecto.put("tipo_documento_factura", "FACTURA");
    }

    /**
     * Verifica si el caché está vigente o necesita actualizarse
     * 
     * @return true si el caché es válido, false si debe actualizarse
     */
    private boolean cacheVigente() {
        if (ultimaActualizacionCache == null) {
            return false;
        }

        // Verificar si ha pasado el tiempo máximo de vigencia
        return LocalDateTime.now().minusMinutes(tiempoVigenciaCache).isBefore(ultimaActualizacionCache);
    }

    /**
     * Actualiza el caché de parámetros desde la base de datos
     */
    private void actualizarCache() {
        System.out.println("Actualizando caché de parámetros del sistema...");
        cacheParametros.clear();

        // Cargar solo los parámetros activos
        List<Parametro> parametros = parametroDAO.obtenerParametrosVisibles();

        for (Parametro parametro : parametros) {
            cacheParametros.put(parametro.getCodigo().toLowerCase(), parametro);
        }

        ultimaActualizacionCache = LocalDateTime.now();
        System.out.println("Caché actualizado con " + cacheParametros.size() + " parámetros activos.");
    }

    /**
     * Fuerza la actualización del caché descartando el existente
     */
    public void invalidarCache() {
        System.out.println("Invalidando caché de parámetros...");
        cacheParametros.clear();
        ultimaActualizacionCache = null;
    }

    /**
     * Establece el tiempo de vigencia del caché en minutos
     * 
     * @param minutos tiempo en minutos
     */
    public void setTiempoVigenciaCache(int minutos) {
        if (minutos > 0) {
            this.tiempoVigenciaCache = minutos;
            System.out.println("Tiempo de vigencia de caché establecido a " + minutos + " minutos");
        }
    }

    /**
     * Obtiene un parámetro por su código
     * 
     * @param codigo código del parámetro
     * @return el parámetro o null si no existe o está inactivo
     */
    public Parametro getParametro(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        codigo = codigo.toLowerCase();

        // Verificar si el caché necesita actualizarse
        if (!cacheVigente()) {
            actualizarCache();
        }

        return cacheParametros.get(codigo);
    }

    /**
     * Obtiene el valor de un parámetro como String
     * 
     * @param codigo código del parámetro
     * @return valor del parámetro o valor por defecto si no existe o está inactivo
     */
    public String getValor(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        codigo = codigo.toLowerCase();

        Parametro parametro = getParametro(codigo);

        // Si el parámetro existe y está activo, devolver su valor
        if (parametro != null && parametro.getEstado() == Parametro.Estado.ACTIVO) {
            return parametro.getValor();
        }

        // Si no existe o está inactivo, devolver valor por defecto
        return valoresPorDefecto.getOrDefault(codigo, null);
    }

    /**
     * Obtiene el valor de un parámetro como String con un valor predeterminado
     * 
     * @param codigo          código del parámetro
     * @param valorPorDefecto valor por defecto si no se encuentra
     * @return valor del parámetro o valor por defecto
     */
    public String getValor(String codigo, String valorPorDefecto) {
        String valor = getValor(codigo);
        return (valor != null) ? valor : valorPorDefecto;
    }

    /**
     * Obtiene el valor de un parámetro como entero
     * 
     * @param codigo          código del parámetro
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como entero o el valor por defecto
     */
    public int getValorEntero(String codigo, int valorPorDefecto) {
        String valor = getValor(codigo);
        if (valor == null) {
            return valorPorDefecto;
        }

        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir parámetro " + codigo + " a entero: " + e.getMessage());
            return valorPorDefecto;
        }
    }

    /**
     * Obtiene el valor de un parámetro como decimal
     * 
     * @param codigo          código del parámetro
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como decimal o el valor por defecto
     */
    public BigDecimal getValorDecimal(String codigo, BigDecimal valorPorDefecto) {
        String valor = getValor(codigo);
        if (valor == null) {
            return valorPorDefecto;
        }

        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir parámetro " + codigo + " a decimal: " + e.getMessage());
            return valorPorDefecto;
        }
    }

    /**
     * Obtiene el valor de un parámetro como BigDecimal
     * 
     * @param codigo          código del parámetro
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como BigDecimal o el valor por defecto
     */
    public BigDecimal getValorBigDecimal(String codigo, BigDecimal valorPorDefecto) {
        return getValorDecimal(codigo, valorPorDefecto);
    }

    /**
     * Obtiene el valor de un parámetro como booleano
     * 
     * @param codigo          código del parámetro
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como booleano o el valor por defecto
     */
    public boolean getValorBoolean(String codigo, boolean valorPorDefecto) {
        String valor = getValor(codigo);
        if (valor == null) {
            return valorPorDefecto;
        }

        valor = valor.trim().toLowerCase();
        return valor.equals("true") || valor.equals("1") || valor.equals("si") || valor.equals("sí")
                || valor.equals("s") || valor.equals("verdadero");
    }

    /**
     * Método alternativo para mantener compatibilidad
     */
    public boolean getValorBooleano(String codigo, boolean valorPorDefecto) {
        return getValorBoolean(codigo, valorPorDefecto);
    }

    /**
     * Obtiene el valor de un parámetro como fecha
     * 
     * @param codigo          código del parámetro
     * @param formato         formato de la fecha (por defecto yyyy-MM-dd)
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como fecha o el valor por defecto
     */
    public LocalDate getValorFecha(String codigo, String formato, LocalDate valorPorDefecto) {
        String valor = getValor(codigo);
        if (valor == null) {
            return valorPorDefecto;
        }

        try {
            if (formato == null || formato.isEmpty()) {
                formato = "yyyy-MM-dd";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            return LocalDate.parse(valor.trim(), formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error al convertir parámetro " + codigo + " a fecha: " + e.getMessage());
            return valorPorDefecto;
        }
    }

    /**
     * Obtiene el valor de un parámetro como hora
     * 
     * @param codigo          código del parámetro
     * @param formato         formato de la hora (por defecto HH:mm:ss)
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como hora o el valor por defecto
     */
    public LocalTime getValorHora(String codigo, String formato, LocalTime valorPorDefecto) {
        String valor = getValor(codigo);
        if (valor == null) {
            return valorPorDefecto;
        }

        try {
            if (formato == null || formato.isEmpty()) {
                formato = "HH:mm:ss";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            return LocalTime.parse(valor.trim(), formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error al convertir parámetro " + codigo + " a hora: " + e.getMessage());
            return valorPorDefecto;
        }
    }

    /**
     * Obtiene el valor porcentual de un parámetro (dividido por 100)
     * 
     * @param codigo          código del parámetro
     * @param valorPorDefecto valor por defecto si no se puede convertir
     * @return valor del parámetro como porcentaje (0.xx) o el valor por defecto
     */
    public BigDecimal getValorPorcentual(String codigo, BigDecimal valorPorDefecto) {
        BigDecimal valor = getValorDecimal(codigo, null);
        if (valor == null) {
            return valorPorDefecto;
        }

        // Convertir a valor porcentual (dividir por 100)
        return valor.divide(new BigDecimal("100"));
    }

    /**
     * Actualiza el valor de un parámetro
     * 
     * @param codigo     código del parámetro
     * @param nuevoValor nuevo valor del parámetro
     * @return true si se actualizó correctamente
     */
    public boolean actualizarValor(String codigo, String nuevoValor) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        boolean resultado = parametroDAO.actualizarValor(codigo, nuevoValor);

        // Si se actualizó correctamente, actualizar también el caché
        if (resultado) {
            invalidarCache(); // Forzar actualización del caché
        }

        return resultado;
    }

    /**
     * Activa un parámetro inactivo
     * 
     * @param codigo código del parámetro
     * @return true si se activó correctamente
     */
    public boolean activarParametro(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        boolean resultado = parametroDAO.activarParametro(codigo);

        // Si se activó correctamente, actualizar también el caché
        if (resultado) {
            invalidarCache();
        }

        return resultado;
    }

    /**
     * Desactiva un parámetro activo
     * 
     * @param codigo código del parámetro
     * @return true si se desactivó correctamente
     */
    public boolean desactivarParametro(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        boolean resultado = parametroDAO.desactivarParametro(codigo);

        // Si se desactivó correctamente, actualizar también el caché
        if (resultado) {
            invalidarCache();
        }

        return resultado;
    }

    /**
     * Obtiene todos los parámetros activos
     * 
     * @return lista de parámetros activos
     */
    public List<Parametro> getParametrosActivos() {
        return parametroDAO.obtenerParametrosVisibles();
    }

    /**
     * Obtiene los parámetros activos de una categoría específica
     * 
     * @param categoria categoría de los parámetros
     * @return lista de parámetros activos de la categoría
     */
    public List<Parametro> getParametrosPorCategoria(String categoria) {
        return parametroDAO.obtenerPorCategoria(categoria);
    }
}
