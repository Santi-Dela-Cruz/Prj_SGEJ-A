package application.controllers.casos_documentacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DetalleCasoBitacoraController {
    @FXML
    private Label lblExpediente, lblTitulo, lblTipo, lblFecha, lblAbogado, lblEstado;
    @FXML
    private TableView<BitacoraDemo> tbBitacora;
    @FXML
    private TableColumn<BitacoraDemo, String> tbcFecha, tbcDescripcion;
    @FXML
    private Button btnAgregarBitacora;
    @FXML
    private Button btnRegresar;

    private Runnable onRegresar;

    public void setOnRegresar(Runnable handler) {
        this.onRegresar = handler;
    }

    @FXML
    private void initialize() {
        if (btnRegresar != null) {
            btnRegresar.setOnAction(e -> {
                if (onRegresar != null)
                    onRegresar.run();
            });
        }
    }

    public void setCaso(ModuloCasosController.CasoDemo caso) {
        lblExpediente.setText(caso.numeroExpediente());
        lblTitulo.setText(caso.titulo());
        lblTipo.setText(caso.tipo());
        lblFecha.setText(caso.fecha());
        lblAbogado.setText(caso.abogado());
        lblEstado.setText(caso.estado());
        // Demo: cargar bitácora
        tbBitacora.getItems().setAll(
                new BitacoraDemo("2024-06-01", "Caso creado"),
                new BitacoraDemo("2024-06-02", "Audiencia programada"));
    }

    public static class BitacoraDemo {
        private final String fecha;
        private final String descripcion;

        public BitacoraDemo(String fecha, String descripcion) {
            this.fecha = fecha;
            this.descripcion = descripcion;
        }

        public String getFecha() {
            return fecha;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}
