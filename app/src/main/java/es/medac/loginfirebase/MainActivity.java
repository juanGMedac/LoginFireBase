package es.medac.loginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Importamos la clase de enlace generada automáticamente por DataBinding
import es.medac.loginfirebase.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // 1. Variables globales
    private ActivityMainBinding binding; // Para acceder a los elementos visuales
    private FirebaseAuth mAuth;        // Para gestionar la autenticación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Configurar DataBinding (Sustituye al setContentView clásico)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 3. Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 4. Configurar el botón de REGISTRO
        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (validarCampos(email, password)) {
                registrarUsuario(email, password);
            }
        });

        // 5. Configurar el botón de LOGIN
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (validarCampos(email, password)) {
                iniciarSesion(email, password);
            }
        });
    }

    // --- MÉTODOS DE FIREBASE ---

    private void registrarUsuario(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si el registro funciona, entramos directamente
                        irAHome();
                    } else {
                        // Si falla (ej: contraseña corta, email ya existe)
                        String error = task.getException().getMessage();
                        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login correcto
                        irAHome();
                    } else {
                        // Login incorrecto
                        Toast.makeText(this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- MÉTODOS AUXILIARES ---

    // Comprobamos si el usuario ya estaba logueado al abrir la app
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            irAHome();
        }
    }

    // Navegación a la pantalla principal
    private void irAHome() {
        // IMPORTANTE: Asegúrate de crear HomeActivity (ver paso siguiente)
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // Cerramos el Login para que no se pueda volver atrás con el botón 'Back'
    }

    // Validación básica de campos vacíos
    private boolean validarCampos(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}