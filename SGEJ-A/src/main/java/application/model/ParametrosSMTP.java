package application.model;

/**
 * Clase que representa los parámetros de configuración SMTP para envío de
 * correos
 */
public class ParametrosSMTP {
    private String servidor;
    private int puerto;
    private String usuario;
    private String clave;
    private String remitente;

    public ParametrosSMTP(String servidor, int puerto, String usuario, String clave, String remitente) {
        this.servidor = servidor;
        this.puerto = puerto;
        this.usuario = usuario;
        this.clave = clave;
        this.remitente = remitente;
    }

    public String getServidor() {
        return servidor;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getClave() {
        return clave;
    }

    public String getRemitente() {
        return remitente;
    }

    /**
     * Verifica si la configuración SMTP es válida (contiene todos los campos
     * obligatorios)
     * 
     * @return true si la configuración es válida, false en caso contrario
     */
    public boolean esValida() {
        return servidor != null && !servidor.trim().isEmpty() &&
                puerto > 0 &&
                usuario != null && !usuario.trim().isEmpty() &&
                clave != null && !clave.trim().isEmpty() &&
                remitente != null && !remitente.trim().isEmpty();
    }

    /**
     * Devuelve una representación en texto de los parámetros SMTP (sin la clave por
     * seguridad)
     * 
     * @return String con los detalles de la configuración
     */
    @Override
    public String toString() {
        return "ParametrosSMTP{" +
                "servidor='" + servidor + '\'' +
                ", puerto=" + puerto +
                ", usuario='" + usuario + '\'' +
                ", clave='" + (clave != null && !clave.isEmpty() ? "[PROTEGIDA]" : "NO CONFIGURADA") + '\'' +
                ", remitente='" + remitente + '\'' +
                '}';
    }
}
