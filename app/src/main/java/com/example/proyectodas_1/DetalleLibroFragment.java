package com.example.proyectodas_1;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class DetalleLibroFragment extends Fragment {

    private TextView titulo, autor, genero, resumen, precio;
    private ImageView portada;
    private String isbn;
    private final String USUARIO;
    private OperacionesBD db;

    private Button btnSalir;

    public DetalleLibroFragment(String usuario) {
        this.USUARIO = usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.libro_detalles, container, false);

        view.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Inicializar las vistas
        titulo = view.findViewById(R.id.titulo);
        autor = view.findViewById(R.id.autor);
        genero = view.findViewById(R.id.genero);
        resumen = view.findViewById(R.id.resumen);
        precio = view.findViewById(R.id.precio);
        portada = view.findViewById(R.id.portada);
        btnSalir = view.findViewById(R.id.btn_salir);

        // Obtener el ISBN del libro desde el argumento
        if (getArguments() != null) {
            isbn = getArguments().getString("isbn");
        }

        // Inicializar la base de datos
        db = new OperacionesBD(getContext(), 1);

        String[] preferencias = db.obtenerPreferencias(USUARIO);
        String idiomaSelec = preferencias[0];

        cambiarIdioma(idiomaSelec);

        // Cargar los detalles del libro
        cargarDetallesLibro();

        btnSalir.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar el fragmento actual
                //getFragmentManager().beginTransaction().remove(DetalleLibroFragment.this).commit();
                getParentFragmentManager().popBackStack();

            }
        });

        return view;
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
        // Actualizar los textos de los elementos de la interfaz
        btnSalir.setText(R.string.exit);
    }

    private void cargarDetallesLibro() {
        // Recuperar el libro de la base de datos
        Libro libro = db.obtenerLibroPorIsbn(isbn);

        if (libro != null) {
            // Mostrar los detalles en las vistas
            titulo.setText(libro.getTitulo());
            autor.setText(libro.getAutor());
            genero.setText(libro.getGenero());
            resumen.setText(libro.getResumen());
            precio.setText(String.format("%.2f€", libro.getPrecio()));

            // Mostrar la portada si existe
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                int imageResId = 0;
                try {
                    Class<?> res = R.drawable.class;
                    java.lang.reflect.Field field = res.getField(libro.getPortada());
                    imageResId = field.getInt(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (imageResId != 0) {
                    portada.setImageResource(imageResId); // Establece la imagen
                } else {
                    // Si no se encuentra la imagen, puedes poner una imagen por defecto
                    portada.setImageResource(R.drawable.por_defecto); // Asegúrate de tener una imagen por defecto
                }
            }
            else {
                portada.setImageResource(R.drawable.por_defecto);
            }

        }
    }
}


