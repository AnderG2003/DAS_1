package com.example.proyectodas_1;

import android.content.Intent;
import android.content.SharedPreferences;
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


public class MainActivity extends AppCompatActivity {

    private OperacionesBD db;
    private String idiomaSelec = "es";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new OperacionesBD(this, 1);

        cambiarIdioma(idiomaSelec);

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

        this.iniciarAnonimo();
        this.registro();
        this.iniciarUsuario();
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

        // Save the selected language in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idioma", idioma);
        editor.apply();

        // Update the app's locale
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(nuevaLocale);
        res.updateConfiguration(conf, res.getDisplayMetrics());

        actualizarTextos();
    }

    private void actualizarTextos() {
        TextView tituloPrincipal = findViewById(R.id.tituloPrincipal);
        TextView textUsuario = findViewById(R.id.textUsuario);
        TextView textContrasena = findViewById(R.id.textContrasena);
        EditText editUsuario = findViewById(R.id.editUsuario);
        EditText editContrasena = findViewById(R.id.editContrasena);
        Button btnIniciarSesion = findViewById(R.id.principalUsuario);
        Button btnRegistro = findViewById(R.id.registro);
        Button btnAnonimo = findViewById(R.id.principalAnonimo);
        TextView textViewLogin = findViewById(R.id.textView);

        tituloPrincipal.setText(R.string.welcome_message);
        textUsuario.setText(R.string.username);
        textContrasena.setText(R.string.password);
        editUsuario.setHint(R.string.username);
        editContrasena.setHint(R.string.password);
        btnIniciarSesion.setText(R.string.login_button);
        btnRegistro.setText(R.string.register_button);
        btnAnonimo.setText(R.string.continue_button);
        textViewLogin.setText(R.string.login);

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
            EditText cajaUsu = findViewById(R.id.editUsuario);
            EditText cajaPassword = findViewById(R.id.editContrasena);

            String usuario = cajaUsu.getText().toString().trim();
            String password = cajaPassword.getText().toString().trim();

            String mensajeUsuarioVacio;
            String mensajeCredencialesInvalidas;

            if (idiomaSelec.equals("Inglés")) {
                mensajeUsuarioVacio = "Please enter a username and password or a valid user.";
                mensajeCredencialesInvalidas = "The username or password is invalid.";
            } else if (idiomaSelec.equals("Euskera")) {
                mensajeUsuarioVacio = "Mesedez, sartu erabiltzaile-izena eta pasahitza edo erabiltzaile baliodun bat.";
                mensajeCredencialesInvalidas = "Erabiltzaile-izena edo pasahitza ez da zuzena.";
            } else { // Castellano (por defecto)
                mensajeUsuarioVacio = "Por favor, ingrese usuario y contraseña o un usuario válido.";
                mensajeCredencialesInvalidas = "El usuario o la contraseña no son válidos.";
            }

            if (usuario.isEmpty() || password.isEmpty() || usuario.equals("Anonimo")) {
                mostrarError(mensajeUsuarioVacio);
                return;
            }

            if (db.comprobarUsuario(usuario, password)) {
                // Si el usuario existe, pasar a PrincipalActivity y enviar el nombre de usuario
                Intent intent = new Intent(this, PrincipalActivity.class);
                intent.putExtra("usuario", usuario); // Pasar el usuario a PrincipalActivity
                startActivity(intent);
            } else {
                // Si el usuario no existe, mostrar un mensaje de error
                mostrarError(mensajeCredencialesInvalidas);
            }
        });
    }

    private void mostrarError(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }


    // Método para manejar el clic en el botón "Registro" que va a RegistroActivity
    private void registro() {
        Button buttonRegistro = findViewById(R.id.registro);
        buttonRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
            finish();
        });
    }

}