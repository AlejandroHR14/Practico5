package Modelo;

public class Archivo extends ArchivoCarpeta{

    private String nombre;
    private String tipo;
    private int tamano;
    private String nombreFisico;

    public Archivo(String nombreFicticio, int tamano, String nombreFisico) {
        this.nombre = nombreFicticio;
        this.tamano = tamano;
        this.nombreFisico = nombreFisico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombreFicticio) {
        this.nombre = nombreFicticio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public String getNombreFisico() {
        return nombreFisico;
    }

    public void setNombreFisico(String nombreFisico) {
        this.nombreFisico = nombreFisico;
    }

    @Override
    public String toString() {
        return "Archivo{" + nombre + ";"+tamano +";"+nombreFisico+'}';
    }
}
