package application.utils;

public class RucValidator {

    public static boolean validarRuc(String ruc) {
        if (ruc == null || ruc.length() != 13 || !ruc.endsWith("001"))
            return false;

        char tercerDigito = ruc.charAt(2);

        if (tercerDigito >= '0' && tercerDigito <= '5') {
            // Persona Natural
            String cedula = ruc.substring(0, 10);
            return validarCedula(cedula);
        } else if (tercerDigito == '6') {
            return validarRucSociedadPublica(ruc);
        } else if (tercerDigito == '9') {
            return validarRucSociedadPrivada(ruc);
        }

        return false;
    }

    // Validación de cédula (para persona natural)
    private static boolean validarCedula(String cedula) {
        if (cedula == null || cedula.length() != 10)
            return false;

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (provincia < 1 || provincia > 24 || tercerDigito > 5)
            return false;

        int[] coeficientes = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int val = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            if (val >= 10)
                val -= 9;
            suma += val;
        }

        int digitoVerificador = (10 - (suma % 10)) % 10;
        return digitoVerificador == Character.getNumericValue(cedula.charAt(9));
    }

    // Validación para sociedades privadas (tercer dígito = 9)
    private static boolean validarRucSociedadPrivada(String ruc) {
        String base = ruc.substring(0, 10);
        int[] coef = { 4, 3, 2, 7, 6, 5, 4, 3, 2 };
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            suma += Character.getNumericValue(base.charAt(i)) * coef[i];
        }

        int modulo = suma % 11;
        int verificador = (modulo == 0) ? 0 : 11 - modulo;

        return verificador == Character.getNumericValue(base.charAt(9));
    }

    // Validación para sociedades públicas (tercer dígito = 6)
    private static boolean validarRucSociedadPublica(String ruc) {
        String base = ruc.substring(0, 9); // El dígito verificador está en la posición 8
        int[] coef = { 3, 2, 7, 6, 5, 4, 3, 2 };
        int suma = 0;

        for (int i = 0; i < 8; i++) {
            suma += Character.getNumericValue(base.charAt(i)) * coef[i];
        }

        int modulo = suma % 11;
        int verificador = (modulo == 0) ? 0 : 11 - modulo;

        return verificador == Character.getNumericValue(ruc.charAt(8));
    }
}
