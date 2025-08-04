package application.util;

/**
 * Clase para gestionar el control de intentos de login fallidos
 * Utiliza el patrón Singleton para asegurar una única instancia
 */
public class LoginFailureManager {
    private static LoginFailureManager instance;

    // Mapa para contar intentos fallidos por usuario
    private java.util.Map<String, Integer> intentosFallidos;

    private LoginFailureManager() {
        intentosFallidos = new java.util.HashMap<>();
    }

    public static LoginFailureManager getInstance() {
        if (instance == null) {
            instance = new LoginFailureManager();
        }
        return instance;
    }

    /**
     * Registra un intento fallido de login para un usuario
     * 
     * @param nombreUsuario Nombre del usuario que falló el login
     * @return Número actual de intentos fallidos
     */
    public synchronized int registrarIntentoFallido(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return 0;
        }

        int intentos = intentosFallidos.getOrDefault(nombreUsuario, 0) + 1;
        intentosFallidos.put(nombreUsuario, intentos);
        return intentos;
    }

    /**
     * Reinicia el contador de intentos fallidos para un usuario
     * 
     * @param nombreUsuario Nombre del usuario a reiniciar
     */
    public synchronized void reiniciarIntentosFallidos(String nombreUsuario) {
        if (nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
            intentosFallidos.remove(nombreUsuario);
        }
    }

    /**
     * Obtiene el número actual de intentos fallidos para un usuario
     * 
     * @param nombreUsuario Nombre del usuario
     * @return Número de intentos fallidos
     */
    public synchronized int getIntentosFallidos(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return 0;
        }
        return intentosFallidos.getOrDefault(nombreUsuario, 0);
    }
}
