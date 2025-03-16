package com.example.proyectodas_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class BD extends SQLiteOpenHelper {
    private static final String BD_NOMBRE = "biblioteca.db"; // Nombre de la base de datos
    private final Context context;

    public BD(@Nullable Context context, int version) {
        super(context, BD_NOMBRE, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        db.execSQL("DROP TABLE IF EXISTS LIBROS");
        db.execSQL("DROP TABLE IF EXISTS USUARIOS");
        db.execSQL("DROP TABLE IF EXISTS LEIDOS");
        db.execSQL("DROP TABLE IF EXISTS QUIERE_LEER");

        // Creación de la tabla de libros
        db.execSQL("CREATE TABLE LIBROS (" +
                "isbn TEXT PRIMARY KEY, " +
                "titulo TEXT NOT NULL, " +
                "autor TEXT NOT NULL, " +
                "genero TEXT NOT NULL, " +
                "portada TEXT, " + // Ruta o URI de la imagen
                "resumen TEXT NOT NULL, "+
                "precio FLOAT NOT NULL)"
        );
        db.execSQL("INSERT INTO LIBROS (isbn, titulo, autor, genero, portada, resumen, precio) VALUES " +
                "('978-3-16-148410-0', 'Cien años de soledad', 'Gabriel García Márquez', 'Realismo mágico', 'libro1', 'Una historia épica de la familia Buendía en el mítico pueblo de Macondo.', 12.99), " +
                "('978-84-481-6987-6', 'Octavo viaje al Reino de la Fantasía', 'Geronimo Stilton', 'Aventura, Infantil', 'libro2', 'Geronimo Stilton y sus amigos regresan al Reino de la Fantasía para una nueva y emocionante aventura llena de magia, desafíos y criaturas fantásticas.', 10.50), " +
                "('978-0-06-112008-4', 'El Alquimista', 'Paulo Coelho', 'Aventura, Filosofía', 'libro3', 'La historia de un joven pastor llamado Santiago que viaja en busca de un tesoro y, en el proceso, aprende importantes lecciones sobre la vida y el destino.', 12.99),"  +
                "('978-0-14-044913-6', 'Crimen y castigo', 'Fiódor Dostoyevski', 'Filosofía, Psicológico', 'libro4', 'La lucha interna de un joven estudiante que comete un asesinato en un intento de justificar sus acciones.', 14.99), " +
                "('978-84-376-0494-7', 'Lo que el viento se llevó', 'Margaret Mitchell', 'Romántico, Histórico', 'libro5', 'Una épica historia de amor y lucha en el contexto de la Guerra Civil Americana y la Reconstrucción en el Sur de Estados Unidos.', 14.99)," +
                "('978-1-4767-4650-7', 'Los juegos del hambre', 'Suzanne Collins', 'Fantasía, Ciencia ficción', 'libro6', 'En un futuro post-apocalíptico, Katniss Everdeen debe luchar en un evento mortal transmitido por televisión.', 18.00), " +
                "('978-0-316-76948-8', 'Harry Potter y la piedra filosofal', 'J.K. Rowling', 'Fantasía, Aventura', 'libro7', 'Un niño descubre que es un mago y se embarca en un viaje lleno de magia, misterios y enemigos.', 20.99), " +
                "('978-0-679-74411-5', 'Matar a un ruiseñor', 'Harper Lee', 'Ficción, Drama', 'libro8', 'Una historia sobre la lucha contra el racismo y la justicia, narrada desde la perspectiva de una niña.', 11.25), " +
                "('978-1-5011-5406-7', 'La chica del tren', 'Paula Hawkins', 'Misterio, Suspenso', 'libro9', 'Una mujer se ve envuelta en un misterio relacionado con la desaparición de una chica mientras viaja en tren.', 9.99), " +
                "('978-0-141-18450-0', 'Orgullo y prejuicio', 'Jane Austen', 'Romántico', 'libro10', 'La historia de amor entre Elizabeth Bennet y Mr. Darcy en el contexto de la sociedad inglesa del siglo XIX.', 7.50)"
        );
        db.execSQL("CREATE TABLE USUARIOS ("+
                "usuario TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "idioma TEXT NOT NULL DEFAULT 'Castellano', " +
                "ordenLib TEXT DEFAULT '')"
        );
        db.execSQL("INSERT INTO USUARIOS VALUES ('Anonimo','', 'Castellano', '')");
        db.execSQL("CREATE TABLE LEIDOS (" +
                "usuario TEXT NOT NULL, " +
                "isbn TEXT NOT NULL, " +
                "PRIMARY KEY(usuario, isbn), " +
                "FOREIGN KEY(usuario) REFERENCES USUARIOS(usuario) ON DELETE CASCADE, " +
                "FOREIGN KEY(isbn) REFERENCES LIBROS(isbn) ON DELETE CASCADE)"
        );
        db.execSQL("CREATE TABLE QUIERE_LEER (" +
                "usuario TEXT NOT NULL, " +
                "isbn TEXT NOT NULL, " +
                "PRIMARY KEY(usuario, isbn), " +
                "FOREIGN KEY(usuario) REFERENCES USUARIOS(usuario) ON DELETE CASCADE, " +
                "FOREIGN KEY(isbn) REFERENCES LIBROS(isbn) ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la BD cambia, eliminamos la tabla antigua y creamos una nueva
        /*db.execSQL("DROP TABLE IF EXISTS LIBROS");
        onCreate(db);*/
    }
}

