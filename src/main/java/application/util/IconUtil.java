package application.util;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 * Clase utilitaria para manejar iconos en la aplicación
 */
public class IconUtil {
    
    // Rutas de los iconos disponibles
    public static final String ICONO_CONFIRMAR = "/icons/confirm.png";
    public static final String ICONO_ERROR = "/icons/error.png";
    public static final String ICONO_INFO = "/icons/info.png";
    public static final String ICONO_WARNING = "/icons/warning.png";
    public static final String ICONO_LOGO = "/icons/firmLogo.jpg";
    
    // Tamaños estándar
    public static final int TAMAÑO_PEQUEÑO = 16;
    public static final int TAMAÑO_MEDIANO = 24;
    public static final int TAMAÑO_GRANDE = 32;
    
    /**
     * Configura un icono para un botón
     * @param boton El botón al que se le asignará el icono
     * @param rutaIcono La ruta del icono
     * @param ancho Ancho del icono
     * @param alto Alto del icono
     * @return true si se configuró correctamente, false en caso contrario
     */
    public static boolean configurarIconoBoton(Button boton, String rutaIcono, int ancho, int alto) {
        try {
            URL iconUrl = IconUtil.class.getResource(rutaIcono);
            if (iconUrl != null) {
                Image imagen = new Image(iconUrl.toString());
                ImageView imageView = new ImageView(imagen);
                imageView.setFitWidth(ancho);
                imageView.setFitHeight(alto);
                imageView.setPreserveRatio(true);
                boton.setGraphic(imageView);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar icono " + rutaIcono + ": " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Configura un icono para un botón con tamaño pequeño (16x16)
     * @param boton El botón al que se le asignará el icono
     * @param rutaIcono La ruta del icono
     * @return true si se configuró correctamente, false en caso contrario
     */
    public static boolean configurarIconoBotonPequeño(Button boton, String rutaIcono) {
        return configurarIconoBoton(boton, rutaIcono, TAMAÑO_PEQUEÑO, TAMAÑO_PEQUEÑO);
    }
    
    /**
     * Configura un icono para un botón con tamaño mediano (24x24)
     * @param boton El botón al que se le asignará el icono
     * @param rutaIcono La ruta del icono
     * @return true si se configuró correctamente, false en caso contrario
     */
    public static boolean configurarIconoBotonMediano(Button boton, String rutaIcono) {
        return configurarIconoBoton(boton, rutaIcono, TAMAÑO_MEDIANO, TAMAÑO_MEDIANO);
    }
    
    /**
     * Crea un ImageView con el icono especificado
     * @param rutaIcono La ruta del icono
     * @param ancho Ancho del icono
     * @param alto Alto del icono
     * @return ImageView con el icono o null si no se pudo cargar
     */
    public static ImageView crearImageView(String rutaIcono, int ancho, int alto) {
        try {
            URL iconUrl = IconUtil.class.getResource(rutaIcono);
            if (iconUrl != null) {
                Image imagen = new Image(iconUrl.toString());
                ImageView imageView = new ImageView(imagen);
                imageView.setFitWidth(ancho);
                imageView.setFitHeight(alto);
                imageView.setPreserveRatio(true);
                return imageView;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar icono " + rutaIcono + ": " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Configura iconos estándar para botones comunes
     */
    public static void configurarIconosEstandar(Button btnGuardar, Button btnCancelar) {
        configurarIconoBotonPequeño(btnGuardar, ICONO_CONFIRMAR);
        configurarIconoBotonPequeño(btnCancelar, ICONO_ERROR);
    }
    
    /**
     * Configura iconos para botones de acción CRUD
     */
    public static void configurarIconosAccion(Button btnNuevo, Button btnEditar, Button btnEliminar) {
        configurarIconoBotonPequeño(btnNuevo, ICONO_CONFIRMAR);
        configurarIconoBotonPequeño(btnEditar, ICONO_INFO);
        configurarIconoBotonPequeño(btnEliminar, ICONO_ERROR);
    }
    
    /**
     * Verifica si un icono existe
     * @param rutaIcono La ruta del icono a verificar
     * @return true si el icono existe, false en caso contrario
     */
    public static boolean iconoExiste(String rutaIcono) {
        try {
            URL iconUrl = IconUtil.class.getResource(rutaIcono);
            return iconUrl != null;
        } catch (Exception e) {
            return false;
        }
    }
}
