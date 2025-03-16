package com.example.proyectodas_1;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PreferenciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferencias);

        String usuario = getIntent().getStringExtra("usuario");
        OperacionesBD operacionesBD = new OperacionesBD(this, 1);
        String[] preferencias = operacionesBD.obtenerPreferencias(usuario);
        String idiomaSelec = preferencias[0];

        cambiarIdioma(idiomaSelec);


        // Inicializar los RadioGroups y botones
        RadioGroup radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
        RadioGroup radioGroupOrden = findViewById(R.id.radioGroupOrden);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Configurar el listener del botón "Guardar"
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el idioma seleccionado
                int selectedIdiomaId = radioGroupIdioma.getCheckedRadioButtonId();
                String idiomaSeleccionado = "";

                if (selectedIdiomaId == R.id.radioIngles) {
                    idiomaSeleccionado = "Inglés";
                } else if (selectedIdiomaId == R.id.radioEuskera) {
                    idiomaSeleccionado = "Euskera";
                } else if (selectedIdiomaId == R.id.radioCastellano) {
                    idiomaSeleccionado = "Castellano";
                }

                // Obtener el orden seleccionado
                int selectedOrdenId = radioGroupOrden.getCheckedRadioButtonId();
                String ordenSeleccionado = "";

                if (selectedOrdenId == R.id.radioPrecioAsc) {
                    ordenSeleccionado = "Precio ascendente";
                } else if (selectedOrdenId == R.id.radioPrecioDesc) {
                    ordenSeleccionado = "Precio descendente";
                } else if (selectedOrdenId == R.id.radioTituloAsc) {
                    ordenSeleccionado = "Título ascendente";
                } else if (selectedOrdenId == R.id.radioTituloDesc) {
                    ordenSeleccionado = "Título descendente";
                }

                // Obtener el usuario (si es necesario)

                OperacionesBD operacionesBD = new OperacionesBD(PreferenciasActivity.this, 1);
                operacionesBD.modificarPreferencias(usuario, idiomaSeleccionado, ordenSeleccionado);

                // Mostrar un mensaje de éxito
                Toast.makeText(PreferenciasActivity.this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        // Configurar el listener del botón "Salir"
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad
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

        Locale.setDefault(nuevaLocale);
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(nuevaLocale);
        res.updateConfiguration(conf, res.getDisplayMetrics());

        // Actualizar los textos de la interfaz
        actualizarTextos();
    }

    private void actualizarTextos() {

        TextView txtTitulo = findViewById(R.id.txtTitulo);
        TextView txtIdioma = findViewById(R.id.txtIdioma);
        TextView txtOrden = findViewById(R.id.txtOrden);
        RadioButton radioIngles = findViewById(R.id.radioIngles);
        RadioButton radioEuskera = findViewById(R.id.radioEuskera);
        RadioButton radioCastellano = findViewById(R.id.radioCastellano);
        RadioButton radioPrecioAsc = findViewById(R.id.radioPrecioAsc);
        RadioButton radioPrecioDesc = findViewById(R.id.radioPrecioDesc);
        RadioButton radioTituloAsc = findViewById(R.id.radioTituloAsc);
        RadioButton radioTituloDesc = findViewById(R.id.radioTituloDesc);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnSalir = findViewById(R.id.btnSalir);

        txtTitulo.setText(R.string.preferences_title);
        txtIdioma.setText(R.string.select_language);
        txtOrden.setText(R.string.sort_books_by);

        radioIngles.setText(R.string.english);
        radioEuskera.setText(R.string.basque);
        radioCastellano.setText(R.string.spanish);

        radioPrecioAsc.setText(R.string.price_ascending);
        radioPrecioDesc.setText(R.string.price_descending);
        radioTituloAsc.setText(R.string.title_ascending);
        radioTituloDesc.setText(R.string.title_descending);

        btnGuardar.setText(R.string.save);
        btnSalir.setText(R.string.exit);

    }

}
