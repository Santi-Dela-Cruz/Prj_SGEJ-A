package application.controllers;

import application.service.ParametroService;
import application.utils.ValidationUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controlador para gestionar los parámetros legales/fiscales del sistema
 */
public class ConfiguracionFiscalController implements Initializable {
    
    private static final Logger LOGGER = Logger.getLogger(ConfiguracionFiscalController.class.getName());

    @FXML private TextField txtPorcentajeIva;
    @FXML private TextField txtRucInstitucional;
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtDireccionMatriz;
    @FXML private TextField txtDireccionSucursal;
    @FXML private TextField txtSubtotalPorcentaje;
    @FXML private Button btnGuardar;
    @FXML private VBox rootPane;

    private ParametroService parametroService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            LOGGER.info("Inicializando ConfiguracionFiscalController");
            
            // Obtener instancia del servicio de parámetros
            parametroService = ParametroService.getInstance();
            
            // Cargar valores actuales
            cargarValoresActuales();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar ConfiguracionFiscalController", e);
            mostrarAlerta("Error", "No se pudieron cargar los parámetros del sistema", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Carga los valores actuales de los parámetros en los campos de texto
     */
    private void cargarValoresActuales() {
        try {
            txtPorcentajeIva.setText(parametroService.getValor("porcentaje_iva", "12"));
            txtRucInstitucional.setText(parametroService.getValor("ruc_institucional", "9999999999001"));
            txtRazonSocial.setText(parametroService.getValor("razon_social", "EMPRESA DEMO"));
            txtDireccionMatriz.setText(parametroService.getValor("direccion_matriz", "DIRECCION MATRIZ"));
            txtDireccionSucursal.setText(parametroService.getValor("direccion_sucursal", "DIRECCION SUCURSAL"));
            txtSubtotalPorcentaje.setText(parametroService.getValor("subtotal_porcentaje", "12"));
            
            LOGGER.info("Valores cargados correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar valores actuales", e);
        }
    }
    
    /**
     * Guarda los cambios en los parámetros
     */
    @FXML
    private void guardarCambios(ActionEvent event) {
        try {
            LOGGER.info("Guardando cambios en parámetros fiscales");
            
            // Validar campos
            if (!validarCampos()) {
                return;
            }
            
            // Obtener valores de los campos
            String porcentajeIva = txtPorcentajeIva.getText().trim();
            String rucInstitucional = txtRucInstitucional.getText().trim();
            String razonSocial = txtRazonSocial.getText().trim();
            String direccionMatriz = txtDireccionMatriz.getText().trim();
            String direccionSucursal = txtDireccionSucursal.getText().trim();
            String subtotalPorcentaje = txtSubtotalPorcentaje.getText().trim();
            
            // Actualizar parámetros
            boolean resultado = parametroService.actualizarParametrosLegalesFiscales(
                porcentajeIva, rucInstitucional, razonSocial, 
                direccionMatriz, direccionSucursal, subtotalPorcentaje
            );
            
            if (resultado) {
                mostrarAlerta("Éxito", "Parámetros actualizados correctamente", Alert.AlertType.INFORMATION);
                // Refrescar valores por si alguno no se guardó correctamente
                cargarValoresActuales();
            } else {
                mostrarAlerta("Error", "No se pudieron actualizar todos los parámetros", Alert.AlertType.WARNING);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar parámetros fiscales", e);
            mostrarAlerta("Error", "Ocurrió un error al guardar los cambios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Valida que los campos obligatorios estén completos
     */
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        
        if (txtPorcentajeIva.getText().trim().isEmpty()) {
            errores.append("- El porcentaje de IVA es obligatorio\n");
        } else {
            try {
                String ivaText = txtPorcentajeIva.getText().trim();
                double iva = Double.parseDouble(ivaText);
                // Validación específica: IVA solo puede ser 0 o 0.12 (o 12%)
                if (!(iva == 0 || iva == 0.12 || iva == 12)) {
                    errores.append("- El porcentaje de IVA solo puede ser 0 o 12%\n");
                }
                
                // Si ingresó 12, convertirlo a 0.12 para consistencia
                if (iva == 12) {
                    txtPorcentajeIva.setText("0.12");
                }
            } catch (NumberFormatException e) {
                errores.append("- El porcentaje de IVA debe ser un número válido\n");
            }
        }
        
        if (txtRucInstitucional.getText().trim().isEmpty()) {
            errores.append("- El RUC institucional es obligatorio\n");
        } else if (!application.utils.ValidationUtils.isValidRuc(txtRucInstitucional.getText().trim())) {
            errores.append("- El RUC ingresado no es válido. Debe ser un RUC ecuatoriano válido\n");
        }
        
        if (txtRazonSocial.getText().trim().isEmpty()) {
            errores.append("- La razón social es obligatoria\n");
        }
        
        if (txtDireccionMatriz.getText().trim().isEmpty()) {
            errores.append("- La dirección de matriz es obligatoria\n");
        }
        
        if (txtSubtotalPorcentaje.getText().trim().isEmpty()) {
            errores.append("- El porcentaje para subtotal es obligatorio\n");
        } else {
            try {
                double subtotal = Double.parseDouble(txtSubtotalPorcentaje.getText().trim());
                if (subtotal < 0 || subtotal > 100) {
                    errores.append("- El porcentaje para subtotal debe estar entre 0 y 100\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El porcentaje para subtotal debe ser un número válido\n");
            }
        }
        
        if (errores.length() > 0) {
            mostrarAlerta("Validación", "Por favor corrija los siguientes errores:\n" + errores.toString(), Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    /**
     * Muestra una alerta con el mensaje y tipo especificados
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
