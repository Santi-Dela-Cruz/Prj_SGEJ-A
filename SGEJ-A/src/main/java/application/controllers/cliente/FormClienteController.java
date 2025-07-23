package application.controllers.cliente;

import application.model.Cliente;
import application.service.ClienteService;
import application.controllers.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para el formulario de cliente
 */
public class FormClienteController implements Initializable {

    @FXML private StackPane pnl_Titulo;
    @FXML private Text txt_TituloForm;
    @FXML private ComboBox<String> cbx_TipoCliente;
    @FXML private TextField txtf_Nombres;
    @FXML private ComboBox<String> cbx_TipoIdentificacion;
    @FXML private TextField txtf_NumeroIdentificacion;
    @FXML private TextField txtf_Direccion;
    @FXML private TextField txtf_Telefono;
    @FXML private TextField txtf_Correo;
    @FXML private ComboBox<String> cbx_Estado;
    @FXML private DatePicker dt_FechaIngreso;
    
    // Paneles específicos por tipo de cliente
    @FXML private VBox vbox_PersonaNatural;
    @FXML private VBox vbox_PersonaJuridica;
    
    // Campos específicos para persona natural
    @FXML private Label lbl_EstadoCivil;
    @FXML private ComboBox<String> cbx_EstadoCivil;
    
    // Campos específicos para persona jurídica
    @FXML private Label lbl_RepresentanteLegal;
    @FXML private TextField txtf_RepresentanteLegal;
    @FXML private Label lbl_DireccionFiscal;
    @FXML private TextField txtf_DireccionFiscal;
    
    // Labels de error para validación
    @FXML private Label lbl_ErrorTipoCliente;
    @FXML private Label lbl_ErrorNombres;
    @FXML private Label lbl_ErrorTipoIdentificacion;
    @FXML private Label lbl_ErrorTelefono;
    @FXML private Label lbl_ErrorCorreo;
    @FXML private Label lbl_ErrorRepresentante;
    @FXML private Label lbl_ErrorDireccionFiscal;
    
    // Botones
    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;

    // Servicios
    private ClienteService clienteService;
    
    // Estado del formulario
    private ModoFormulario modo;
    private Cliente clienteActual;
    
    public enum ModoFormulario {
        REGISTRAR, VISUALIZAR, EDITAR
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteService = new ClienteService();
        configurarComponentes();
        configurarEventos();
        configurarModo(ModoFormulario.REGISTRAR);
    }

