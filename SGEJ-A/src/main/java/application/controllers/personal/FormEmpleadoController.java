package application.controllers.personal;

import application.controllers.DialogUtil;
import application.model.Personal;
import application.service.EmpleadoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class FormEmpleadoController {

    @FXML
    private Button btn_Guardar, btn_Cancelar;
    @FXML
    private TextField txtf_Nombres, txtf_Apellidos, txtf_NumeroIdentificacion, txtf_Direccion, txtf_Correo,
            txtf_Telefono;
    @FXML
    private ComboBox<String> cbx_Rol, cbx_TipoIdentificacion, cbx_Estado;
    @FXML
    private DatePicker dt_FechaIngreso;
    @FXML
    private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    private EmpleadoService empleadoService;
    private Personal personalActual;
    private String modoActual;

    @FXML
    private void initialize() {
        empleadoService = new EmpleadoService();

        cbx_Estado.getItems().addAll("Activo", "Inactivo");
        cbx_TipoIdentificacion.getItems().addAll("Cédula", "RUC", "Pasaporte");
        cbx_Rol.getItems().addAll("Gerente", "Administrador", "Empleado", "Abogado");

        btn_Guardar.setOnAction(event -> {
            if (txtf_Nombres.getText().isEmpty() ||
                    txtf_Apellidos.getText().isEmpty() ||
                    txtf_NumeroIdentificacion.getText().isEmpty() ||
                    cbx_TipoIdentificacion.getValue() == null ||
                    cbx_Rol.getValue() == null ||
                    cbx_Estado.getValue() == null ||
                    dt_FechaIngreso.getValue() == null) {

                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios:\n" +
                                " - Nombres\n" +
                                " - Apellidos\n" +
                                " - Número de Identificación\n" +
                                " - Tipo de Identificación\n" +
                                " - Rol\n" +
                                " - Estado\n" +
                                " - Fecha de Ingreso",
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar este empleado?",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO));

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                guardarEmpleado();
                if (onGuardar != null)
                    onGuardar.run();
            }
        });

        btn_Cancelar.setOnAction(event -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO));

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null)
                    onCancelar.run();
            }
        });
    }

    /**
     * Guarda los datos del formulario en la base de datos
     */
    private void guardarEmpleado() {
        try {
            Personal personal = new Personal();

            // Si estamos en modo edición, cargar el ID del empleado actual
            if (personalActual != null && "EDITAR".equalsIgnoreCase(modoActual)) {
                personal.setId(personalActual.getId());
            }

            // Asignar valores del formulario al objeto Personal
            personal.setNombres(txtf_Nombres.getText());
            personal.setApellidos(txtf_Apellidos.getText());
            personal.setNumeroIdentificacion(txtf_NumeroIdentificacion.getText());
            personal.setTipoIdentificacion(cbx_TipoIdentificacion.getValue());
            personal.setTelefono(txtf_Telefono.getText());
            personal.setCorreo(txtf_Correo.getText());
            personal.setDireccion(txtf_Direccion.getText());
            personal.setFechaIngreso(dt_FechaIngreso.getValue());
            personal.setRol(cbx_Rol.getValue());
            personal.setEstado(cbx_Estado.getValue());

            boolean resultado;

            // Guardar o actualizar según el modo
            if ("EDITAR".equalsIgnoreCase(modoActual) && personalActual != null) {
                resultado = empleadoService.actualizarEmpleado(personal);
                if (resultado) {
                    DialogUtil.mostrarDialogo("Éxito", "Empleado actualizado correctamente", "info",
                            List.of(ButtonType.OK));
                } else {
                    DialogUtil.mostrarDialogo("Error", "No se pudo actualizar el empleado", "error",
                            List.of(ButtonType.OK));
                }
            } else {
                int id = empleadoService.registrarEmpleado(personal);
                resultado = (id > 0);
                if (resultado) {
                    DialogUtil.mostrarDialogo("Éxito", "Empleado registrado correctamente con ID: " + id, "info",
                            List.of(ButtonType.OK));
                } else {
                    DialogUtil.mostrarDialogo("Error", "No se pudo registrar el empleado", "error",
                            List.of(ButtonType.OK));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo("Error", "Error al guardar: " + e.getMessage(), "error", List.of(ButtonType.OK));
        }
    }

    /**
     * Carga los datos de un empleado en el formulario para edición o visualización
     */
    public void cargarEmpleado(ModuloEmpleadoController.EmpleadoDemo empleado) {
        try {
            // Primero intentamos cargar el empleado desde la base de datos por su número de
            // identificación
            personalActual = empleadoService.obtenerEmpleadoPorIdentificacion(empleado.numeroIdentificacion());

            // Si no lo encontramos, creamos un objeto temporal con los datos del
            // EmpleadoDemo
            if (personalActual == null) {
                personalActual = new Personal();
                personalActual.setNombres(empleado.nombres());
                personalActual.setApellidos(empleado.apellidos());
                personalActual.setNumeroIdentificacion(empleado.numeroIdentificacion());
                personalActual.setDireccion(empleado.direccion());
                personalActual.setFechaIngreso(empleado.fechaIngreso());
                personalActual.setTelefono(empleado.telefono());
                personalActual.setCorreo(empleado.correo());
                personalActual.setRol(empleado.rol());
                personalActual.setTipoIdentificacion(empleado.tipoIdentificacion());
                personalActual.setEstado(empleado.estado());
            }

            // Cargar datos en el formulario
            txtf_Nombres.setText(personalActual.getNombres());
            txtf_Apellidos.setText(personalActual.getApellidos());
            txtf_NumeroIdentificacion.setText(personalActual.getNumeroIdentificacion());
            txtf_Direccion.setText(personalActual.getDireccion());
            dt_FechaIngreso.setValue(personalActual.getFechaIngreso());
            txtf_Telefono.setText(personalActual.getTelefono());
            txtf_Correo.setText(personalActual.getCorreo());
            cbx_Rol.setValue(personalActual.getRol());
            cbx_TipoIdentificacion.setValue(personalActual.getTipoIdentificacion());
            cbx_Estado.setValue(personalActual.getEstado());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo("Error", "No se pudo cargar el empleado: " + e.getMessage(), "error",
                    List.of(ButtonType.OK));
        }
    }

    /**
     * Configura el formulario según el modo de uso (nuevo, editar, ver)
     */
    public void setModo(String modo) {
        this.modoActual = modo;
        boolean esEditar = "EDITAR".equalsIgnoreCase(modo);
        boolean esVer = "VER".equalsIgnoreCase(modo);
        // boolean esRegistrar = !esEditar && !esVer; // No se usa pero se mantiene para
        // claridad

        if (esEditar) {
            txt_TituloForm.setText("Editar Empleado");
        } else if (esVer) {
            txt_TituloForm.setText("Ver Empleado");
        } else {
            txt_TituloForm.setText("Registrar nuevo Empleado");
        }

        if (esVer) {
            txtf_Nombres.setEditable(false);
            txtf_Apellidos.setEditable(false);
            txtf_NumeroIdentificacion.setEditable(false);
            cbx_TipoIdentificacion.setDisable(true);
            txtf_Telefono.setEditable(false);
            txtf_Correo.setEditable(false);
            cbx_Estado.setDisable(true);
            txtf_Direccion.setEditable(false);
            cbx_Rol.setDisable(true);
            dt_FechaIngreso.setDisable(true);
            btn_Guardar.setDisable(true);
        } else if (esEditar) {
            txtf_Nombres.setEditable(true);
            txtf_Apellidos.setEditable(true);
            txtf_NumeroIdentificacion.setEditable(false);
            cbx_TipoIdentificacion.setDisable(true);
            txtf_Telefono.setEditable(true);
            txtf_Correo.setEditable(true);
            cbx_Estado.setDisable(false); // Permitir cambiar el estado al editar
            txtf_Direccion.setEditable(true);
            cbx_Rol.setDisable(false);
            dt_FechaIngreso.setDisable(false);
            btn_Guardar.setDisable(false);
        } else { // REGISTRAR
            txtf_Nombres.setEditable(true);
            txtf_Apellidos.setEditable(true);
            txtf_NumeroIdentificacion.setEditable(true);
            cbx_TipoIdentificacion.setDisable(false);
            txtf_Telefono.setEditable(true);
            txtf_Correo.setEditable(true);
            cbx_Estado.setDisable(false);
            txtf_Direccion.setEditable(true);
            cbx_Rol.setDisable(false);
            dt_FechaIngreso.setDisable(false);
            btn_Guardar.setDisable(false);
        }
    }
}