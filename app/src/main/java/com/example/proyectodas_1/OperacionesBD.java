package com.example.proyectodas_1;

import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import androidx.annotation.Nullable;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OperacionesBD extends BD {

    public OperacionesBD(@Nullable Context context, int version) {
        super(context, version); // 1 es la versión de la base de datos
    }

    public ArrayList<Libro> obtenerTodosLosLibros(String ordenSeleccionado) {
        ArrayList<Libro> listaLibros = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM LIBROS", null);

        if (cursor.moveToFirst()) {
            do {
                String isbn = cursor.getString(0);
                String titulo = cursor.getString(1);
                String autor = cursor.getString(2);
                String genero = cursor.getString(3);
                String portada = cursor.getString(4); // Puede ser null
                String resumen = cursor.getString(5);
                double precio = cursor.getDouble(6);

                listaLibros.add(new Libro(isbn, titulo, autor, genero, portada, resumen, precio));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Verificar si orderLib está vacío
        if (ordenSeleccionado == null || ordenSeleccionado.isEmpty()) {
            return listaLibros;
        }

        // Ordenar la lista según el parámetro ordenSeleccionado
        switch (ordenSeleccionado) {
            case "Precio ascendente":
                Collections.sort(listaLibros, new Comparator<Libro>() {
                    @Override
                    public int compare(Libro libro1, Libro libro2) {
                        return Double.compare(libro1.getPrecio(), libro2.getPrecio());
                    }
                });
                break;
            case "Precio descendente":
                Collections.sort(listaLibros, new Comparator<Libro>() {
                    @Override
                    public int compare(Libro libro1, Libro libro2) {
                        return Double.compare(libro2.getPrecio(), libro1.getPrecio());
                    }
                });
                break;
            case "Título ascendente":
                Collections.sort(listaLibros, new Comparator<Libro>() {
                    @Override
                    public int compare(Libro libro1, Libro libro2) {
                        return libro1.getTitulo().compareToIgnoreCase(libro2.getTitulo());
                    }
                });
                break;
            case "Título descendente":
                Collections.sort(listaLibros, new Comparator<Libro>() {
                    @Override
                    public int compare(Libro libro1, Libro libro2) {
                        return libro2.getTitulo().compareToIgnoreCase(libro1.getTitulo());
                    }
                });
                break;
            default:
                // No hacer nada si el orden no es reconocido
                break;
        }

        return listaLibros;
    }

    public Libro obtenerLibroPorIsbn(String isbn) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("LIBROS", null, "isbn=?", new String[]{isbn}, null, null, null);

        String titulo = null;
        String autor = null;
        String genero = null;
        String portada = null;
        String resumen = null;
        float precio = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int indexTitulo = cursor.getColumnIndex("titulo");
            int indexAutor = cursor.getColumnIndex("autor");
            int indexGenero = cursor.getColumnIndex("genero");
            int indexPortada = cursor.getColumnIndex("portada");
            int indexResumen = cursor.getColumnIndex("resumen");
            int indexPrecio = cursor.getColumnIndex("precio");

            if (indexTitulo != -1) {
                titulo = cursor.getString(indexTitulo);
            }
            if (indexAutor != -1) {
                autor = cursor.getString(indexAutor);
            }
            if (indexGenero != -1) {
                genero = cursor.getString(indexGenero);
            }
            if (indexPortada != -1) {
                portada = cursor.getString(indexPortada);
            }
            if (indexResumen != -1) {
                resumen = cursor.getString(indexResumen);
            }
            if (indexPrecio != -1) {
                precio = cursor.getFloat(indexPrecio);
            }

            cursor.close();
            return new Libro(isbn, titulo, autor, genero, portada, resumen, precio);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean buscarLibro(String isbn) {
        SQLiteDatabase db = getReadableDatabase();
        boolean existe = false;

        Cursor cursor = db.rawQuery("SELECT 1 FROM LIBROS WHERE isbn = ?", new String[]{isbn});
        if (cursor.moveToFirst()) {
            existe = true;
        }

        cursor.close();
        db.close();
        return existe;
    }

    public String[] obtenerPreferencias(String pUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] preferencias = new String[2]; // [0] = idioma, [1] = ordenLib

        // Consulta SQL
        String query = "SELECT idioma, ordenLib FROM USUARIOS WHERE usuario = ?";
        Cursor cursor = db.rawQuery(query, new String[]{pUsuario});

        if (cursor != null && cursor.moveToFirst()) {
            preferencias[0] = cursor.getString(cursor.getColumnIndexOrThrow("idioma")); // Idioma
            preferencias[1] = cursor.getString(cursor.getColumnIndexOrThrow("ordenLib")); // Orden de libros
            cursor.close();
            Log.d("OperacionesBD", "Se ha metido en el if del idioma y el idioma que devuelve es: " + preferencias[0]);
        } else {
            // Si no se encuentran preferencias, usar valores por defecto
            preferencias[0] = "Castellano"; // Idioma por defecto
            preferencias[1] = "Título ascendente"; // Orden por defecto
            Log.d("OperacionesBD", "No se ha metido en el if del idioma y el idioma que devuelve es: " + preferencias[0]);

        }

        db.close();
        return preferencias;
    }

    public void modificarPreferencias(String pUsuario, String pIdioma, String pOrdLib) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Crear un ContentValues para almacenar los datos
        ContentValues values = new ContentValues();
        values.put("idioma", pIdioma);
        values.put("ordenLib", pOrdLib);

        // Actualizar la tabla de usuarios con las nuevas preferencias
        db.update("USUARIOS", values, "usuario = ?", new String[]{pUsuario});

        db.close();
    }

    public void insertarLibro(String pIsbn, String pTitulo, String pAutor, String pGenero, String pPortada, String pResumen, double pPrecio) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isbn", pIsbn);
        values.put("titulo", pTitulo);
        values.put("autor", pAutor);
        values.put("genero", pGenero);
        values.put("portada", pPortada);
        values.put("resumen", pResumen);
        values.put("precio", pPrecio);

        db.insert("LIBROS", null, values);
        db.close();
    }

    public void insertarUsuario(String pUsuario, String pPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario", pUsuario);
        values.put("password", pPassword);

        db.insert("USUARIOS", null, values);
        db.close();
    }

    public boolean comprobarUsuario(String pUsuario, String pPassword) {
        boolean enc = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE usuario=? AND password=?", new String[]{pUsuario, pPassword});
        if (cursor.moveToFirst()) {
            enc = true;
        }
        return enc;
    }

    public boolean comprobarUsuario(String pUsuario) {
        boolean enc = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE usuario=?", new String[]{pUsuario});
        if (cursor.moveToFirst()) {
            enc = true;
        }
        return enc;
    }

    public void agregarAListaLeidos(String usuario, String isbn) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("isbn", isbn);
        db.insert("LEIDOS", null, values);
        db.close();
    }

    public void agregarAListaQuieroLeer(String usuario, String isbn) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("isbn", isbn);
        db.insert("QUIERE_LEER", null, values);
        db.close();
    }

    public void eliminarDeListaLeidos(String usuario, String isbn) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("LEIDOS", "usuario=? AND isbn=?", new String[]{usuario, isbn});
        db.close();
    }

    public void modificarPassword(String usuario, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("password", password);
        db.update("USUARIOS", valores, "usuario=?", new String[]{usuario}); // Filtrar por usuario
        db.close();
    }

    public void eliminarDeListaQuieroLeer(String usuario, String isbn) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("QUIERE_LEER", "usuario=? AND isbn=?", new String[]{usuario, isbn});
        db.close();
    }

    public boolean estaEnListaLeidos(String usuario, String isbn) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + "LEIDOS" + " WHERE usuario=? AND isbn=?", new String[]{usuario, isbn});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public boolean estaEnListaQuieroLeer(String usuario, String isbn) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + "QUIERE_LEER" + " WHERE usuario=? AND isbn=?", new String[]{usuario, isbn});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public ArrayList<Libro> listaLeidos(String pUsuario) {
        ArrayList<Libro> listaLibros = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            // Obtener base de datos en modo lectura
            db = getReadableDatabase();

            // Consulta SQL para obtener los libros leídos
            String query = "SELECT LIBROS.isbn, LIBROS.titulo, LIBROS.autor, LIBROS.genero, " +
                    "LIBROS.portada, LIBROS.resumen, LIBROS.precio " +
                    "FROM LEIDOS " +
                    "INNER JOIN LIBROS ON LIBROS.isbn = LEIDOS.isbn " +
                    "WHERE LEIDOS.usuario = ?";

            // Ejecutar la consulta
            cursor = db.rawQuery(query, new String[]{pUsuario});

            // Verificar si hay resultados
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String isbn = cursor.getString(0);
                    String titulo = cursor.getString(1);
                    String autor = cursor.getString(2);
                    String genero = cursor.getString(3);
                    String portada = cursor.getString(4); // Puede ser null
                    String resumen = cursor.getString(5); // Puede ser null
                    double precio = cursor.getDouble(6);

                    // Manejo de valores nulos
                    if (portada == null) portada = ""; // Valor por defecto si es null
                    if (resumen == null) resumen = ""; // Valor por defecto si es null

                    // Crear y agregar libro a la lista
                    listaLibros.add(new Libro(isbn, titulo, autor, genero, portada, resumen, precio));

                    // Depuración: Agregar log para ver los libros recuperados
                    Log.d("Libro Leído", "ISBN: " + isbn + ", Título: " + titulo + ", Autor: " + autor);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            // Manejo de errores: Log de excepciones
            Log.e("DB Error", "Error al recuperar los libros leídos: " + e.getMessage());
        } finally {
            // Asegurarse de cerrar el cursor y la base de datos
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        // Retornar la lista de libros leídos
        return listaLibros;
    }

    public ArrayList<Libro> listaQuieroLeer(String pUsuario) {
        ArrayList<Libro> listaLibros = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            // Obtener base de datos en modo lectura
            db = getReadableDatabase();

            // Consulta SQL para obtener los libros "Quiero Leer"
            String query = "SELECT LIBROS.isbn, LIBROS.titulo, LIBROS.autor, LIBROS.genero, " +
                    "LIBROS.portada, LIBROS.resumen, LIBROS.precio " +
                    "FROM QUIERE_LEER " +
                    "INNER JOIN LIBROS ON LIBROS.isbn = QUIERE_LEER.isbn " +
                    "WHERE QUIERE_LEER.usuario = ?";

            // Ejecutar la consulta
            cursor = db.rawQuery(query, new String[]{pUsuario});

            // Verificar si hay resultados
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String isbn = cursor.getString(0);
                    String titulo = cursor.getString(1);
                    String autor = cursor.getString(2);
                    String genero = cursor.getString(3);
                    String portada = cursor.getString(4); // Puede ser null
                    String resumen = cursor.getString(5); // Puede ser null
                    double precio = cursor.getDouble(6);

                    // Manejo de valores nulos
                    if (portada == null) portada = ""; // Valor por defecto si es null
                    if (resumen == null) resumen = ""; // Valor por defecto si es null

                    // Crear y agregar libro a la lista
                    listaLibros.add(new Libro(isbn, titulo, autor, genero, portada, resumen, precio));

                    // Depuración: Agregar log para ver los libros recuperados
                    Log.d("Libro Quiero Leer", "ISBN: " + isbn + ", Título: " + titulo + ", Autor: " + autor);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            // Manejo de errores: Log de excepciones
            Log.e("DB Error", "Error al recuperar los libros 'Quiero Leer': " + e.getMessage());
        } finally {
            // Asegurarse de cerrar el cursor y la base de datos
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        // Retornar la lista de libros "Quiero Leer"
        return listaLibros;
    }

}
