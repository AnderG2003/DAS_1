package com.example.proyectodas_1;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CambiarPasswordActivity extends AppCompatActivity {

    private OperacionesBD db;
    private EditText passwordAnt, passwordAct;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_password);
        db = new OperacionesBD(this, 1);

        usuario = getIntent().getStringExtra("usuario");

        String[] preferencias = db.obtenerPreferencias(usuario);
        String idiomaSelec = preferencias[0];

        cambiarIdioma(idiomaSelec);

        Button btnCambiar = findViewById(R.id.btn_cambiar_contrasenia);
        Button btnSalir = findViewById(R.id.btn_salir);

        passwordAnt = findViewById(R.id.et_contrasenia_antigua);
        passwordAct = findViewById(R.id.et_nueva_contrasenia);

        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String antiguaPassword = passwordAnt.getText().toString();
                String nuevaPassword = passwordAct.getText().toString();

                if (db.comprobarUsuario(usuario, antiguaPassword)) {
                    db.modificarPassword(usuario, nuevaPassword);
                    mostrarMensaje("Contraseña cambiada exitosamente");
                } else {
                    mostrarAlerta("Contraseña incorrecta");
                }
            }
        });


        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        // Referencias a los elementos de la interfaz
        TextView tvTitulo = findViewById(R.id.tv_titulo);
        TextView tvContraseniaAntigua = findViewById(R.id.tv_contrasenia_antigua);
        EditText etContraseniaAntigua = findViewById(R.id.et_contrasenia_antigua);
        TextView tvNuevaContrasenia = findViewById(R.id.tv_nueva_contrasenia);
        EditText etNuevaContrasenia = findViewById(R.id.et_nueva_contrasenia);
        Button btnCambiarContrasenia = findViewById(R.id.btn_cambiar_contrasenia);
        Button btnSalir = findViewById(R.id.btn_salir);

        // Asignar los textos traducidos
        tvTitulo.setText(R.string.change_password); // Cambiar contraseña
        tvContraseniaAntigua.setText(R.string.prev_password); // Última contraseña
        etContraseniaAntigua.setHint(R.string.prev_password); // Hint para el campo de última contraseña
        tvNuevaContrasenia.setText(R.string.new_password); // Nueva contraseña
        etNuevaContrasenia.setHint(R.string.new_password); // Hint para el campo de nueva contraseña
        btnCambiarContrasenia.setText(R.string.change_password); // Cambiar contraseña
        btnSalir.setText(R.string.exit); // Salir
    }

    private void mostrarAlerta(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    private void mostrarMensaje(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Éxito")
                .setMessage(mensaje)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

}
