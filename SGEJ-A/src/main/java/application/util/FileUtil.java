package application.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidades para manejo de archivos
 */
public class FileUtil {

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    private static final String DIRECTORIO_BASE = "uploads";
    private static final String DIRECTORIO_TEMP = DIRECTORIO_BASE + File.separator + "temp";
    private static final String DIRECTORIO_FACTURAS = DIRECTORIO_BASE + File.separator + "facturas";

    /**
     * Obtiene la ruta al directorio temporal para archivos generados
     * 
     * @return Ruta al directorio temporal (con separador al final)
     */
    public static String getDirectorioTemporal() {
        String userDir = System.getProperty("user.dir");
        String dirPath = userDir + File.separator + DIRECTORIO_TEMP;

        // Crear el directorio si no existe
        try {
            Files.createDirectories(Paths.get(dirPath));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear directorio temporal", e);
        }

        return dirPath + File.separator;
    }

    /**
     * Obtiene la ruta al directorio de facturas
     * 
     * @return Ruta al directorio de facturas (con separador al final)
     */
    public static String getDirectorioFacturas() {
        String userDir = System.getProperty("user.dir");
        String dirPath = userDir + File.separator + DIRECTORIO_FACTURAS;

        // Crear el directorio si no existe
        try {
            Files.createDirectories(Paths.get(dirPath));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear directorio de facturas", e);
        }

        return dirPath + File.separator;
    }

    /**
     * Verifica si un archivo existe
     * 
     * @param ruta Ruta completa al archivo
     * @return true si existe, false si no
     */
    public static boolean existeArchivo(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return false;
        }

        File archivo = new File(ruta);
        return archivo.exists() && archivo.isFile();
    }

    /**
     * Elimina un archivo temporal
     * 
     * @param ruta Ruta completa al archivo
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public static boolean eliminarArchivo(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return false;
        }

        File archivo = new File(ruta);
        if (archivo.exists() && archivo.isFile()) {
            return archivo.delete();
        }

        return false;
    }
}
