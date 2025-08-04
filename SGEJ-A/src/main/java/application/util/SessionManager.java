package application.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Optional;

/**
 * Clase para gestionar el tiempo de sesión de usuario
 * Controla la expiración de la sesión y el cierre automático
 */
public class SessionManager {
    private static SessionManager instance;

    private Timer sessionTimer;
    private long sessionDurationMillis;
    private Runnable logoutAction;
    private boolean sessionActive = false;
    private Stage mainStage;

    private SessionManager() {
        sessionTimer = null;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Inicia el control de tiempo de sesión
     * 
     * @param durationMinutes Duración de la sesión en minutos
     * @param logoutAction    Acción a ejecutar cuando expire la sesión
     * @param stage           Ventana principal de la aplicación
     */
    public synchronized void startSessionTimer(int durationMinutes, Runnable logoutAction, Stage stage) {
        // Cancelar cualquier timer existente
        stopSessionTimer();

        // Asegurar que el tiempo de sesión sea válido (mínimo 1 minuto)
        int safeDurationMinutes = Math.max(1, durationMinutes);
        this.sessionDurationMillis = safeDurationMinutes * 60 * 1000L;
        this.logoutAction = logoutAction;
        this.mainStage = stage;
        this.sessionActive = true;

        System.out.println("Iniciando temporizador de sesión: " + safeDurationMinutes + " minutos ("
                + sessionDurationMillis + " ms)");

        // Crear nuevo timer
        sessionTimer = new Timer();
        sessionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sessionActive) {
                    Platform.runLater(() -> showSessionExpiredDialog());
                }
            }
        }, sessionDurationMillis);
    }

    /**
     * Detiene el control de tiempo de sesión
     */
    public synchronized void stopSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
            sessionTimer = null;
        }
        sessionActive = false;
    }

    /**
     * Reinicia el tiempo de sesión (por ejemplo, cuando hay actividad del usuario)
     */
    public synchronized void resetSessionTimer() {
        if (sessionActive && logoutAction != null) {
            stopSessionTimer();
            // Calcular duración en minutos, con un mínimo de 1 minuto
            int durationMinutes = Math.max(1, (int) (sessionDurationMillis / (60 * 1000)));
            System.out.println("Reiniciando temporizador de sesión: " + durationMinutes + " minutos");
            startSessionTimer(durationMinutes, logoutAction, mainStage);
        }
    }

    /**
     * Muestra un diálogo informando que la sesión ha expirado
     */
    private void showSessionExpiredDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sesión Expirada");
        alert.setHeaderText("Su tiempo de sesión ha expirado");
        alert.setContentText("Por razones de seguridad, su sesión ha finalizado. Por favor, inicie sesión nuevamente.");

        if (mainStage != null) {
            alert.initOwner(mainStage);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            stopSessionTimer();
            if (logoutAction != null) {
                logoutAction.run();
            }
        }
    }

    /**
     * Verifica si la sesión está activa
     * 
     * @return true si la sesión está activa
     */
    public boolean isSessionActive() {
        return sessionActive;
    }
}
