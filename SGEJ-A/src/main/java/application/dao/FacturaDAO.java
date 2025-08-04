package application.dao;

import application.database.DatabaseConnection;
import application.model.Factura;
import application.model.FacturaDetalle;
import application.service.ParametroService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase DAO para las operaciones CRUD de la factura
 */
public class FacturaDAO {
    private static final Logger LOGGER = Logger.getLogger(FacturaDAO.class.getName());

    /**
     * Guarda una factura en la base de datos
     * 
     * @param factura Factura a guardar
     * @return El ID generado o -1 en caso de error
     */
    public int guardar(Factura factura) {
        String sql = "INSERT INTO factura (tipo_documento, clave_acceso, numero_autorizacion, fecha_autorizacion, " +
                "ambiente, emision, ruc_emisor, razon_social_emisor, direccion_matriz, direccion_sucursal, " +
                "obligado_contabilidad, codigo_establecimiento, codigo_punto_emision, secuencial, codigo_documento, " +
                "fecha_emision, nombre_cliente, id_cliente, tipo_identificacion, direccion_cliente, " +
                "telefono_cliente, email_cliente, numero_expediente, nombre_caso, abogado_responsable, " +
                "subtotal12, subtotal0, subtotal_no_objeto_iva, subtotal_exento_iva, subtotal_sin_impuestos, " +
                "total_descuento, valor_iva, porcentaje_iva, devolucion_iva, propina, valor_total, " +
                "valor_sin_subsidio, ahorro_subsidio, forma_pago, monto_pago, plazo, estado_factura, pago_realizado, " +
                "estado_sri, respuesta_sri, fecha_respuesta_sri, usuario_creacion, usuario_modificacion, created_at, updated_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int idx = 1;
            pstmt.setString(idx++, factura.getTipoDocumento());
            pstmt.setString(idx++, factura.getClaveAcceso());
            pstmt.setString(idx++, factura.getNumeroAutorizacion());

            if (factura.getFechaAutorizacion() != null) {
                pstmt.setTimestamp(idx++, Timestamp.valueOf(factura.getFechaAutorizacion()));
            } else {
                pstmt.setNull(idx++, Types.TIMESTAMP);
            }

            pstmt.setString(idx++, factura.getAmbiente());
            pstmt.setString(idx++, factura.getEmision());
            pstmt.setString(idx++, factura.getRucEmisor());
            pstmt.setString(idx++, factura.getRazonSocialEmisor());
            pstmt.setString(idx++, factura.getDireccionMatriz());
            pstmt.setString(idx++, factura.getDireccionSucursal());
            pstmt.setBoolean(idx++, factura.isObligadoContabilidad());
            pstmt.setString(idx++, factura.getCodigoEstablecimiento());
            pstmt.setString(idx++, factura.getCodigoPuntoEmision());
            pstmt.setString(idx++, factura.getSecuencial());
            pstmt.setString(idx++, factura.getCodigoDocumento());

            if (factura.getFechaEmision() != null) {
                pstmt.setDate(idx++, Date.valueOf(factura.getFechaEmision()));
            } else {
                pstmt.setNull(idx++, Types.DATE);
            }

            pstmt.setString(idx++, factura.getNombreCliente());
            pstmt.setString(idx++, factura.getIdCliente());
            pstmt.setString(idx++, factura.getTipoIdentificacion());
            pstmt.setString(idx++, factura.getDireccionCliente());
            pstmt.setString(idx++, factura.getTelefonoCliente());
            pstmt.setString(idx++, factura.getEmailCliente());
            pstmt.setString(idx++, factura.getNumeroExpediente());
            pstmt.setString(idx++, factura.getNombreCaso());
            pstmt.setString(idx++, factura.getAbogadoResponsable());

            pstmt.setBigDecimal(idx++, factura.getSubtotal12());
            pstmt.setBigDecimal(idx++, factura.getSubtotal0());
            pstmt.setBigDecimal(idx++, factura.getSubtotalNoObjetoIva());
            pstmt.setBigDecimal(idx++, factura.getSubtotalExentoIva());
            pstmt.setBigDecimal(idx++, factura.getSubtotalSinImpuestos());
            pstmt.setBigDecimal(idx++, factura.getTotalDescuento());
            pstmt.setBigDecimal(idx++, factura.getValorIva());
            pstmt.setBigDecimal(idx++, factura.getPorcentajeIva());
            pstmt.setBigDecimal(idx++, factura.getDevolucionIva());
            pstmt.setBigDecimal(idx++, factura.getPropina());
            pstmt.setBigDecimal(idx++, factura.getValorTotal());
            pstmt.setBigDecimal(idx++, factura.getValorSinSubsidio());
            pstmt.setBigDecimal(idx++, factura.getAhorroSubsidio());
            pstmt.setString(idx++, factura.getFormaPago());
            pstmt.setBigDecimal(idx++, factura.getMontoPago());
            pstmt.setInt(idx++, factura.getPlazo());
            pstmt.setString(idx++, factura.getEstadoFactura());
            pstmt.setBoolean(idx++, factura.isPagoRealizado());
            pstmt.setString(idx++, factura.getEstadoSRI());
            pstmt.setString(idx++, factura.getRespuestaSRI());

            if (factura.getFechaRespuestaSRI() != null) {
                pstmt.setTimestamp(idx++, Timestamp.valueOf(factura.getFechaRespuestaSRI()));
            } else {
                pstmt.setNull(idx++, Types.TIMESTAMP);
            }

            pstmt.setString(idx++, factura.getUsuarioCreacion());
            pstmt.setString(idx++, factura.getUsuarioModificacion());

            LocalDateTime now = LocalDateTime.now();
            pstmt.setTimestamp(idx++, Timestamp.valueOf(now));
            pstmt.setTimestamp(idx++, Timestamp.valueOf(now));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "No se pudo guardar la factura");
                return -1;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int facturaId = generatedKeys.getInt(1);
                    factura.setId(facturaId);

