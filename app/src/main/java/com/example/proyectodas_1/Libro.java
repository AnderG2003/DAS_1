package com.example.proyectodas_1;

public class Libro {

    private String isbn;
    private String titulo;
    private String autor;
    private String genero;
    private String portada;
    private String resumen;
    private double precio;
    public Libro (String isbn, String titulo, String autor, String genero, String portada, String resumen, double precio) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.portada = portada;
        this.resumen = resumen;
        this.precio = precio;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getGenero() {
        return genero;
    }

    public String getPortada() {
        return portada;
    }

    public String getResumen() {
        return resumen;
    }

    public double getPrecio() {
        return precio;
    }

    // Setters
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }


}
