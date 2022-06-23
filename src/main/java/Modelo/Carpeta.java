package Modelo;

public class Carpeta extends ArchivoCarpeta{

    private String nombre;
    private long tamanoTotal;

    public Carpeta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTamanoTotal() {
        return tamanoTotal;
    }

    public void setTamanoTotal(long tamanoTotal) {
        this.tamanoTotal = tamanoTotal;
    }

    @Override
    public String toString() {
        return "Carpeta{" +nombre+'}';
    }
}
