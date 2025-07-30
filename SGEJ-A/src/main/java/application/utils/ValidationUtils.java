package application.utils;

import javafx.scene.control.TextField;

import java.util.regex.Pattern;

/**
 * Clase de utilidades para validación de datos
 */
public class ValidationUtils {

    /**
     * Expresión regular para validar correos electrónicos
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Configura validación para un campo requerido, marcándolo en rojo si está
     * vacío
     * 
     * @param field        Campo a validar
     * @param errorMessage Mensaje de error a mostrar
     */
    public static void setupRequiredFieldValidation(TextField field, String errorMessage) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // cuando pierde el foco
                if (field.getText() == null || field.getText().trim().isEmpty()) {
                    field.setStyle("-fx-border-color: red;");
                    // Opcionalmente agregar un tooltip
                } else {
                    field.setStyle("");
                }
            }
        });
    }

    /**
     * Valida si una cadena es un correo electrónico válido
     * 
     * @param email Correo a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida si una cédula ecuatoriana es válida utilizando el algoritmo de
     * verificación
     * 
     * @param cedula Cédula a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidCedula(String cedula) {
        // Una cédula ecuatoriana tiene 10 dígitos
        if (cedula == null || cedula.length() != 10) {
            return false;
        }

        // Verificar que solo contenga dígitos
        try {
            Long.parseLong(cedula);
        } catch (NumberFormatException e) {
            return false;
        }

        // Extraer los dígitos
        int[] coeficientes = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
        int verificador = Character.getNumericValue(cedula.charAt(9));
        int suma = 0;

        // Algoritmo de validación de cédula ecuatoriana
        for (int i = 0; i < coeficientes.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            suma += (valor >= 10) ? valor - 9 : valor;
        }

        int digitoVerificador = (suma % 10 == 0) ? 0 : 10 - (suma % 10);

        return verificador == digitoVerificador;
    }

    /**
     * Valida si un RUC ecuatoriano es válido
     * 
     * @param ruc RUC a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidRuc(String ruc) {
        // Un RUC tiene 13 dígitos
        if (ruc == null || ruc.length() != 13) {
            return false;
        }

        // Verificar que solo contenga dígitos
        try {
            Long.parseLong(ruc);
        } catch (NumberFormatException e) {
            return false;
        }

        // Para personas naturales, los primeros 10 dígitos son la cédula
        // y los últimos 3 dígitos deben ser 001
        String cedula = ruc.substring(0, 10);
        String sufijo = ruc.substring(10);

        // Verificar que el sufijo sea 001
        if (!sufijo.equals("001")) {
            return false;
        }

        // Validar la parte de la cédula
        return isValidCedula(cedula);
    }

    /**
     * Verifica si una cadena solo contiene letras y espacios
     * 
     * @param texto Texto a verificar
     * @return true si solo contiene letras y espacios, false en caso contrario
     */
    public static boolean soloLetrasYEspacios(String texto) {
        return texto != null && texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]*$");
    }

    /**
     * Verifica si una cadena solo contiene dígitos
     * 
     * @param texto Texto a verificar
     * @return true si solo contiene dígitos, false en caso contrario
     */
    public static boolean soloDigitos(String texto) {
        return texto != null && texto.matches("^\\d*$");
    }
}
