package com.example.proyectodas_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {
    private OperacionesBD operacionesBD;
    private String idiomaSelec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        operacionesBD = new OperacionesBD(this, 1);

        // Referencia al Spinner
        Spinner spinnerIdiomas = findViewById(R.id.spinner_categorias);

        // Obtener la lista de idiomas desde strings.xml
        String[] idiomas = getResources().getStringArray(R.array.idiomas_array);

        // Crear un ArrayAdapter con los idiomas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, idiomas);

        // Asignar el adapter al Spinner
        spinnerIdiomas.setAdapter(adapter);

        spinnerIdiomas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                idiomaSelec = idiomas[position];
                cambiarIdioma(idiomaSelec);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // No hacer nada si no se selecciona nada
            }
        });

        inicioSesion();
        iniciarAnonimo();
        iniciarUsuario();
    }

    private void iniciarAnonimo() {
        Button buttonContinuar = findViewById(R.id.principalAnonimo);
        buttonContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            intent.putExtra("usuario", "Anonimo");
            startActivity(intent);
        });
    }

    private void iniciarUsuario() {
        Button buttonContinuar = findViewById(R.id.principalUsuario);
        buttonContinuar.setOnClickListener(v -> {
            EditText cajaUsu = findViewById(R.id.cajaUsu);
            EditText cajaPassword = findViewById(R.id.cajaPassword);

            String usuario = cajaUsu.getText().toString().trim();
            String password = cajaPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                if (idiomaSelec.equals("Inglés")) {
                    mostrarError("Please enter username and password.");
                } else if (idiomaSelec.equals("Euskera")) {
                    mostrarError("Mesedez, sartu erabiltzailea eta pasahitza.");
                } else {
                    mostrarError("Por favor, ingrese usuario y contraseña.");
                }
                return;
            }

            if (operacionesBD.comprobarUsuario(usuario)) {
                 if (idiomaSelec.equals("Inglés")) {
                    mostrarError("The user already exists in the system.");
                } else if (idiomaSelec.equals("Euskera")) {
                    mostrarError("Erabiltzailea badago erregistratuta sisteman.");
                } else {
                     mostrarError("El usuario ya existe dentro del sistema.");
                 }
                return;
            }

            operacionesBD.insertarUsuario(usuario, password);

            Intent intent = new Intent(this, PrincipalActivity.class);
            intent.putExtra("usuario", usuario); // Pasar el usuario a PrincipalActivity
            startActivity(intent);
        });
    }

    private void mostrarError(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    private void cambiarIdioma(String idioma) {
        Locale nuevaLocale;

        switch (idioma) {
            case "Inglés":
                nuevaLocale = new Locale("en");
                break;
            case "Euskera":
                nuevaLocale = new Locale("eu");
                break;
            default:
                nuevaLocale = new Locale("es");
                break;
        }

        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(nuevaLocale);
        res.updateConfiguration(conf, res.getDisplayMetrics());

        actualizarTextos();
    }

    private void actualizarTextos() {
        TextView tituloPrincipal = findViewById(R.id.tituloPrincipal);
        TextView textViewRegistro = findViewById(R.id.textView);
        TextView textUsuario = findViewById(R.id.textUsuario);
        TextView textContrasena = findViewById(R.id.textContrasena);
        EditText cajaUsu = findViewById(R.id.cajaUsu);
        EditText cajaPassword = findViewById(R.id.cajaPassword);
        Button btnRegistrar = findViewById(R.id.principalUsuario);
        Button btnVolverLogin = findViewById(R.id.iniciarSesion);
        Button btnAnonimo = findViewById(R.id.principalAnonimo);

        // Actualización de textos
        tituloPrincipal.setText(R.string.welcome_message);
        textViewRegistro.setText(R.string.register_button); // Este puede ser "Registrarse"
        textUsuario.setText(R.string.new_user); // Puede ser "Nuevo usuario"
        textContrasena.setText(R.string.new_password); // Puede ser "Nueva contraseña"
        cajaUsu.setHint(R.string.username); // Puede ser "Nombre de usuario"
        cajaPassword.setHint(R.string.password); // Puede ser "Contraseña"
        btnRegistrar.setText(R.string.register_button); // Puede ser "Registrarse"
        btnVolverLogin.setText(R.string.back_to_login); // Puede ser "Volver inicio sesión"
        btnAnonimo.setText(R.string.continue_without_registering); // Puede ser "Continuar sin registrarse"
    }


    // Método para manejar el clic en el botón "Registro" que va a RegistroActivity
    private void inicioSesion() {
        Button buttonRegistro = findViewById(R.id.iniciarSesion);
        buttonRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
