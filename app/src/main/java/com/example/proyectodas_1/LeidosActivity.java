package com.example.proyectodas_1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

public class LeidosActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "DescargaLibros";
    private ArrayList<Libro> listaLibros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_libros_leidos);

        String usuario = getIntent().getStringExtra("usuario");

        OperacionesBD db = new OperacionesBD(this, 1);

        String[] preferencias = db.obtenerPreferencias(usuario);
        String idiomaSelec = preferencias[0];

        cambiarIdioma(idiomaSelec);

        crearCanalNotificacion();

        RecyclerView recyclerView = findViewById(R.id.recyclerLeidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        listaLibros = db.listaLeidos(usuario);
        // El tercer parámetro 'false' indica que no se mostrarán los botones de "Leído" y "Quiero Leer"
        LibroView adapter = new LibroView(listaLibros, usuario, false);

        recyclerView.setAdapter(adapter);

        Button btnDescargar = findViewById(R.id.btnDescargar);
        btnDescargar.setOnClickListener(v -> descargarLibros());

        // Botón Salir
        Button btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(v -> finish());

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
        // Referencias a los elementos de la interfaz
        Button btnDescargar = findViewById(R.id.btnDescargar);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Actualizar los textos de los botones
        btnDescargar.setText(R.string.download); // "Descargar" en el idioma seleccionado
        btnSalir.setText(R.string.exit); // "Salir" en el idioma seleccionado

    }

    private void descargarLibros() {
        if (listaLibros == null || listaLibros.isEmpty()) {
            Toast.makeText(this, "No hay libros para descargar", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "libros_leidos.txt";
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues);

        if (uri == null) {
            mostrarNotificacion("Error al crear el archivo", false);
            return;
        }

        try (OutputStream outputStream = resolver.openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {

            for (Libro libro : listaLibros) {
                String line = "ISBN: " + libro.getIsbn() + "\n" +
                        "Título: " + libro.getTitulo() + "\n" +
                        "Autor: " + libro.getAutor() + "\n" +
                        "Género: " + libro.getGenero() + "\n\n";
                writer.write(line);
            }

            writer.flush();
            //Toast.makeText(this, "Archivo guardado en Descargas", Toast.LENGTH_LONG).show();
            mostrarNotificacion("Archivo guardado en Descargas", true);

        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
            mostrarNotificacion("Error al guardar el archivo", false);

        }
    }
    private void mostrarNotificacion(String mensaje, boolean exito) {
        int icono = exito ? android.R.drawable.stat_sys_download_done : android.R.drawable.stat_notify_error;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(icono)
                .setContentTitle("Descarga de libros")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Solo pedir permiso en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= 33) { // 33 = Android 13 (Tiramisu)
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 101);
                return;
            }
        }

        notificationManager.notify(1, builder.build());
    }

    private void crearCanalNotificacion() {
        CharSequence nombre = "Canal Descargas";
        String descripcion = "Notificaciones sobre descargas de libros";
        int importancia = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, nombre, importancia);
        channel.setDescription(descripcion);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