                    // Guardar detalles de la factura
                    if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
                        guardarDetalles(facturaId, factura.getDetalles());
                    }

                    return facturaId;
                } else {
                    LOGGER.log(Level.WARNING, "No se pudo obtener ID para la factura");
                    return -1;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la factura", e);
            return -1;
        }
    }

    /**
     * Guarda los detalles de una factura en la base de datos
     * 
     * @param facturaId ID de la factura
     * @param detalles  Lista de detalles
     */
    private void guardarDetalles(int facturaId, List<FacturaDetalle> detalles) {
        String sql = "INSERT INTO factura_detalle (factura_id, codigo_servicio, codigo_auxiliar, descripcion, " +
                "cantidad, precio_unitario, descuento, valor_subtotal, codigo_impuesto, codigo_tarifa_iva, " +
                "porcentaje_iva, valor_iva, tipo_impuesto, usuario_creacion, usuario_modificacion, created_at, updated_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (FacturaDetalle detalle : detalles) {
                // Aseguramos que el subtotal esté calculado
                detalle.calcularSubtotal();

                // Aseguramos que todos los campos tienen valores predeterminados para evitar
                // NULLs
                if (detalle.getCodigoImpuesto() == null)
                    detalle.setCodigoImpuesto("");
                if (detalle.getCodigoTarifaIva() == null)
                    detalle.setCodigoTarifaIva("");
                if (detalle.getTipoImpuesto() == null)
                    detalle.setTipoImpuesto("IVA_0");
                if (detalle.getPorcentajeIva() == null)
                    detalle.setPorcentajeIva(BigDecimal.ZERO);
                if (detalle.getValorIva() == null)
                    detalle.setValorIva(BigDecimal.ZERO);

                int idx = 1;
                pstmt.setInt(idx++, facturaId);
                pstmt.setInt(idx++, detalle.getCodigoServicio());
                pstmt.setString(idx++, detalle.getCodigoAuxiliar());
                pstmt.setString(idx++, detalle.getDescripcion());
                pstmt.setBigDecimal(idx++, detalle.getCantidad());
                pstmt.setBigDecimal(idx++, detalle.getPrecioUnitario());
                pstmt.setBigDecimal(idx++, detalle.getDescuento());
                pstmt.setBigDecimal(idx++, detalle.getValorSubtotal());
                pstmt.setString(idx++, detalle.getCodigoImpuesto());
                pstmt.setString(idx++, detalle.getCodigoTarifaIva());
                pstmt.setBigDecimal(idx++, detalle.getPorcentajeIva());
                pstmt.setBigDecimal(idx++, detalle.getValorIva());
                pstmt.setString(idx++, detalle.getTipoImpuesto());
                pstmt.setString(idx++, detalle.getUsuarioCreacion());
                pstmt.setString(idx++, detalle.getUsuarioModificacion());

                LocalDateTime now = LocalDateTime.now();
                pstmt.setTimestamp(idx++, Timestamp.valueOf(now));
                pstmt.setTimestamp(idx++, Timestamp.valueOf(now));

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            LOGGER.info("Detalles de factura guardados correctamente para factura ID: " + facturaId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar los detalles de la factura: " + e.getMessage(), e);
            try {
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error al hacer rollback: " + rollbackEx.getMessage(), rollbackEx);
            }

            // Loguear más detalles sobre la estructura de la tabla para diagnóstico
            try (Connection diagConn = DatabaseConnection.getConnection();
                    Statement stmt = diagConn.createStatement()) {
                LOGGER.info("Verificando estructura de tabla factura_detalle...");
                try (var rs = stmt.executeQuery("PRAGMA table_info(factura_detalle)")) {
                    while (rs.next()) {
                        LOGGER.info("Columna: " + rs.getString("name") + ", Tipo: " + rs.getString("type"));
                    }
                }
            } catch (SQLException diagEx) {
                LOGGER.log(Level.SEVERE, "Error al verificar estructura de tabla: " + diagEx.getMessage(), diagEx);
            }
        }
    }

    /**
     * Actualiza una factura existente en la base de datos
     * 
     * @param factura Factura a actualizar
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizar(Factura factura) {
        String sql = "UPDATE factura SET tipo_documento = ?, clave_acceso = ?, numero_autorizacion = ?, " +
                "fecha_autorizacion = ?, ambiente = ?, emision = ?, ruc_emisor = ?, razon_social_emisor = ?, " +
                "direccion_matriz = ?, direccion_sucursal = ?, obligado_contabilidad = ?, " +
                "codigo_establecimiento = ?, codigo_punto_emision = ?, secuencial = ?, codigo_documento = ?, " +
                "fecha_emision = ?, nombre_cliente = ?, id_cliente = ?, tipo_identificacion = ?, " +
                "direccion_cliente = ?, telefono_cliente = ?, email_cliente = ?, numero_expediente = ?, " +
                "nombre_caso = ?, abogado_responsable = ?, subtotal_12 = ?, subtotal_0 = ?, " +
                "subtotal_no_objeto_iva = ?, subtotal_exento_iva = ?, subtotal_sin_impuestos = ?, " +
                "total_descuento = ?, valor_iva = ?, porcentaje_iva = ?, devolucion_iva = ?, propina = ?, " +
                "valor_total = ?, valor_sin_subsidio = ?, ahorro_subsidio = ?, forma_pago = ?, " +
                "monto_pago = ?, plazo = ?, estado_factura = ?, pago_realizado = ?, estado_sri = ?, " +
                "respuesta_sri = ?, fecha_respuesta_sri = ?, usuario_modificacion = ?, updated_at = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setString(idx++, factura.getTipoDocumento());
            pstmt.setString(idx++, factura.getClaveAcceso());
            pstmt.setString(idx++, factura.getNumeroAutorizacion());

            if (factura.getFechaAutorizacion() != null) {
                pstmt.setTimestamp(idx++, Timestamp.valueOf(factura.getFechaAutorizacion()));
            } else {
                pstmt.setNull(idx++, Types.TIMESTAMP);
            }

            pstmt.setString(idx++, factura.getAmbiente());
            pstmt.setString(idx++, factura.getEmision());
            pstmt.setString(idx++, factura.getRucEmisor());
            pstmt.setString(idx++, factura.getRazonSocialEmisor());
            pstmt.setString(idx++, factura.getDireccionMatriz());
            pstmt.setString(idx++, factura.getDireccionSucursal());
            pstmt.setBoolean(idx++, factura.isObligadoContabilidad());
            pstmt.setString(idx++, factura.getCodigoEstablecimiento());
            pstmt.setString(idx++, factura.getCodigoPuntoEmision());
            pstmt.setString(idx++, factura.getSecuencial());
            pstmt.setString(idx++, factura.getCodigoDocumento());

            if (factura.getFechaEmision() != null) {
                pstmt.setDate(idx++, Date.valueOf(factura.getFechaEmision()));
            } else {
                pstmt.setNull(idx++, Types.DATE);
            }

            pstmt.setString(idx++, factura.getNombreCliente());
            pstmt.setString(idx++, factura.getIdCliente());
            pstmt.setString(idx++, factura.getTipoIdentificacion());
            pstmt.setString(idx++, factura.getDireccionCliente());
            pstmt.setString(idx++, factura.getTelefonoCliente());
            pstmt.setString(idx++, factura.getEmailCliente());
            pstmt.setString(idx++, factura.getNumeroExpediente());
            pstmt.setString(idx++, factura.getNombreCaso());
            pstmt.setString(idx++, factura.getAbogadoResponsable());

            pstmt.setBigDecimal(idx++, factura.getSubtotal12());
            pstmt.setBigDecimal(idx++, factura.getSubtotal0());
            pstmt.setBigDecimal(idx++, factura.getSubtotalNoObjetoIva());
            pstmt.setBigDecimal(idx++, factura.getSubtotalExentoIva());
            pstmt.setBigDecimal(idx++, factura.getSubtotalSinImpuestos());
            pstmt.setBigDecimal(idx++, factura.getTotalDescuento());
            pstmt.setBigDecimal(idx++, factura.getValorIva());
            pstmt.setBigDecimal(idx++, factura.getPorcentajeIva());
            pstmt.setBigDecimal(idx++, factura.getDevolucionIva());
            pstmt.setBigDecimal(idx++, factura.getPropina());
            pstmt.setBigDecimal(idx++, factura.getValorTotal());
            pstmt.setBigDecimal(idx++, factura.getValorSinSubsidio());
            pstmt.setBigDecimal(idx++, factura.getAhorroSubsidio());
            pstmt.setString(idx++, factura.getFormaPago());
            pstmt.setBigDecimal(idx++, factura.getMontoPago());
            pstmt.setInt(idx++, factura.getPlazo());
            pstmt.setString(idx++, factura.getEstadoFactura());
            pstmt.setBoolean(idx++, factura.isPagoRealizado());
            pstmt.setString(idx++, factura.getEstadoSRI());
            pstmt.setString(idx++, factura.getRespuestaSRI());

            if (factura.getFechaRespuestaSRI() != null) {
                pstmt.setTimestamp(idx++, Timestamp.valueOf(factura.getFechaRespuestaSRI()));
            } else {
                pstmt.setNull(idx++, Types.TIMESTAMP);
            }

            pstmt.setString(idx++, factura.getUsuarioModificacion());
            pstmt.setTimestamp(idx++, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(idx++, factura.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Actualizamos los detalles
                eliminarDetalles(factura.getId());
                if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
                    guardarDetalles(factura.getId(), factura.getDetalles());
                }
                return true;
            }

            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la factura", e);
            return false;
        }
    }

    /**
     * Elimina los detalles de una factura
     * 
     * @param facturaId ID de la factura
     */
    private void eliminarDetalles(int facturaId) {
        String sql = "DELETE FROM factura_detalle WHERE factura_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, facturaId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar los detalles de la factura", e);
        }
    }

    /**
     * Elimina una factura por su ID
     * 
     * @param id ID de la factura a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(int id) {
        // Primero eliminamos los detalles
        eliminarDetalles(id);

        // Luego eliminamos la factura
        String sql = "DELETE FROM factura WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la factura", e);
            return false;
        }
    }

    /**
     * Obtiene una factura por su ID
     * 
     * @param id ID de la factura
     * @return La factura o null si no existe
     */
    public Factura obtenerPorId(int id) {
        String sql = "SELECT * FROM factura WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Factura factura = mapearFactura(rs);
                factura.setDetalles(obtenerDetallesPorFacturaId(id));
                return factura;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la factura por ID", e);
        }

        return null;
    }

    /**
     * Obtiene los detalles de una factura por su ID
     * 
     * @param facturaId ID de la factura
     * @return Lista de detalles de la factura
     */
    public List<FacturaDetalle> obtenerDetallesPorFacturaId(int facturaId) {
        String sql = "SELECT * FROM factura_detalle WHERE factura_id = ?";
        List<FacturaDetalle> detalles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, facturaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                detalles.add(mapearFacturaDetalle(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener los detalles de la factura", e);
        }

        return detalles;
    }

    /**
     * Obtiene todas las facturas
     * 
     * @return Lista de facturas
     */
    public List<Factura> obtenerTodas() {
        String sql = "SELECT * FROM factura ORDER BY fecha_emision DESC";
        List<Factura> facturas = new ArrayList<>();

        try {
            // First, verify if the table exists
            if (!existeTablaFactura()) {
                // Create the table if it doesn't exist
                crearTablaFactura();
                return facturas; // Return empty list since table was just created
            }

            // If table exists, proceed with query
            try (Connection conn = DatabaseConnection.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Factura factura = mapearFactura(rs);
                    facturas.add(factura);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todas las facturas", e);
        }

        return facturas;
    }

    /**
     * Obtiene facturas filtradas por varios criterios
     * 
     * @param fechaDesde Fecha desde
     * @param fechaHasta Fecha hasta
     * @param cliente    Nombre o ID del cliente
     * @param estado     Estado de la factura
     * @param expediente Número de expediente
     * @return Lista de facturas filtradas
     */
    public List<Factura> buscarFacturas(LocalDate fechaDesde, LocalDate fechaHasta,
            String cliente, String estado, String expediente) {
        StringBuilder sql = new StringBuilder("SELECT * FROM factura WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (fechaDesde != null) {
            sql.append(" AND fecha_emision >= ?");
            parametros.add(Date.valueOf(fechaDesde));
        }

        if (fechaHasta != null) {
            sql.append(" AND fecha_emision <= ?");
            parametros.add(Date.valueOf(fechaHasta));
        }

        if (cliente != null && !cliente.trim().isEmpty()) {
            sql.append(" AND (nombre_cliente LIKE ? OR id_cliente LIKE ?)");
            parametros.add("%" + cliente + "%");
            parametros.add("%" + cliente + "%");
        }

        if (estado != null && !estado.trim().isEmpty()) {
            sql.append(" AND estado_factura = ?");
            parametros.add(estado);
        }

        if (expediente != null && !expediente.trim().isEmpty()) {
            sql.append(" AND numero_expediente LIKE ?");
            parametros.add("%" + expediente + "%");
        }

        sql.append(" ORDER BY fecha_emision DESC");

        List<Factura> facturas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Establecemos los parámetros
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Factura factura = mapearFactura(rs);
                facturas.add(factura);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar facturas", e);
        }

        return facturas;
    }

    /**
     * Genera el próximo número secuencial de factura
     * 
     * @return Próximo número secuencial en formato '000000001'
     */
    public String generarProximoSecuencial() {
        String sql = "SELECT MAX(CAST(secuencial AS INTEGER)) FROM factura";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            int proximoNumero = 1;
            if (rs.next()) {
                int ultimoNumero = rs.getInt(1);
                proximoNumero = ultimoNumero + 1;
            }

            return String.format("%09d", proximoNumero);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al generar el próximo secuencial", e);
            return "000000001"; // Valor por defecto
        }
    }

    /**
     * Mapea un ResultSet a un objeto Factura
     */
    private Factura mapearFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setId(rs.getInt("id"));
        factura.setTipoDocumento(rs.getString("tipo_documento"));
        factura.setClaveAcceso(rs.getString("clave_acceso"));
        factura.setNumeroAutorizacion(rs.getString("numero_autorizacion"));

        factura.setFechaAutorizacion(obtenerLocalDateTime(rs, "fecha_autorizacion"));

        factura.setAmbiente(rs.getString("ambiente"));
        factura.setEmision(rs.getString("emision"));
        factura.setRucEmisor(rs.getString("ruc_emisor"));
        factura.setRazonSocialEmisor(rs.getString("razon_social_emisor"));
        factura.setDireccionMatriz(rs.getString("direccion_matriz"));
        factura.setDireccionSucursal(rs.getString("direccion_sucursal"));
        factura.setObligadoContabilidad(rs.getBoolean("obligado_contabilidad"));
        factura.setCodigoEstablecimiento(rs.getString("codigo_establecimiento"));
        factura.setCodigoPuntoEmision(rs.getString("codigo_punto_emision"));
        factura.setSecuencial(rs.getString("secuencial"));
        factura.setCodigoDocumento(rs.getString("codigo_documento"));

        factura.setFechaEmision(obtenerLocalDate(rs, "fecha_emision"));

        factura.setNombreCliente(rs.getString("nombre_cliente"));
        factura.setIdCliente(rs.getString("id_cliente"));
        factura.setTipoIdentificacion(rs.getString("tipo_identificacion"));
        factura.setDireccionCliente(rs.getString("direccion_cliente"));
        factura.setTelefonoCliente(rs.getString("telefono_cliente"));
        factura.setEmailCliente(rs.getString("email_cliente"));
        factura.setNumeroExpediente(rs.getString("numero_expediente"));
        factura.setNombreCaso(rs.getString("nombre_caso"));
        factura.setAbogadoResponsable(rs.getString("abogado_responsable"));

        factura.setSubtotal12(rs.getBigDecimal("subtotal12"));
        factura.setSubtotal0(rs.getBigDecimal("subtotal0"));
        factura.setSubtotalNoObjetoIva(rs.getBigDecimal("subtotal_no_objeto_iva"));
        factura.setSubtotalExentoIva(rs.getBigDecimal("subtotal_exento_iva"));
        factura.setSubtotalSinImpuestos(rs.getBigDecimal("subtotal_sin_impuestos"));
        factura.setTotalDescuento(rs.getBigDecimal("total_descuento"));
        factura.setValorIva(rs.getBigDecimal("valor_iva"));
        factura.setPorcentajeIva(rs.getBigDecimal("porcentaje_iva"));
        factura.setDevolucionIva(rs.getBigDecimal("devolucion_iva"));
        factura.setPropina(rs.getBigDecimal("propina"));
        factura.setValorTotal(rs.getBigDecimal("valor_total"));
        factura.setValorSinSubsidio(rs.getBigDecimal("valor_sin_subsidio"));
        factura.setAhorroSubsidio(rs.getBigDecimal("ahorro_subsidio"));
        factura.setFormaPago(rs.getString("forma_pago"));
        factura.setMontoPago(rs.getBigDecimal("monto_pago"));
        factura.setPlazo(rs.getInt("plazo"));
        factura.setEstadoFactura(rs.getString("estado_factura"));
        factura.setPagoRealizado(rs.getBoolean("pago_realizado"));
        factura.setEstadoSRI(rs.getString("estado_sri"));
        factura.setRespuestaSRI(rs.getString("respuesta_sri"));

        factura.setFechaRespuestaSRI(obtenerLocalDateTime(rs, "fecha_respuesta_sri"));
        factura.setUsuarioCreacion(rs.getString("usuario_creacion"));
        factura.setUsuarioModificacion(rs.getString("usuario_modificacion"));
        factura.setCreatedAt(obtenerLocalDateTime(rs, "created_at"));
        factura.setUpdatedAt(obtenerLocalDateTime(rs, "updated_at"));

        return factura;
    }

    /**
     * Mapea un ResultSet a un objeto FacturaDetalle
     */
    private FacturaDetalle mapearFacturaDetalle(ResultSet rs) throws SQLException {
        FacturaDetalle detalle = new FacturaDetalle();
        detalle.setId(rs.getInt("id"));
        detalle.setFacturaId(rs.getInt("factura_id"));
        detalle.setCodigoServicio(rs.getInt("codigo_servicio"));
        detalle.setCodigoAuxiliar(rs.getString("codigo_auxiliar"));
        detalle.setDescripcion(rs.getString("descripcion"));
        detalle.setCantidad(rs.getBigDecimal("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setDescuento(rs.getBigDecimal("descuento"));
        detalle.setValorSubtotal(rs.getBigDecimal("valor_subtotal"));
        detalle.setCodigoImpuesto(rs.getString("codigo_impuesto"));
        detalle.setCodigoTarifaIva(rs.getString("codigo_tarifa_iva"));
        detalle.setPorcentajeIva(rs.getBigDecimal("porcentaje_iva"));
        detalle.setValorIva(rs.getBigDecimal("valor_iva"));
        detalle.setTipoImpuesto(rs.getString("tipo_impuesto"));
        detalle.setUsuarioCreacion(rs.getString("usuario_creacion"));
        detalle.setUsuarioModificacion(rs.getString("usuario_modificacion"));

        detalle.setCreatedAt(obtenerLocalDateTime(rs, "created_at"));
        detalle.setUpdatedAt(obtenerLocalDateTime(rs, "updated_at"));

        return detalle;
    }

    /**
     * Método auxiliar para obtener un LocalDateTime de manera segura desde un
     * ResultSet
     * Maneja casos donde la fecha está almacenada como timestamp, string o número
     */
    private LocalDateTime obtenerLocalDateTime(ResultSet rs, String columnName) {
        try {
            // Intentar directamente como String primero
            String value = rs.getString(columnName);
            if (value == null || value.isEmpty()) {
                return null;
            }

            // Verificar si es un timestamp Unix (milisegundos)
            if (value.matches("\\d+")) {
                try {
                    long timeMillis = Long.parseLong(value);
                    return Instant.ofEpochMilli(timeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                } catch (Exception e) {
                    // Suprimir errores de conversión y continuar
                }
            }

            // Intentar parsear como Timestamp usando getString
            try {
                Timestamp timestamp = rs.getTimestamp(columnName);
                if (timestamp != null) {
                    return timestamp.toLocalDateTime();
                }
            } catch (SQLException e) {
                // Suprimir esta excepción específica y continuar
            }

            // Intentar parsear como ISO fecha-hora
            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException e) {
                // Suprimir el log para no llenar el log con errores esperados
            }
        } catch (SQLException e) {
            // Suprimir el log para reducir el ruido en la consola
        }

        // Si todos los intentos fallan, devolver null
        return null;
    }

    /**
     * Método auxiliar para obtener un LocalDate de manera segura desde un ResultSet
     * Maneja casos donde la fecha está almacenada como Date, string o número
     */
    private LocalDate obtenerLocalDate(ResultSet rs, String columnName) {
        try {
            // Intentar directamente como String primero para evitar excepciones
            String value = rs.getString(columnName);
            if (value == null || value.isEmpty()) {
                return null;
            }

            // Verificar si es un timestamp Unix (milisegundos)
            if (value.matches("\\d+")) {
                try {
                    long timeMillis = Long.parseLong(value);
                    return Instant.ofEpochMilli(timeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                } catch (Exception e) {
                    // Suprimir errores de conversión
                }
            }

            // Intentar como Date solo después de verificar que no es un timestamp
            try {
                Date date = rs.getDate(columnName);
                if (date != null) {
                    return date.toLocalDate();
                }
            } catch (SQLException e) {
                // Suprimir esta excepción específica y continuar
            }

            // Intentar parsear como ISO fecha
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                // Si falla, intentar extraer la fecha de un LocalDateTime
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(value);
                    return dateTime.toLocalDate();
                } catch (DateTimeParseException e2) {
                    // Suprimir mensajes de log para no llenar la consola
                }
            }
        } catch (SQLException e) {
            // Suprimir el log para reducir el ruido en la consola
        }

        // Si todos los métodos fallan, devolver null
        return null;
    }

    /**
     * Verifica si la tabla factura existe en la base de datos
     * 
     * @return true si existe, false en caso contrario
     * @throws SQLException si hay un error al verificar
     */
    private boolean existeTablaFactura() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            try {
                // Intentar ejecutar una consulta simple para verificar si la tabla existe
                stmt.executeQuery("SELECT 1 FROM factura LIMIT 1");
                return true;
            } catch (SQLException e) {
                if (e.getMessage().contains("no such table")) {
                    return false;
                }
                throw e; // Re-lanzar si es otro tipo de error
            }
        }
    }

    /**
     * Crea la tabla factura y factura_detalle en la base de datos
     * 
     * @throws SQLException si hay un error al crear las tablas
     */
    private void crearTablaFactura() throws SQLException {
        LOGGER.info("Creando tablas de factura y factura_detalle");

        // Leer el script SQL para crear las tablas
        StringBuilder sqlBuilder = new StringBuilder();

        // Leer el archivo SQL desde resources
        try (InputStream is = getClass().getResourceAsStream("/database/02_crear_tabla_facturas.sql");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo SQL para crear tablas de factura", e);
            throw new SQLException("Error al leer el archivo SQL: " + e.getMessage());
        }

        // Ejecutar el script SQL
        String[] sqlStatements = sqlBuilder.toString().split(";");

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.executeUpdate(sql);
                }
            }

            LOGGER.info("Tablas de factura creadas exitosamente");
        }
    }

    /**
     * Busca facturas por texto en diferentes campos
     * 
     * @param termino Término de búsqueda
     * @return Lista de facturas que coinciden con la búsqueda
     */
    public List<Factura> buscar(String termino) {
        String sql = "SELECT * FROM factura " +
                "WHERE nombre_cliente LIKE ? " +
                "OR secuencial LIKE ? " +
                "OR id_cliente LIKE ? " +
                "OR numero_expediente LIKE ? " +
                "OR nombre_caso LIKE ? " +
                "ORDER BY fecha_emision DESC";

        List<Factura> facturas = new ArrayList<>();
        String parametro = "%" + termino + "%";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            pstmt.setString(3, parametro);
            pstmt.setString(4, parametro);
            pstmt.setString(5, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapearFactura(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar facturas con término: " + termino, e);
        }

        return facturas;
    }

    /**
     * Genera y envía la factura en formato PDF por correo electrónico
     * 
     * @param facturaId ID de la factura a enviar
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarFacturaPorCorreo(int facturaId) {
        try {
            // 1. Obtener la factura completa
            Factura factura = obtenerPorId(facturaId);
            if (factura == null) {
                LOGGER.warning("No se puede enviar la factura por correo: factura no encontrada con ID " + facturaId);
                return false;
            }

            // 2. Verificar que tenga correo electrónico
            if (factura.getEmailCliente() == null || factura.getEmailCliente().trim().isEmpty()) {
                LOGGER.warning("No se puede enviar la factura por correo: cliente sin correo electrónico");
                return false;
            }

            return enviarFacturaPorCorreoA(facturaId, factura.getEmailCliente());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al enviar factura por correo", e);
            return false;
        }
    }

    /**
     * Genera y envía la factura en formato PDF a una dirección de correo específica
     * 
     * @param facturaId    ID de la factura a enviar
     * @param emailDestino Dirección de correo electrónico del destinatario
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarFacturaPorCorreoA(int facturaId, String emailDestino) {
        try {
            // Validar dirección de correo
            if (emailDestino == null || emailDestino.trim().isEmpty()) {
                LOGGER.warning("No se puede enviar la factura por correo: dirección de correo vacía");
                return false;
            }

            // 1. Obtener la factura completa
            Factura factura = obtenerPorId(facturaId);
            if (factura == null) {
                LOGGER.warning("No se puede enviar la factura por correo: factura no encontrada con ID " + facturaId);
                return false;
            }

            // 2. Generar el PDF
            String rutaPDF = application.service.FacturaPDFService.generarPDF(factura);
            if (rutaPDF == null) {
                LOGGER.warning("No se pudo generar el PDF de la factura");
                return false;
            }

            // 3. Cargar parámetros SMTP
            application.model.ParametrosSMTP smtp = application.service.ParametroService.getInstance()
                    .cargarParametrosSMTP();

            // 4. Preparar mensaje de correo
            String asunto = "Factura Electrónica No. " + factura.getSecuencial();
            String mensaje = "Estimado(a) " + factura.getNombreCliente() + ",\n\n" +
                    "Adjunto encontrará su factura electrónica No. " + factura.getSecuencial() + " " +
                    "por un valor de $" + factura.getValorTotal() + ".\n\n" +
                    "Gracias por confiar en nuestros servicios.\n\n" +
                    "Atentamente,\n" +
                    factura.getRazonSocialEmisor();

            // Verificar conectividad a Internet antes de intentar enviar
            try {
                InetAddress address = InetAddress.getByName("www.google.com");
                if (!address.isReachable(5000)) {
                    LOGGER.warning("No hay conexión a Internet disponible");
                    throw new Exception("No hay conexión a Internet disponible. Verifique su conexión de red.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error al verificar conectividad a Internet", e);
                throw new Exception("No se pudo verificar la conexión a Internet: " + e.getMessage());
            }

            // 5. Enviar correo
            LOGGER.info("Intentando enviar correo a: " + emailDestino + " usando servidor: " + smtp.getServidor());

            boolean enviado = application.service.EmailSenderService.enviarCorreoConAdjunto(
                    emailDestino, asunto, mensaje, rutaPDF, smtp);

            // 6. Registrar el envío en la base de datos (opcional)
            if (enviado) {
                LOGGER.info("Factura enviada correctamente por correo a: " + emailDestino);
                // Aquí se podría registrar el envío en una tabla de historial
            } else {
                LOGGER.warning("Error al enviar la factura por correo");
                throw new Exception(
                        "No se pudo enviar el correo. Verifique la configuración SMTP y su conexión a Internet.");
            }

            return enviado;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al enviar factura por correo: " + e.getMessage(), e);
            // Propagar el error con un mensaje descriptivo
            throw new RuntimeException("Error al enviar correo: " +
                    (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }
}
