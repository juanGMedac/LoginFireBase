package es.medac.loginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Buscamos los elementos visuales
        Button btnLogout = findViewById(R.id.btnLogout);
        TextView tvWelcome = findViewById(R.id.tvWelcome);

        // 2. Mostramos el email del usuario actual si existe
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            tvWelcome.setText("Hola,\n" + email);
        }

        // 3. Lógica del botón CERRAR SESIÓN
        btnLogout.setOnClickListener(v -> {
            // Desconectar de Firebase
            FirebaseAuth.getInstance().signOut();

            // Volver a la pantalla de Login
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cerramos esta actividad para limpiar la pila
        });
    }
}