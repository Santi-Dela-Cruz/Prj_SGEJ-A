package application.controllers;

import application.service.ParametroService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private Label lblBienvenida, lblCasosActivos, lblClientes, lblFacturas, lblActividades;

    @FXML
    private VBox seccionCasos, seccionFacturas, seccionTareas, seccionAudiencias, seccionDocumentos;

    @FXML
    private TableView<CaseRow> tableCasosRecientes;
    @FXML
    private TableColumn<CaseRow, String> colCasoId, colCasoNombre, colCasoEstado, colCasoFecha;

    @FXML
    private TableView<FacturaRow> tableFacturasPendientes;
    @FXML
    private TableColumn<FacturaRow, String> colFacturaId, colFacturaCliente, colFacturaMonto, colFacturaEstado,
            colFacturaVencimiento;

    @FXML
    private TableView<TareaRow> tableTareasDia;
    @FXML
    private TableColumn<TareaRow, String> colTarea, colTareaResponsable, colTareaHora, colTareaEstado;

    @FXML
    private TableView<AudienciaRow> tableAudiencias;
    @FXML
    private TableColumn<AudienciaRow, String> colAudienciaCaso, colAudienciaFecha, colAudienciaHora, colAudienciaLugar;

    @FXML
    private TableView<DocumentoRow> tableDocumentosRecientes;
    @FXML
    private TableColumn<DocumentoRow, String> colDocNombre, colDocFecha;

    private Pane pnl_Forms;
    private MainController mainController;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void configurarPorRol(String rol) {
        switch (rol) {
            case "Administrador":
                // Mostrar todo
                break;
            case "Asistente Legal":
                ocultarSeccion(seccionFacturas);
                break;
            case "Contador":
                ocultarSeccion(seccionCasos);
                ocultarSeccion(seccionTareas);
                ocultarSeccion(seccionAudiencias);
                break;
        }
    }

    private void ocultarSeccion(VBox seccion) {
        seccion.setVisible(false);
        seccion.setManaged(false);
    }

    /**
     * Aplica los parámetros del sistema al dashboard
     */
    private void aplicarParametros() {
        try {
            // Mostrar novedades (puede ser configurable por el usuario)
            boolean mostrarNovedades = parametroService.getValorBooleano("mostrar_novedades", true);

            // Tema oscuro (podría cambiar el estilo del dashboard)
            boolean temaOscuro = parametroService.getValorBooleano("tema_oscuro", false);
            if (temaOscuro) {
                aplicarTemaOscuro();
            } else {
                aplicarTemaClaro();
            }

            // Idioma del sistema (podría cambiar los textos)
            String idioma = parametroService.getValor("idioma_sistema");
            if ("en".equals(idioma)) {
                traducirInterfazIngles();
            }

            // Notificaciones activas (podría mostrar alertas)
            boolean notificacionesActivas = parametroService.getValorBooleano("notificaciones_activas", true);

            System.out.println("Dashboard configurado con los siguientes parámetros:");
            System.out.println(" - Mostrar novedades: " + mostrarNovedades);
            System.out.println(" - Tema oscuro: " + temaOscuro);
            System.out.println(" - Idioma: " + (idioma != null ? idioma : "es (por defecto)"));
            System.out.println(" - Notificaciones activas: " + notificacionesActivas);

        } catch (Exception e) {
            System.err.println("Error al aplicar parámetros al dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Aplica tema oscuro al dashboard
     */
    private void aplicarTemaOscuro() {
        // Aquí se aplicaría un estilo CSS para tema oscuro
        System.out.println("Aplicando tema oscuro al dashboard...");
        // Ejemplo: seccionCasos.getStyleClass().add("tema-oscuro");
    }

    /**
     * Aplica tema claro al dashboard
     */
    private void aplicarTemaClaro() {
        // Aquí se aplicaría un estilo CSS para tema claro
        System.out.println("Aplicando tema claro al dashboard...");
        // Ejemplo: seccionCasos.getStyleClass().remove("tema-oscuro");
    }

    /**
     * Traduce la interfaz a inglés
     */
    private void traducirInterfazIngles() {
        // Aquí se cambiarían los textos a inglés
        System.out.println("Traduciendo interfaz a inglés...");
        // Ejemplo:
        // lblCasosActivos.setText("Active Cases");
        // lblClientes.setText("Clients");
        // lblFacturas.setText("Invoices");
        // lblActividades.setText("Activities");
    }

    // Servicio de parámetros
    private ParametroService parametroService = ParametroService.getInstancia();

    @FXML
    public void initialize() {
        // Aplicar configuraciones basadas en parámetros del sistema
        aplicarParametros();

        lblBienvenida.setText("Bienvenido, Juan Pérez");

        colCasoId.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));
        colCasoNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNombre()));
        colCasoEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEstado()));
        colCasoFecha.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFecha()));

        colFacturaId.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));
        colFacturaCliente.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCliente()));
        colFacturaMonto.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMonto()));
        colFacturaEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEstado()));
        colFacturaVencimiento.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getVencimiento()));

        colTarea.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTarea()));
        colTareaResponsable.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getResponsable()));
        colTareaHora.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHora()));
        colTareaEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEstado()));

        colAudienciaCaso.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCaso()));
        colAudienciaFecha.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFecha()));
        colAudienciaHora.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHora()));
        colAudienciaLugar.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLugar()));

        colDocNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNombre()));
        colDocFecha.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFecha()));

        tableCasosRecientes.setItems(FXCollections.observableArrayList(
                new CaseRow("C-145", "Pérez vs Estado", "En proceso", "05/07"),
                new CaseRow("C-146", "Torres vs Díaz", "Abierto", "05/07"),
                new CaseRow("C-147", "García vs López", "Cerrado", "04/07"),
                new CaseRow("C-148", "Martínez vs Empresa", "En proceso", "03/07"),
                new CaseRow("C-149", "Ramírez vs Estado", "Abierto", "02/07")));

        tableFacturasPendientes.setItems(FXCollections.observableArrayList(
                new FacturaRow("F-001", "Pérez", "$500", "Pendiente", "10/07"),
                new FacturaRow("F-002", "Díaz", "$300", "Pendiente", "12/07"),
                new FacturaRow("F-003", "García", "$700", "Pendiente", "15/07"),
                new FacturaRow("F-004", "Martínez", "$200", "Pendiente", "18/07"),
                new FacturaRow("F-005", "Ramírez", "$450", "Pendiente", "20/07")));

        tableTareasDia.setItems(FXCollections.observableArrayList(
                new TareaRow("Revisar expediente", "Juan", "09:00", "Pendiente"),
                new TareaRow("Llamar cliente", "Ana", "11:00", "Completada"),
                new TareaRow("Enviar informe", "Luis", "13:00", "Pendiente"),
                new TareaRow("Actualizar datos", "María", "15:00", "Pendiente"),
                new TareaRow("Reunión interna", "Pedro", "16:00", "Completada")));

        tableAudiencias.setItems(FXCollections.observableArrayList(
                new AudienciaRow("C-145", "06/07", "10:00", "Juzgado 1"),
                new AudienciaRow("C-146", "07/07", "12:00", "Juzgado 2"),
                new AudienciaRow("C-147", "08/07", "09:00", "Juzgado 3"),
                new AudienciaRow("C-148", "09/07", "14:00", "Juzgado 4"),
                new AudienciaRow("C-149", "10/07", "11:00", "Juzgado 5")));

        tableDocumentosRecientes.setItems(FXCollections.observableArrayList(
                new DocumentoRow("Demanda laboral.pdf", "03/07"),
                new DocumentoRow("Informe Médico.pdf", "02/07"),
                new DocumentoRow("Contrato arrendamiento.pdf", "01/07"),
                new DocumentoRow("Resolución judicial.pdf", "30/06"),
                new DocumentoRow("Poder notarial.pdf", "29/06")));
    }

    @FXML
    private void irClientes() {
        mainController.cargarModulo("/views/cliente/modulo_cliente.fxml");
    }

    @FXML
    private void irCasos() {
        mainController.cargarModulo("/views/casos_documentos/modulo_casos_documentacion_casos.fxml");
    }

    @FXML
    private void irFacturacion() {
        mainController.cargarModulo("/views/factura/modulo_factura.fxml");
    }

    @FXML
    private void irSistema() {
        mainController.cargarModulo("/views/sistema/modulo_sistema.fxml");
    }

    // Table row classes
    public static class CaseRow {
        private final String id, nombre, estado, fecha;

        public CaseRow(String id, String nombre, String estado, String fecha) {
            this.id = id;
            this.nombre = nombre;
            this.estado = estado;
            this.fecha = fecha;
        }

        public String getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEstado() {
            return estado;
        }

        public String getFecha() {
            return fecha;
        }
    }

    public static class FacturaRow {
        private final String id, cliente, monto, estado, vencimiento;

        public FacturaRow(String id, String cliente, String monto, String estado, String vencimiento) {
            this.id = id;
            this.cliente = cliente;
            this.monto = monto;
            this.estado = estado;
            this.vencimiento = vencimiento;
        }

        public String getId() {
            return id;
        }

        public String getCliente() {
            return cliente;
        }

        public String getMonto() {
            return monto;
        }

        public String getEstado() {
            return estado;
        }

        public String getVencimiento() {
            return vencimiento;
        }
    }

    public static class TareaRow {
        private final String tarea, responsable, hora, estado;

        public TareaRow(String tarea, String responsable, String hora, String estado) {
            this.tarea = tarea;
            this.responsable = responsable;
            this.hora = hora;
            this.estado = estado;
        }

        public String getTarea() {
            return tarea;
        }

        public String getResponsable() {
            return responsable;
        }

        public String getHora() {
            return hora;
        }

        public String getEstado() {
            return estado;
        }
    }

    public static class AudienciaRow {
        private final String caso, fecha, hora, lugar;

        public AudienciaRow(String caso, String fecha, String hora, String lugar) {
            this.caso = caso;
            this.fecha = fecha;
            this.hora = hora;
            this.lugar = lugar;
        }

        public String getCaso() {
            return caso;
        }

        public String getFecha() {
            return fecha;
        }

        public String getHora() {
            return hora;
        }

        public String getLugar() {
            return lugar;
        }
    }

    public static class DocumentoRow {
        private final String nombre, fecha;

        public DocumentoRow(String nombre, String fecha) {
            this.nombre = nombre;
            this.fecha = fecha;
        }

        public String getNombre() {
            return nombre;
        }

        public String getFecha() {
            return fecha;
        }
    }
}
