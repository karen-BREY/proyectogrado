import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.proyecto_grado.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun flujoCompleto_loginYnavegacionARegistroAnimal() {

        composeTestRule.onNodeWithTag("email_field").performTextInput("kreyes6@udi.edu.co")
        composeTestRule.onNodeWithTag("password_field").performTextInput("karen10")
        composeTestRule.onNodeWithTag("login_button").performClick()


        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText("Bienvenido").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Bienvenido").assertIsDisplayed()


        composeTestRule.onNodeWithText("Registrar Animal").performClick()

        composeTestRule.onNodeWithTag("pantalla_registro_animal").assertIsDisplayed()
    }


    @Test
    fun desdeLogin_navegaCorrectamenteAOlvidoContrasena() {
        // ETAPA 1: VERIFICAR QUE ESTAMOS EN LA PANTALLA DE LOGIN
        composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()

        // ETAPA 2: REALIZAR LA ACCIÓN
        composeTestRule.onNodeWithText("¿Olvidaste tu contraseña?").performClick()

        // ETAPA 3: VERIFICAR EL RESULTADO
        // Asegúrate de que el texto "Recuperar Contraseña" exista en tu pantalla de destino.
        val textoEnPantallaDestino = "Recuperar Contraseña"
        composeTestRule.onNodeWithText(textoEnPantallaDestino).assertIsDisplayed()
    }

    @Test
    fun login_fallaConContrasenaIncorrecta_yMuestraError() {

        composeTestRule.onNodeWithTag("email_field").performTextInput("kreyes6@udi.edu.co")
        composeTestRule.onNodeWithTag("password_field").performTextInput("contrasenaincorrecta")

        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.onNodeWithText("Bienvenido").assertDoesNotExist()

        composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()


    }
    @Test
    fun flujoCompleto_registroYRedireccion() {

        composeTestRule.onNodeWithText("Regístrate").performClick()


        composeTestRule.onNodeWithText("Crear cuenta").assertIsDisplayed()

        val emailAleatorio = "testuser_${System.currentTimeMillis()}@test.com"

        // Asumimos que los campos de registro tienen estos testTags. ¡Ajústalos si es necesario!
        composeTestRule.onNodeWithTag("email_registro_field").performTextInput(emailAleatorio)
        composeTestRule.onNodeWithTag("password_registro_field").performTextInput("password123")

        // Hacemos clic en el botón para finalizar el registro.
        composeTestRule.onNodeWithTag("register_button").performClick()

        // --- ETAPA 3: VERIFICAR LA REDIRECCIÓN ---
        // Después de un registro exitoso, la app debería navegar a la pantalla principal.
        // Esperamos a que aparezca el texto "Bienvenido", lo que confirma el éxito.
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText("Bienvenido").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Bienvenido").assertIsDisplayed()
    }


}