    /**
     * Configura los componentes del formulario
     */
    private void configurarComponentes() {
        try {
            System.out.println("🔧 Iniciando configuración de componentes...");
            
            // Configurar ComboBox de tipo de cliente
            if (cbx_TipoCliente != null) {
                cbx_TipoCliente.getItems().clear();
                cbx_TipoCliente.getItems().addAll("Persona Natural", "Persona Jurídica");
                cbx_TipoCliente.setPromptText("Seleccionar tipo de cliente");
                aplicarEstilosComboBox(cbx_TipoCliente);
                System.out.println("✅ ComboBox Tipo Cliente configurado: " + cbx_TipoCliente.getItems().size() + " items");
            } else {
                System.err.println("❌ cbx_TipoCliente es null");
            }
            
            // Configurar ComboBox de tipo de identificación
            if (cbx_TipoIdentificacion != null) {
                cbx_TipoIdentificacion.getItems().clear();
                cbx_TipoIdentificacion.getItems().addAll("Cédula", "RUC", "Pasaporte");
                cbx_TipoIdentificacion.setPromptText("Seleccionar tipo de identificación");
                aplicarEstilosComboBox(cbx_TipoIdentificacion);
                System.out.println("✅ ComboBox Tipo Identificación configurado: " + cbx_TipoIdentificacion.getItems().size() + " items");
            } else {
                System.err.println("❌ cbx_TipoIdentificacion es null");
            }
            
            // Configurar ComboBox de estado
            if (cbx_Estado != null) {
                cbx_Estado.getItems().clear();
                cbx_Estado.getItems().addAll("Activo", "Inactivo");
                cbx_Estado.getSelectionModel().select("Activo");
                aplicarEstilosComboBox(cbx_Estado);
                System.out.println("✅ ComboBox Estado configurado: " + cbx_Estado.getItems().size() + " items");
            } else {
                System.err.println("❌ cbx_Estado es null");
            }
            
            // Configurar ComboBox de estado civil
            if (cbx_EstadoCivil != null) {
                cbx_EstadoCivil.getItems().clear();
                cbx_EstadoCivil.getItems().addAll("Soltero", "Casado", "Divorciado", "Viudo", "Unión Libre");
                cbx_EstadoCivil.setPromptText("Seleccionar estado civil");
                aplicarEstilosComboBox(cbx_EstadoCivil);
                System.out.println("✅ ComboBox Estado Civil configurado: " + cbx_EstadoCivil.getItems().size() + " items");
            } else {
                System.err.println("❌ cbx_EstadoCivil es null");
            }
            
            // Configurar fecha por defecto
            if (dt_FechaIngreso != null) {
                dt_FechaIngreso.setValue(LocalDate.now());
                System.out.println("✅ DatePicker configurado con fecha: " + dt_FechaIngreso.getValue());
            } else {
                System.err.println("❌ dt_FechaIngreso es null");
            }
            
            // Configurar iconos en botones
            configurarIconosBotones();
            
            // Configurar visibilidad inicial de campos condicionales
            mostrarCamposPersonaNatural(false);
            mostrarCamposPersonaJuridica(false);
            
            System.out.println("✅ Componentes configurados correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al configurar componentes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        System.out.println("🔗 Configurando eventos...");
        
        // Evento para cambio de tipo de cliente
        if (cbx_TipoCliente != null) {
            cbx_TipoCliente.setOnAction(_ -> {
                String tipoSeleccionado = cbx_TipoCliente.getValue();
                System.out.println("🔄 Tipo cliente seleccionado: " + tipoSeleccionado);
                if (tipoSeleccionado != null) {
                    if (tipoSeleccionado.equals("Persona Natural")) {
                        mostrarCamposPersonaNatural(true);
                        mostrarCamposPersonaJuridica(false);
                        System.out.println("👤 Mostrando campos persona natural");
                    } else if (tipoSeleccionado.equals("Persona Jurídica")) {
                        mostrarCamposPersonaNatural(false);
                        mostrarCamposPersonaJuridica(true);
                        System.out.println("🏢 Mostrando campos persona jurídica");
                    }
                }
            });
            System.out.println("✅ Evento cbx_TipoCliente configurado");
        }
        
        // Agregar eventos de debug para otros ComboBox
        if (cbx_TipoIdentificacion != null) {
            cbx_TipoIdentificacion.setOnAction(_ -> {
                String tipoId = cbx_TipoIdentificacion.getValue();
                System.out.println("🆔 Tipo identificación seleccionado: " + tipoId);
            });
            System.out.println("✅ Evento cbx_TipoIdentificacion configurado");
        }
        
        if (cbx_Estado != null) {
            cbx_Estado.setOnAction(_ -> {
                String estado = cbx_Estado.getValue();
                System.out.println("📊 Estado seleccionado: " + estado);
            });
            System.out.println("✅ Evento cbx_Estado configurado");
        }
        
        if (cbx_EstadoCivil != null) {
            cbx_EstadoCivil.setOnAction(_ -> {
                String estadoCivil = cbx_EstadoCivil.getValue();
                System.out.println("💍 Estado civil seleccionado: " + estadoCivil);
            });
            System.out.println("✅ Evento cbx_EstadoCivil configurado");
        }

        // Evento para botón guardar
        if (btn_Guardar != null) {
            btn_Guardar.setOnAction(_ -> guardarCliente());
            System.out.println("✅ Evento btn_Guardar configurado");
        }

        // Evento para botón cancelar
        if (btn_Cancelar != null) {
            btn_Cancelar.setOnAction(_ -> cancelarOperacion());
            System.out.println("✅ Evento btn_Cancelar configurado");
        }
        
        // Configurar eventos para limpiar errores al escribir/seleccionar
        configurarEventosLimpiarErrores();
        
        System.out.println("✅ Todos los eventos configurados");
    }
    
    /**
     * Configura eventos para limpiar errores cuando el usuario interactúa con los campos
     */
    private void configurarEventosLimpiarErrores() {
        // Nombres
        if (txtf_Nombres != null && lbl_ErrorNombres != null) {
            txtf_Nombres.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    lbl_ErrorNombres.setVisible(false);
                }
            });
        }
        
