package com.example.proyectodas_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class PrincipalActivity extends AppCompatActivity {

    private OperacionesBD db;
    private LibroView libroView; // Usar el adaptador actualizado
    private String usuario, idiomaSelec, ordLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        db = new OperacionesBD(this, 1);

        usuario = getIntent().getStringExtra("usuario");

        RecyclerView recyclerViewLibros = findViewById(R.id.recyclerViewLibros);
        recyclerViewLibros.setLayoutManager(new LinearLayoutManager(this));

        String[] preferencias = db.obtenerPreferencias(usuario);
        idiomaSelec = preferencias[0];
        ordLib = preferencias[1];

        ArrayList<Libro> listaLibros = db.obtenerTodosLosLibros(ordLib);
        libroView = new LibroView(listaLibros, usuario, true);
        recyclerViewLibros.setAdapter(libroView);

        Button verLeidos = findViewById(R.id.btn_ver_leidos);
        Button verQuieroLeer = findViewById(R.id.btn_ver_quiero_leer);
        Button cambiarPassword = findViewById(R.id.btn_cambiar_password);
        ImageButton btnSalir = findViewById(R.id.btn_salir);
        Button anadirLibro = findViewById(R.id.btn_anadir_libro);
        Button btnPreferencias = findViewById(R.id.btn_preferencias);

        verLeidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, LeidosActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        verQuieroLeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, QuieroLeerActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        cambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("anonimo".equalsIgnoreCase(usuario)) {
                    new AlertDialog.Builder(PrincipalActivity.this)
                            .setTitle("Acceso denegado")
                            .setMessage("No puedes cambiar la contraseña si eres un usuario anónimo.")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Intent intent = new Intent(PrincipalActivity.this, CambiarPasswordActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                }
            }
        });

        anadirLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, NuevoLibroActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSalir();
            }
        });

        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, PreferenciasActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the adapter with the new data

        String[] preferencias = db.obtenerPreferencias(usuario);
        idiomaSelec = preferencias[0];
        ordLib = preferencias[1];

        cambiarIdioma(idiomaSelec);
        ArrayList<Libro> listaLibros = db.obtenerTodosLosLibros(ordLib);
        libroView.actualizarListaLibros(listaLibros);


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
        // Referencias a los elementos de la interfaz
        TextView textoBienvenida = findViewById(R.id.textoBienvenida);
        Button btnVerLeidos = findViewById(R.id.btn_ver_leidos);
        Button btnVerQuieroLeer = findViewById(R.id.btn_ver_quiero_leer);
        ImageButton btnSalir = findViewById(R.id.btn_salir);
        Button btnCambiarPassword = findViewById(R.id.btn_cambiar_password);
        Button btnPreferencias = findViewById(R.id.btn_preferencias);
        Button btnAnadirLibro = findViewById(R.id.btn_anadir_libro);

        textoBienvenida.setText(R.string.welcome_message);
        btnVerLeidos.setText(R.string.see_read);
        btnVerQuieroLeer.setText(R.string.see_want_to_read);
        btnSalir.setContentDescription(getString(R.string.exit));
        btnCambiarPassword.setText(R.string.new_password);
        btnPreferencias.setText(R.string.preferences);
        btnAnadirLibro.setText(R.string.add_book);
    }

    private void mostrarDialogoSalir() {
        String mensaje = "";
        String botonSi = "";
        String botonNo = "";

        // Ajustar el mensaje y los botones según el idioma seleccionado
        switch (idiomaSelec) {
            case "Inglés": // Inglés
                mensaje = "Are you sure you want to exit?";
                botonSi = "Yes";
                botonNo = "No";
                break;
            case "Euskera": // Euskera
                mensaje = "Ziur zaude irten nahi duzula?";
                botonSi = "Bai";
                botonNo = "Ez";
                break;
            default: // Si el idioma no está configurado, se pone en español por defecto
                mensaje = "¿Seguro que quieres salir?";
                botonSi = "Sí";
                botonNo = "No";
                break;
        }

        // Crear el AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(mensaje)
                .setCancelable(false) // No se puede cancelar tocando fuera del dialogo
                .setPositiveButton(botonSi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Si pulsa Sí, destruir la actividad
                        finish();
                    }
                })
                .setNegativeButton(botonNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Si pulsa No, simplemente cerrar el dialogo
                        dialog.dismiss();
                    }
                })
                .create(); // Crear el dialogo

        // Mostrar el dialogo
        dialog.show();

        // Ahora acceder a los botones después de que el AlertDialog se haya mostrado
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Cambiar el color de los botones
        positiveButton.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        negativeButton.setTextColor(getResources().getColor(android.R.color.holo_red_light));
    }


}
