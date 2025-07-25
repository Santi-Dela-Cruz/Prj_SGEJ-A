package application.utils;

public class VerificationID {

    public boolean verificarTamano(String identificacion) {
        return identificacion != null && identificacion.matches("\\d{10}");
    }

    public boolean validarPrimerosDigitos(String identificacion) {
        int provincia = Integer.parseInt(identificacion.substring(0, 2));
        int tercerDigito = Character.getNumericValue(identificacion.charAt(2));
        return provincia >= 1 && provincia <= 24 && tercerDigito < 6;
    }

    public boolean validadDigitoVerificador(String identificacion) {
        int[] coeficientes = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int valor = Character.getNumericValue(identificacion.charAt(i)) * coeficientes[i];
            if (valor >= 10)
                valor -= 9;
            suma += valor;
        }

        int digitoVerificador = (10 - (suma % 10)) % 10;
        return digitoVerificador == Character.getNumericValue(identificacion.charAt(9));
    }

    /**
     * Validación completa de cédula ecuatoriana
     */
    public boolean validarCedula(String cedula) {
        return verificarTamano(cedula) &&
                validarPrimerosDigitos(cedula) &&
                validadDigitoVerificador(cedula);
    }
}