        // Tipo de Cliente
        if (cbx_TipoCliente != null && lbl_ErrorTipoCliente != null) {
            cbx_TipoCliente.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    lbl_ErrorTipoCliente.setVisible(false);
                }
            });
        }
        
        // Tipo de Identificación
        if (cbx_TipoIdentificacion != null && lbl_ErrorTipoIdentificacion != null) {
            cbx_TipoIdentificacion.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    lbl_ErrorTipoIdentificacion.setVisible(false);
                }
            });
        }
        
        // Teléfono
        if (txtf_Telefono != null && lbl_ErrorTelefono != null) {
            txtf_Telefono.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    lbl_ErrorTelefono.setVisible(false);
                }
            });
        }
        
        // Correo
        if (txtf_Correo != null && lbl_ErrorCorreo != null) {
            txtf_Correo.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    lbl_ErrorCorreo.setVisible(false);
                }
            });
        }
        
        // Representante Legal
        if (txtf_RepresentanteLegal != null && lbl_ErrorRepresentante != null) {
            txtf_RepresentanteLegal.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    lbl_ErrorRepresentante.setVisible(false);
                }
            });
        }
        
        // Dirección Fiscal
        if (txtf_DireccionFiscal != null && lbl_ErrorDireccionFiscal != null) {
            txtf_DireccionFiscal.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    lbl_ErrorDireccionFiscal.setVisible(false);
                }
            });
        }
    }

    /**
     * Configura el modo del formulario
     */
    public void configurarModo(ModoFormulario modo) {
        this.modo = modo;
        
        switch (modo) {
            case REGISTRAR:
                txt_TituloForm.setText("REGISTRAR CLIENTE");
                habilitarCampos(true);
                btn_Guardar.setText("Guardar");
                btn_Guardar.setVisible(true);
                limpiarFormulario();
                break;
                
            case VISUALIZAR:
                txt_TituloForm.setText("VISUALIZAR CLIENTE");
                habilitarCampos(false);
                btn_Guardar.setVisible(false);
                break;
                
            case EDITAR:
                txt_TituloForm.setText("EDITAR CLIENTE");
                habilitarCamposEditables(true);
                btn_Guardar.setText("Actualizar");
                btn_Guardar.setVisible(true);
                break;
        }
        
        // Actualizar iconos según el modo
        actualizarIconosPorModo();
    }

    /**
     * Cargar datos de un cliente en el formulario
     */
    public void cargarCliente(Cliente cliente) {
        this.clienteActual = cliente;
        
        if (cliente != null) {
            txtf_Nombres.setText(cliente.getNombreCompleto());
            
            // Configurar tipo de persona
            if (cliente.getTipoPersona() == Cliente.TipoPersona.NATURAL) {
                cbx_TipoCliente.getSelectionModel().select("Persona Natural");
                mostrarCamposPersonaNatural(true);
                mostrarCamposPersonaJuridica(false);
                cbx_EstadoCivil.getSelectionModel().select(cliente.getEstadoCivil());
            } else {
                cbx_TipoCliente.getSelectionModel().select("Persona Jurídica");
                mostrarCamposPersonaNatural(false);
                mostrarCamposPersonaJuridica(true);
                txtf_RepresentanteLegal.setText(cliente.getRepresentanteLegal());
                txtf_DireccionFiscal.setText(cliente.getDireccionFiscal());
            }
            
            // Configurar tipo de identificación
            switch (cliente.getTipoIdentificacion()) {
                case CEDULA:
                    cbx_TipoIdentificacion.getSelectionModel().select("Cédula");
                    break;
                case RUC:
                    cbx_TipoIdentificacion.getSelectionModel().select("RUC");
                    break;
                case PASAPORTE:
                    cbx_TipoIdentificacion.getSelectionModel().select("Pasaporte");
                    break;
            }
            
            txtf_NumeroIdentificacion.setText(cliente.getNumeroIdentificacion());
            txtf_Direccion.setText(cliente.getDireccion());
            txtf_Telefono.setText(cliente.getTelefono());
            txtf_Correo.setText(cliente.getCorreoElectronico());
            
            // Configurar estado
            cbx_Estado.getSelectionModel().select(cliente.getEstado() == Cliente.Estado.ACTIVO ? "Activo" : "Inactivo");
            
            dt_FechaIngreso.setValue(cliente.getFechaRegistro());
        }
    }

    /**
     * Guardar o actualizar cliente
     */
    private void guardarCliente() {
        try {
            // Validar campos obligatorios
            if (!validarCamposObligatorios()) {
                return;
            }
            
            // Mostrar confirmación antes de guardar
            String mensaje = (modo == ModoFormulario.REGISTRAR) ? 
                "¿Está seguro de que desea registrar este cliente?" : 
                "¿Está seguro de que desea actualizar este cliente?";
            
            if (!mostrarConfirmacion("Confirmar Operación", mensaje)) {
                return;
            }
            
            Cliente cliente = construirClienteDesdeFormulario();
            
            if (modo == ModoFormulario.REGISTRAR) {
                ClienteService.ResultadoOperacion resultado = clienteService.registrarCliente(cliente);
                
                if (resultado.esExitoso()) {
                    mostrarMensajeInformacion("Éxito", resultado.getMensaje());
                    limpiarFormulario();
                    
                    // Ejecutar callback de guardar
                    if (onGuardarCallback != null) {
                        onGuardarCallback.run();
                    }
                } else {
                    mostrarMensajeError("Error", resultado.getMensaje());
                }
                
            } else if (modo == ModoFormulario.EDITAR) {
                cliente.setId(clienteActual.getId());
                cliente.setTipoPersona(clienteActual.getTipoPersona());
                
                ClienteService.ResultadoOperacion resultado = clienteService.actualizarCliente(cliente);
                
                if (resultado.esExitoso()) {
                    mostrarMensajeInformacion("Éxito", resultado.getMensaje());
                    // Actualizar el cliente actual con los nuevos datos
                    clienteActual = cliente;
                    
                    // Ejecutar callback de guardar
                    if (onGuardarCallback != null) {
                        onGuardarCallback.run();
                    }
                } else {
                    mostrarMensajeError("Error", resultado.getMensaje());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error completo: " + e.getMessage());
            e.printStackTrace();
            mostrarMensajeError("Error", "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    /**
     * Validar campos obligatorios
     */
    private boolean validarCamposObligatorios() {
        boolean esValido = true;
        
        // Ocultar todos los labels de error primero
        ocultarErrores();
        
        // CAMPOS OBLIGATORIOS GENERALES (para todos los tipos de cliente)
        
        // Validar nombre
        if (txtf_Nombres.getText() == null || txtf_Nombres.getText().trim().isEmpty()) {
            mostrarError(lbl_ErrorNombres);
            esValido = false;
        } else {
            // Validar formato del nombre
            if (!validarFormatoNombre(txtf_Nombres.getText())) {
                mostrarError(lbl_ErrorNombres);
                lbl_ErrorNombres.setText("*Solo se permiten letras, espacios, ñ y vocales con tildes");
                esValido = false;
            }
        }
        
        // Validar tipo de cliente
        if (cbx_TipoCliente.getValue() == null) {
            mostrarError(lbl_ErrorTipoCliente);
            esValido = false;
        }
        
        // Validar tipo de identificación
        if (cbx_TipoIdentificacion.getValue() == null) {
            mostrarError(lbl_ErrorTipoIdentificacion);
            esValido = false;
        }
        
        // Validar teléfono
        if (txtf_Telefono.getText() == null || txtf_Telefono.getText().trim().isEmpty()) {
            mostrarError(lbl_ErrorTelefono);
            esValido = false;
        }
        
        // Validar correo
        if (txtf_Correo.getText() == null || txtf_Correo.getText().trim().isEmpty()) {
            mostrarError(lbl_ErrorCorreo);
            esValido = false;
        } else {
            // Validar formato de correo básico
            String correo = txtf_Correo.getText().trim();
            if (!correo.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                mostrarError(lbl_ErrorCorreo);
                lbl_ErrorCorreo.setText("*Formato de correo inválido");
                esValido = false;
            }
        }
        
        // NOTA: txtf_Direccion (dirección general) NO es obligatorio para ningún tipo de cliente
        // NOTA: cbx_EstadoCivil (estado civil) NO es obligatorio para personas naturales
        
        // CAMPOS OBLIGATORIOS ESPECÍFICOS DE PERSONA JURÍDICA
        if (cbx_TipoCliente.getValue() != null && cbx_TipoCliente.getValue().equals("Persona Jurídica")) {
            // Validar representante legal (obligatorio solo para persona jurídica)
            if (txtf_RepresentanteLegal.getText() == null || txtf_RepresentanteLegal.getText().trim().isEmpty()) {
                if (lbl_ErrorRepresentante == null) {
                    System.err.println("⚠️ Label de error para Representante Legal no encontrado");
                } else {
                    mostrarError(lbl_ErrorRepresentante);
                }
                esValido = false;
            }
            
            // Validar dirección fiscal (obligatorio solo para persona jurídica)
            if (txtf_DireccionFiscal.getText() == null || txtf_DireccionFiscal.getText().trim().isEmpty()) {
                if (lbl_ErrorDireccionFiscal == null) {
                    System.err.println("⚠️ Label de error para Dirección Fiscal no encontrado");
                } else {
                    mostrarError(lbl_ErrorDireccionFiscal);
                }
                esValido = false;
            }
        }
        
        // Si hay errores, mostrar mensaje general
        if (!esValido) {
            mostrarMensajeAdvertencia("Campos Obligatorios", 
                "Por favor complete correctamente los campos marcados en rojo.");
        }
        
        return esValido;
    }
    
    /**
     * Ocultar todos los labels de error
     */
    private void ocultarErrores() {
        if (lbl_ErrorNombres != null) {
            lbl_ErrorNombres.setVisible(false);
            lbl_ErrorNombres.setText("*Campo Obligatorio"); // Resetear texto
        }
        if (lbl_ErrorTipoCliente != null) lbl_ErrorTipoCliente.setVisible(false);
        if (lbl_ErrorTipoIdentificacion != null) lbl_ErrorTipoIdentificacion.setVisible(false);
        if (lbl_ErrorTelefono != null) lbl_ErrorTelefono.setVisible(false);
        if (lbl_ErrorCorreo != null) {
            lbl_ErrorCorreo.setVisible(false);
            lbl_ErrorCorreo.setText("*Campo Obligatorio"); // Resetear texto
        }
        if (lbl_ErrorRepresentante != null) lbl_ErrorRepresentante.setVisible(false);
        if (lbl_ErrorDireccionFiscal != null) lbl_ErrorDireccionFiscal.setVisible(false);
    }
    
    /**
     * Mostrar un label de error específico
     */
    private void mostrarError(Label labelError) {
        if (labelError != null) {
            labelError.setVisible(true);
        }
    }

    /**
     * Validar formato del nombre (solo letras, espacios, ñ y vocales con tildes)
     */
    private boolean validarFormatoNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        
        String nombreTrimmed = nombre.trim();
        
        // Longitud mínima y máxima
        if (nombreTrimmed.length() < 2 || nombreTrimmed.length() > 100) {
            return false;
        }
        
        // Regex para caracteres válidos: letras, ñ/Ñ, vocales con tildes y espacios simples
        String regex = "^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ]+(?:\\s+[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ]+)*$";
        if (!nombreTrimmed.matches(regex)) {
            return false;
        }
        
        // No debe tener más de un espacio consecutivo
        if (nombreTrimmed.contains("  ")) {
            return false;
        }
        
        // No debe empezar ni terminar con espacio (después del trim)
        if (nombreTrimmed.startsWith(" ") || nombreTrimmed.endsWith(" ")) {
            return false;
        }
        
        return true;
    }

    /**
     * Construir objeto Cliente desde los datos del formulario
     */
    private Cliente construirClienteDesdeFormulario() {
        Cliente cliente = new Cliente();
        
        cliente.setNombreCompleto(txtf_Nombres.getText().trim());
        
        // Configurar tipo de identificación
        String tipoId = cbx_TipoIdentificacion.getValue();
        if (tipoId != null) {
            switch (tipoId) {
                case "Cédula":
                    cliente.setTipoIdentificacion(Cliente.TipoIdentificacion.CEDULA);
                    break;
                case "RUC":
                    cliente.setTipoIdentificacion(Cliente.TipoIdentificacion.RUC);
                    break;
                case "Pasaporte":
                    cliente.setTipoIdentificacion(Cliente.TipoIdentificacion.PASAPORTE);
                    break;
            }
        }
        
        // Configurar tipo de persona
        String tipoPersona = cbx_TipoCliente.getValue();
        if (tipoPersona != null) {
            if (tipoPersona.equals("Persona Natural")) {
                cliente.setTipoPersona(Cliente.TipoPersona.NATURAL);
                cliente.setEstadoCivil(cbx_EstadoCivil.getValue());
            } else {
                cliente.setTipoPersona(Cliente.TipoPersona.JURIDICA);
                cliente.setRepresentanteLegal(txtf_RepresentanteLegal.getText().trim());
                cliente.setDireccionFiscal(txtf_DireccionFiscal.getText().trim());
            }
        }
        
        cliente.setNumeroIdentificacion(txtf_NumeroIdentificacion.getText().trim());
        cliente.setDireccion(txtf_Direccion.getText().trim());
        cliente.setTelefono(txtf_Telefono.getText().trim());
        cliente.setCorreoElectronico(txtf_Correo.getText().trim());
        
        // Configurar estado
        String estado = cbx_Estado.getValue();
        cliente.setEstado(estado != null && estado.equals("Activo") ? Cliente.Estado.ACTIVO : Cliente.Estado.INACTIVO);
        
        cliente.setFechaRegistro(dt_FechaIngreso.getValue());
        
        return cliente;
    }

    /**
     * Cancelar operación
     */
    private void cancelarOperacion() {
        // Verificar si hay cambios pendientes
        if (hayCambiosPendientes()) {
            // Mostrar diálogo de confirmación
            Optional<ButtonType> resultado = DialogUtil.mostrarDialogo(
                "Confirmar Cancelación",
                "¿Está seguro que desea cancelar?\nSe perderán todos los cambios no guardados.",
                "warning",
                List.of(ButtonType.YES, ButtonType.NO)
            );
            
            if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
                procederCancelacion();
            }
        } else {
            procederCancelacion();
        }
    }
    
    /**
     * Verificar si hay cambios pendientes en el formulario
     */
    private boolean hayCambiosPendientes() {
        // En modo registrar, verificar si hay campos llenos
        if (modo == ModoFormulario.REGISTRAR) {
            return !txtf_Nombres.getText().trim().isEmpty() ||
                   !txtf_NumeroIdentificacion.getText().trim().isEmpty() ||
                   !txtf_Direccion.getText().trim().isEmpty() ||
                   !txtf_Telefono.getText().trim().isEmpty() ||
                   !txtf_Correo.getText().trim().isEmpty() ||
                   cbx_TipoCliente.getValue() != null ||
                   cbx_TipoIdentificacion.getValue() != null;
        }
        
        // En modo editar, verificar si hay cambios respecto al cliente actual
        if (modo == ModoFormulario.EDITAR && clienteActual != null) {
            return !txtf_Direccion.getText().trim().equals(clienteActual.getDireccion() != null ? clienteActual.getDireccion() : "") ||
                   !txtf_Telefono.getText().trim().equals(clienteActual.getTelefono() != null ? clienteActual.getTelefono() : "") ||
                   !txtf_Correo.getText().trim().equals(clienteActual.getCorreoElectronico() != null ? clienteActual.getCorreoElectronico() : "");
        }
        
        return false;
    }
    
    /**
     * Proceder con la cancelación
     */
    private void procederCancelacion() {
        if (modo == ModoFormulario.REGISTRAR) {
            limpiarFormulario();
        }
        
        // Ejecutar callback de cancelar (esto maneja el cierre del formulario)
        if (onCancelarCallback != null) {
            onCancelarCallback.run();
        }
    }

    /**
     * Limpiar formulario
     */
    private void limpiarFormulario() {
        txtf_Nombres.clear();
        cbx_TipoCliente.setValue(null);
        cbx_TipoIdentificacion.setValue(null);
        txtf_NumeroIdentificacion.clear();
        txtf_Direccion.clear();
        txtf_Telefono.clear();
        txtf_Correo.clear();
        cbx_Estado.setValue("Activo");
        dt_FechaIngreso.setValue(LocalDate.now());
        cbx_EstadoCivil.setValue(null);
        txtf_RepresentanteLegal.clear();
        txtf_DireccionFiscal.clear();
        
        mostrarCamposPersonaNatural(false);
        mostrarCamposPersonaJuridica(false);
    }

    /**
     * Habilitar/deshabilitar todos los campos
     */
    private void habilitarCampos(boolean habilitar) {
        txtf_Nombres.setDisable(!habilitar);
        cbx_TipoCliente.setDisable(!habilitar);
        cbx_TipoIdentificacion.setDisable(!habilitar);
        txtf_NumeroIdentificacion.setDisable(!habilitar);
        txtf_Direccion.setDisable(!habilitar);
        txtf_Telefono.setDisable(!habilitar);
        txtf_Correo.setDisable(!habilitar);
        cbx_Estado.setDisable(!habilitar);
        dt_FechaIngreso.setDisable(!habilitar);
        cbx_EstadoCivil.setDisable(!habilitar);
        txtf_RepresentanteLegal.setDisable(!habilitar);
        txtf_DireccionFiscal.setDisable(!habilitar);
    }

    /**
     * Habilitar solo campos editables
     */
    private void habilitarCamposEditables(boolean habilitar) {
        // Campos no editables
        txtf_Nombres.setDisable(true);
        cbx_TipoCliente.setDisable(true);
        cbx_TipoIdentificacion.setDisable(true);
        txtf_NumeroIdentificacion.setDisable(true);
        dt_FechaIngreso.setDisable(true);
        
        // Campos editables
        txtf_Direccion.setDisable(!habilitar);
        txtf_Telefono.setDisable(!habilitar);
        txtf_Correo.setDisable(!habilitar);
        cbx_Estado.setDisable(!habilitar);
        cbx_EstadoCivil.setDisable(!habilitar);
        txtf_RepresentanteLegal.setDisable(!habilitar);
        txtf_DireccionFiscal.setDisable(!habilitar);
    }

    /**
     * Mostrar/ocultar campos específicos de persona natural
     */
    private void mostrarCamposPersonaNatural(boolean mostrar) {
        if (vbox_PersonaNatural != null) {
            vbox_PersonaNatural.setVisible(mostrar);
            vbox_PersonaNatural.setManaged(mostrar);
        }
    }

    /**
     * Mostrar/ocultar campos específicos de persona jurídica
     */
    private void mostrarCamposPersonaJuridica(boolean mostrar) {
        if (vbox_PersonaJuridica != null) {
            vbox_PersonaJuridica.setVisible(mostrar);
            vbox_PersonaJuridica.setManaged(mostrar);
        }
    }

    /**
     * Métodos auxiliares para mostrar mensajes
     */
    private void mostrarMensajeInformacion(String titulo, String mensaje) {
        DialogUtil.mostrarDialogo(titulo, mensaje, "info", List.of(ButtonType.OK));
    }

    private void mostrarMensajeError(String titulo, String mensaje) {
        DialogUtil.mostrarDialogo(titulo, mensaje, "error", List.of(ButtonType.OK));
    }

    private void mostrarMensajeAdvertencia(String titulo, String mensaje) {
        DialogUtil.mostrarDialogo(titulo, mensaje, "warning", List.of(ButtonType.OK));
    }

    private boolean mostrarConfirmacion(String titulo, String mensaje) {
        Optional<ButtonType> resultado = DialogUtil.mostrarDialogo(titulo, mensaje, "confirm", List.of(ButtonType.YES, ButtonType.NO));
        return resultado.isPresent() && resultado.get() == ButtonType.YES;
    }

    /**
     * Configurar iconos en los botones
     */
    private void configurarIconosBotones() {
        try {
            // Icono para el botón Guardar
            configurarIconoBoton(btn_Guardar, "/icons/confirm.png", 16, 16);
            
            // Icono para el botón Cancelar  
            configurarIconoBoton(btn_Cancelar, "/icons/error.png", 16, 16);
            
        } catch (Exception e) {
            System.err.println("Error al cargar iconos: " + e.getMessage());
        }
    }

    /**
     * Configura un icono para un botón específico
     */
    private void configurarIconoBoton(Button boton, String rutaIcono, int ancho, int alto) {
        try {
            URL iconUrl = getClass().getResource(rutaIcono);
            if (iconUrl != null) {
                Image imagen = new Image(iconUrl.toString());
                ImageView imageView = new ImageView(imagen);
                imageView.setFitWidth(ancho);
                imageView.setFitHeight(alto);
                imageView.setPreserveRatio(true);
                boton.setGraphic(imageView);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar icono " + rutaIcono + ": " + e.getMessage());
        }
    }

    /**
     * Actualizar iconos según el modo del formulario
     */
    private void actualizarIconosPorModo() {
        switch (modo) {
            case REGISTRAR:
                configurarIconoBoton(btn_Guardar, "/icons/confirm.png", 16, 16);
                break;
            case EDITAR:
                configurarIconoBoton(btn_Guardar, "/icons/confirm.png", 16, 16);
                break;
            case VISUALIZAR:
                // En modo visualizar el botón guardar no es visible
                break;
        }
    }

    // Callbacks para comunicación con el controlador padre
    private Runnable onGuardarCallback;
    private Runnable onCancelarCallback;

    /**
     * Métodos de compatibilidad con ModuloClienteController
     */
    public void setModo(String modo) {
        switch (modo.toUpperCase()) {
            case "NUEVO":
            case "REGISTRAR":
                configurarModo(ModoFormulario.REGISTRAR);
                break;
            case "EDITAR":
                configurarModo(ModoFormulario.EDITAR);
                break;
            case "VER":
            case "VISUALIZAR":
                configurarModo(ModoFormulario.VISUALIZAR);
                break;
            default:
                configurarModo(ModoFormulario.REGISTRAR);
                break;
        }
    }

    public void setOnGuardar(Runnable onGuardar) {
        this.onGuardarCallback = onGuardar;
    }

    public void setOnCancelar(Runnable onCancelar) {
        this.onCancelarCallback = onCancelar;
    }
    
    /**
     * Aplica estilos programáticamente al ComboBox para asegurar la visibilidad del texto
     */
    private void aplicarEstilosComboBox(ComboBox<String> comboBox) {
        try {
            // Estilo directo para asegurar visibilidad del texto
            comboBox.setStyle(
                "-fx-font-size: 13px; " +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif; " +
                "-fx-text-fill: #1a202c; " +
                "-fx-background-color: white; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 4px; " +
                "-fx-background-radius: 4px; " +
                "-fx-padding: 8px 12px; " +
                "-fx-pref-height: 32px; " +
                "-fx-max-height: 32px;"
            );
            
            // Configurar el comportamiento del popup
            comboBox.setVisibleRowCount(5);
            
            // Listener para asegurar que el texto se vea cuando se seleccione
            comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    System.out.println("ComboBox seleccionado: " + newVal);
                    // Forzar la actualización visual usando setText
                    if (comboBox.getButtonCell() != null) {
                        comboBox.getButtonCell().setText(newVal);
                    }
                }
            });
            
            System.out.println("Estilos aplicados a ComboBox: " + comboBox.getId());
        } catch (Exception e) {
            System.err.println("Error al aplicar estilos a ComboBox: " + e.getMessage());
        }
    }
}
