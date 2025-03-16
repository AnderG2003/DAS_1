package com.example.proyectodas_1;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class NuevoLibroActivity extends AppCompatActivity {

    private EditText editISBN, editTitulo, editAutor, editGenero, editResumen, editPrecio;
    private OperacionesBD db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_libro); // Asegúrate de que el nombre del XML sea correcto


        // Referencias a los elementos del layout
        editISBN = findViewById(R.id.editISBN);
        editTitulo = findViewById(R.id.editTitulo);
        editAutor = findViewById(R.id.editAutor);
        editGenero = findViewById(R.id.editGenero);
        editResumen = findViewById(R.id.editResumen);
        editPrecio = findViewById(R.id.editPrecio);
        Button btnAnadir = findViewById(R.id.btnAnadir);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Inicializar la base de datos
        db = new OperacionesBD(this, 1);

        String usuario = getIntent().getStringExtra("usuario");

        String[] preferencias = db.obtenerPreferencias(usuario);
        String idiomaSelec = preferencias[0];

        cambiarIdioma(idiomaSelec);

        // Botón Salir
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finalizar la actividad
                finish();
            }
        });

        // Botón Añadir
        btnAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos
                String isbn = editISBN.getText().toString().trim();
                String titulo = editTitulo.getText().toString().trim();
                String autor = editAutor.getText().toString().trim();
                String genero = editGenero.getText().toString().trim();
                String resumen = editResumen.getText().toString().trim();
                String precioStr = editPrecio.getText().toString().trim();

                // Validar que todos los campos estén llenos
                if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty() || genero.isEmpty() || resumen.isEmpty() || precioStr.isEmpty()) {
                    Toast.makeText(NuevoLibroActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertir el precio a double
                double precio;
                try {
                    precio = Double.parseDouble(precioStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(NuevoLibroActivity.this, "El precio debe ser un número válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar si el ISBN ya existe
                if (db.buscarLibro(isbn)) {
                    Toast.makeText(NuevoLibroActivity.this, "El ISBN ya existe. Introduce un ISBN diferente.", Toast.LENGTH_SHORT).show();
                } else {
                    // Insertar el libro en la base de datos
                    db.insertarLibro(isbn, titulo, autor, genero, "", resumen, precio);
                    Toast.makeText(NuevoLibroActivity.this, "Libro añadido correctamente.", Toast.LENGTH_SHORT).show();

                    // Limpiar los campos después de añadir el libro
                    editISBN.setText("");
                    editTitulo.setText("");
                    editAutor.setText("");
                    editGenero.setText("");
                    editResumen.setText("");
                    editPrecio.setText("");
                }
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
        TextView tituloAnadirLibro = findViewById(R.id.tituloAnadirLibro);
        TextView textTitulo = findViewById(R.id.textTitulo);
        TextView textAutor = findViewById(R.id.textAutor);
        TextView textGenero = findViewById(R.id.textGenero);
        TextView textResumen = findViewById(R.id.textResumen);
        TextView textPrecio = findViewById(R.id.textPrecio);
        Button btnAnadir = findViewById(R.id.btnAnadir);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Actualizar los textos de los elementos de la interfaz
        tituloAnadirLibro.setText(R.string.add_book); // "Añadir libro" en el idioma seleccionado
        textTitulo.setText(R.string.title); // "Título" en el idioma seleccionado
        textAutor.setText(R.string.author); // "Autor" en el idioma seleccionado
        textGenero.setText(R.string.genre); // "Género" en el idioma seleccionado
        textResumen.setText(R.string.summary); // "Resumen" en el idioma seleccionado
        textPrecio.setText(R.string.price); // "Precio" en el idioma seleccionado
        btnAnadir.setText(R.string.add); // "Añadir" en el idioma seleccionado
        btnSalir.setText(R.string.exit); // "Salir" en el idioma seleccionado

        // Actualizar los hints de los EditText
        editISBN.setHint(R.string.enter_isbn); // "Introduce el ISBN" en el idioma seleccionado
        editTitulo.setHint(R.string.enter_title); // "Introduce el título" en el idioma seleccionado
        editAutor.setHint(R.string.enter_author); // "Introduce el autor" en el idioma seleccionado
        editGenero.setHint(R.string.enter_genre); // "Introduce el género" en el idioma seleccionado
        editResumen.setHint(R.string.enter_summary); // "Introduce el resumen" en el idioma seleccionado
        editPrecio.setHint(R.string.enter_price); // "Introduce el precio" en el idioma seleccionado
    }
}