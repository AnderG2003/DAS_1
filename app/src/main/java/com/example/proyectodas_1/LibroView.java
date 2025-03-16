package com.example.proyectodas_1;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class LibroView extends RecyclerView.Adapter<LibroView.LibroViewHolder> {
    private final ArrayList<Libro> LIBROS;
    private final String USUARIO; // Agregamos el usuario
    private final boolean MOSTRAR_BOTONES;


    public LibroView(ArrayList<Libro> libros, String usuario, boolean mostrarBotones) {
        this.LIBROS = libros;
        this.USUARIO = usuario; // Guardamos el usuario
        this.MOSTRAR_BOTONES = mostrarBotones;
    }

    @Override
    public LibroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout libro.xml con CardView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.libro, parent, false);
        return new LibroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LibroViewHolder holder, int position) {
        Libro libro = LIBROS.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
        holder.precio.setText(String.format("%.2f€", libro.getPrecio()));

        OperacionesBD db = new OperacionesBD(holder.itemView.getContext(), 1);

        String[] preferencias = db.obtenerPreferencias(USUARIO);
        String idiomaSelec = preferencias[0];

        cambiarIdioma(holder,idiomaSelec);

        boolean enLeidos = db.estaEnListaLeidos(USUARIO, libro.getIsbn());
        boolean enQuieroLeer = db.estaEnListaQuieroLeer(USUARIO, libro.getIsbn());


        holder.iconoLeido.setVisibility((enLeidos) ? View.VISIBLE : View.GONE);
        holder.iconoQuieroLeer.setVisibility((enQuieroLeer) ? View.VISIBLE : View.GONE);

        if (!MOSTRAR_BOTONES) {
            holder.btnLeido.setVisibility(View.GONE);
            holder.btnQuieroLeer.setVisibility(View.GONE);
        } else {
            holder.btnLeido.setVisibility(View.VISIBLE);
            holder.btnQuieroLeer.setVisibility(View.VISIBLE);
        }

        // Set the book cover image
        if (libro.getPortada() == null || libro.getPortada().isEmpty()) {
            // If the cover is empty or null, set the default image
            holder.portada.setImageResource(R.drawable.por_defecto);
        } else {
            // Try to load the cover image from resources
            int imageResId = 0;
            try {
                Class<?> res = R.drawable.class;
                java.lang.reflect.Field field = res.getField(libro.getPortada());
                imageResId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (imageResId != 0) {
                holder.portada.setImageResource(imageResId); // Set the image
            } else {
                // If the image is not found, set the default image
                holder.portada.setImageResource(R.drawable.por_defecto);
            }
        }

        // Botón "Leído"
        holder.btnLeido.setOnClickListener(v -> {
            if (enLeidos) {
                db.eliminarDeListaLeidos(USUARIO, libro.getIsbn());
                holder.iconoLeido.setVisibility(View.GONE);
                mostrarNotificacion(v.getContext(), "Se eliminó de la lista de leídos");
            } else {
                db.agregarAListaLeidos(USUARIO, libro.getIsbn());
                holder.iconoLeido.setVisibility(View.VISIBLE);
                mostrarNotificacion(v.getContext(), "Se agregó a la lista de leídos");
            }
            notifyItemChanged(position);
        });

        // Botón "Quiero Leer"
        holder.btnQuieroLeer.setOnClickListener(v -> {
            if (enQuieroLeer) {
                db.eliminarDeListaQuieroLeer(USUARIO, libro.getIsbn());
                holder.iconoQuieroLeer.setVisibility(View.GONE);
                mostrarNotificacion(v.getContext(), "Se eliminó de la lista de 'Quiero Leer'");
            } else {
                db.agregarAListaQuieroLeer(USUARIO, libro.getIsbn());
                holder.iconoQuieroLeer.setVisibility(View.VISIBLE);
                mostrarNotificacion(v.getContext(), "Se agregó a la lista de 'Quiero Leer'");
            }
            notifyItemChanged(position);
        });

        if (MOSTRAR_BOTONES) {
            holder.portada.setOnClickListener(v -> {
                // Crear un nuevo fragmento de detalles
                DetalleLibroFragment detalleFragment = new DetalleLibroFragment(USUARIO);

                // Pasar el ISBN del libro como argumento
                Bundle args = new Bundle();
                args.putString("isbn", libro.getIsbn());
                detalleFragment.setArguments(args);

                // Reemplazar el fragmento en la actividad contenedora
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detalleFragment) // Asegúrate de que R.id.fragment_container existe en tu actividad
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    public void actualizarListaLibros(ArrayList<Libro> nuevosLibros) {
        this.LIBROS.clear(); // Clear the old data
        this.LIBROS.addAll(nuevosLibros); // Add the new data
        notifyDataSetChanged(); // Notify the adapter of the changes
    }

    private void cambiarIdioma(LibroViewHolder holder, String idioma) {
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
        Resources res = holder.itemView.getContext().getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(nuevaLocale);
        res.updateConfiguration(conf, res.getDisplayMetrics());

        // Actualizar los textos de la interfaz
        actualizarTextos(holder);

    }

    private void actualizarTextos(LibroViewHolder holder) {
        // Actualizar los textos de los botones
        holder.btnLeido.setText(R.string.read); // "Leído" en el idioma seleccionado
        holder.btnQuieroLeer.setText(R.string.want_to_read); // "Quiero Leer" en el idioma seleccionado

    }

    @Override
    public int getItemCount() {
        return LIBROS.size();
    }

    private void mostrarNotificacion(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }


    public static class LibroViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView autor;
        TextView precio;
        ImageView portada;
        ImageView iconoLeido;
        ImageView iconoQuieroLeer;
        Button btnLeido;
        Button btnQuieroLeer;

        public LibroViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            autor = itemView.findViewById(R.id.autor);
            precio = itemView.findViewById(R.id.precio);
            portada = itemView.findViewById(R.id.portada);
            iconoLeido = itemView.findViewById(R.id.icono_leido);
            iconoQuieroLeer = itemView.findViewById(R.id.icono_quiero_leer);
            btnLeido = itemView.findViewById(R.id.btn_leido);
            btnQuieroLeer = itemView.findViewById(R.id.btn_quiero_leer);

        }

    }

}