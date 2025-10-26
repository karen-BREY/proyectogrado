import org.junit.Assert
import org.junit.Test

// --- Pruebas para UserValidator ---

// Objeto a probar: Contiene la lógica para validar datos de un usuario.
object UserValidator {

    /**
     * Valida si un formato de email es estructuralmente correcto.
     * No comprueba si el email realmente existe.
     * @param email El email a validar.
     * @return `true` si el formato es válido, `false` en caso contrario.
     */
    fun esEmailValido(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            return false
        }
        // Utiliza una expresión regular simple para verificar el formato "algo@algo.algo"
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida si una contraseña cumple con los requisitos de seguridad.
     * Requisitos: Mínimo 6 caracteres.
     * @param contrasena La contraseña a validar.
     * @return `true` si la contraseña es válida, `false` en caso contrario.
     */
    fun esContrasenaValida(contrasena: String?): Boolean {
        // Para este ejemplo, la única regla es que no sea nula y tenga al menos 6 caracteres.
        return !contrasena.isNullOrEmpty() && contrasena.length >= 6
    }
}

// --- Clase de Prueba ---
// (Estas pruebas se añaden dentro de tu clase `ExampleUnitTest`)
class UserValidatorTests { // Puedes poner estas pruebas dentro de ExampleUnitTest o en su propia clase

    // --- Pruebas para esEmailValido ---
    @Test
    fun `esEmailValido_devuelve_true_para_email_correcto`() {
        Assert.assertTrue(UserValidator.esEmailValido("kreye6@udi.edu.co"))
    }

    @Test
    fun `esEmailValido_devuelve_false_para_email_sin_arroba`() {
        Assert.assertFalse(UserValidator.esEmailValido("usuariodominio.com"))
    }

    @Test
    fun `esEmailValido_devuelve_false_para_email_sin_dominio`() {
        Assert.assertFalse(UserValidator.esEmailValido("usuario@.com"))
    }

    @Test
    fun `esEmailValido_devuelve_false_para_email_nulo_o_vacio`() {
        Assert.assertFalse("Un email nulo debería ser inválido", UserValidator.esEmailValido(null))
        Assert.assertFalse("Un email vacío debería ser inválido", UserValidator.esEmailValido(""))
        Assert.assertFalse("Un email en blanco debería ser inválido", UserValidator.esEmailValido("   "))
    }

    // --- Pruebas para esContrasenaValida ---
    @Test
    fun `esContrasenaValida_devuelve_true_para_contrasena_valida`() {
        Assert.assertTrue(UserValidator.esContrasenaValida("123456"))
    }

    @Test
    fun `esContrasenaValida_devuelve_false_para_contrasena_muy_corta`() {
        Assert.assertFalse(UserValidator.esContrasenaValida("12345"))
    }

    @Test
    fun `esContrasenaValida_devuelve_false_para_contrasena_nula_o_vacia`() {
        Assert.assertFalse("Una contraseña nula debería ser inválida", UserValidator.esContrasenaValida(null))
        Assert.assertFalse("Una contraseña vacía debería ser inválida", UserValidator.esContrasenaValida(""))
    }
}




