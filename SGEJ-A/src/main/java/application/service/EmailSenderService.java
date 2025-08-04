package application.service;

import application.model.ParametrosSMTP;
import javax.mail.*;
import javax.mail.internet.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para enviar correos electrónicos con adjuntos
 */
public class EmailSenderService {

    private static final Logger LOGGER = Logger.getLogger(EmailSenderService.class.getName());
    private static final int TIMEOUT = 10000; // 10 segundos de timeout

    /**
     * Envía un correo electrónico con un archivo adjunto
     * 
     * @param destinatario Correo del destinatario
     * @param asunto       Asunto del correo
     * @param mensaje      Cuerpo del mensaje
     * @param rutaAdjunto  Ruta del archivo a adjuntar
     * @param smtp         Configuración SMTP del servidor de correo
     * @return true si el correo se envió correctamente, false en caso contrario
     */
    public static boolean enviarCorreoConAdjunto(
            String destinatario,
            String asunto,
            String mensaje,
            String rutaAdjunto,
            ParametrosSMTP smtp) {

        // Validar parámetros SMTP antes de intentar la conexión
        if (!smtp.esValida()) {
            LOGGER.severe("La configuración SMTP no es válida, faltan parámetros obligatorios");
            LOGGER.severe("Configuración SMTP: " + smtp.toString());

            if (smtp.getUsuario() == null || smtp.getUsuario().trim().isEmpty()) {
                LOGGER.severe("El usuario SMTP está vacío");
            }
            if (smtp.getClave() == null || smtp.getClave().trim().isEmpty()) {
                LOGGER.severe("La clave SMTP está vacía");
            } else {
                LOGGER.info("La clave SMTP tiene " + smtp.getClave().length() + " caracteres");
            }
            return false;
        }

        // Verificar la conectividad antes de intentar enviar
        if (!verificarConectividad(smtp.getServidor())) {
            LOGGER.severe("No hay conectividad con el servidor SMTP: " + smtp.getServidor());
            return false;
        }

        LOGGER.info("Iniciando envío de correo a: " + destinatario);
        LOGGER.info("Usando servidor SMTP: " + smtp.getServidor() + ":" + smtp.getPuerto());
        LOGGER.info("Usuario SMTP: " + smtp.getUsuario());
        LOGGER.info("Remitente: " + smtp.getRemitente());

        Properties props = new Properties();
        props.put("mail.smtp.host", smtp.getServidor());
        props.put("mail.smtp.port", String.valueOf(smtp.getPuerto()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Si es Gmail, usar configuración específica para Gmail
        boolean isGmail = smtp.getServidor().contains("gmail.com");
        if (isGmail) {
            LOGGER.info("Detectado servidor Gmail, aplicando configuración específica");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.enable", "false");
        }

        // Añadir timeouts
        props.put("mail.smtp.connectiontimeout", String.valueOf(TIMEOUT));
        props.put("mail.smtp.timeout", String.valueOf(TIMEOUT));
        props.put("mail.smtp.writetimeout", String.valueOf(TIMEOUT));

        // Para servidores que requieren SSL/TLS
        props.put("mail.smtp.ssl.trust", smtp.getServidor());
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Mostrar información de la autenticación antes de crear la sesión
        LOGGER.info("Creando sesión con usuario: " + smtp.getUsuario());
        if (smtp.getClave() == null || smtp.getClave().isEmpty()) {
            LOGGER.severe("ALERTA: La contraseña está vacía!");
        } else {
            LOGGER.info("La contraseña tiene " + smtp.getClave().length() + " caracteres");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (smtp.getUsuario() == null || smtp.getClave() == null) {
                    LOGGER.severe("Usuario o clave nulos en autenticación!");
                    return new PasswordAuthentication("", "");
                }
                LOGGER.info("Intentando autenticar con usuario: " + smtp.getUsuario());
                return new PasswordAuthentication(smtp.getUsuario(), smtp.getClave());
            }
        });

        // Habilitamos el modo debug para obtener más información
        session.setDebug(true);

        try {
            Message correo = new MimeMessage(session);
            correo.setFrom(new InternetAddress(smtp.getRemitente()));
            correo.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            correo.setSubject(asunto);

            // Crear parte para el texto del mensaje
            MimeBodyPart cuerpoTexto = new MimeBodyPart();
            cuerpoTexto.setText(mensaje);

            // Crear parte para el archivo adjunto
            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(new File(rutaAdjunto));

            // Combinar partes en un multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpoTexto);
            multipart.addBodyPart(adjunto);

            // Establecer contenido del mensaje
            correo.setContent(multipart);

            // Enviar el mensaje
            Transport.send(correo);
            LOGGER.info("Correo enviado correctamente a: " + destinatario);
            return true;

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error en la mensajería al enviar correo: " + e.getMessage(), e);
            if (e.getCause() != null) {
                LOGGER.log(Level.SEVERE, "Causa: " + e.getCause().getMessage());
            }
            return false;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error de IO al adjuntar archivo: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al enviar correo: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica si hay conectividad con el servidor SMTP
     * 
     * @param servidor Nombre del servidor a verificar
     * @return true si hay conectividad, false en caso contrario
     */
    private static boolean verificarConectividad(String servidor) {
        try {
            LOGGER.info("Verificando conectividad con: " + servidor);

            // Primero verificamos la conectividad a Internet general
            try {
                InetAddress googleDNS = InetAddress.getByName("8.8.8.8");
                boolean internetAccesible = googleDNS.isReachable(5000);
                LOGGER.info("Conexión a Internet: " + (internetAccesible ? "DISPONIBLE" : "NO DISPONIBLE"));

                if (!internetAccesible) {
                    LOGGER.severe("No hay conexión a Internet. Verifique su red.");
                    return false;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error al verificar conexión a Internet", e);
                // Continuamos con la verificación específica del servidor
            }

            // Luego verificamos si el servidor SMTP es accesible
            InetAddress address = InetAddress.getByName(servidor);
            LOGGER.info("Resolviendo DNS de " + servidor + " -> " + address.getHostAddress());

            // Para Gmail, es mejor probar la conectividad con un puerto específico
            boolean isGmail = servidor.contains("gmail.com");

            if (isGmail) {
                LOGGER.info("Detectado servidor Gmail, realizando verificación específica");

                try {
                    // Intentamos conectarnos al puerto 587 (SMTP con STARTTLS)
                    java.net.Socket socket = new java.net.Socket();
                    socket.connect(new java.net.InetSocketAddress(servidor, 587), 5000);
                    boolean connected = socket.isConnected();
                    socket.close();
                    LOGGER.info("Conexión al puerto 587 de " + servidor + ": " + (connected ? "ÉXITO" : "FALLÓ"));
                    return connected;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "No se pudo conectar al puerto 587 de " + servidor, e);
                    return false;
                }
            } else {
                // Para otros servidores, hacemos ping estándar
                boolean reachable = address.isReachable(TIMEOUT);
                LOGGER.info("Servidor " + servidor + " es " + (reachable ? "accesible" : "inaccesible"));
                return reachable;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al verificar conectividad con " + servidor + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtiene un mensaje descriptivo del problema de envío de correo
     * 
     * @param e La excepción que ocurrió durante el envío
     * @return Un mensaje descriptivo del error
     */
    public static String obtenerMensajeError(Exception e) {
        if (e instanceof AuthenticationFailedException) {
            return "Error de autenticación: usuario o contraseña incorrectos para el servidor SMTP";
        } else if (e instanceof SendFailedException) {
            return "Error al enviar: dirección de correo inválida o servidor rechazó el mensaje";
        } else if (e instanceof MessagingException) {
            if (e.getCause() instanceof java.net.ConnectException) {
                return "No se pudo conectar al servidor SMTP. Verifique su conexión a Internet y los datos del servidor";
            } else if (e.getCause() instanceof java.net.UnknownHostException) {
                return "No se pudo encontrar el servidor SMTP. Verifique el nombre del servidor";
            } else {
                return "Error de comunicación con el servidor SMTP: " + e.getMessage();
            }
        } else {
            return "Error al enviar correo: " + e.getMessage();
        }
    }
}
