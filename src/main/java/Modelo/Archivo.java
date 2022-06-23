package Modelo;

import java.text.DecimalFormat;

public class Archivo extends ArchivoCarpeta{

    private String nombre;
    private String tipo;
    private String sTamano;
    private long tamano;
    private String nombreFisico;

    public Archivo(String nombre, long tamano) {
        this.nombre = nombreLimpio(nombre);
        this.tamano = tamano;
        this.sTamano = calcularBytes(tamano);
        this.nombreFisico = encriptarNombre(nombre);
    }

    private String nombreLimpio(String nombre) {

        char[] Arr = nombre.toCharArray();

        int posicionPunto = 0;

        for (int i = Arr.length-1; i > 0; i--) {
            if (Arr[i] == '.'){
                posicionPunto = i;
                break;
            }
        }

        String nombreLimpio = nombre.substring(0,posicionPunto);

        this.tipo = nombre.substring(posicionPunto+1);

        return nombreLimpio;
    }

    private String encriptarNombre(String nombre) {

        String[] abecedario = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","u","v",
                "w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","U","V",
                "W","X","Y","Z","0","1","2","3","4","5","6","7","8","9"};

        String nombreCambiado = "";

        for (int i = 0; i <= 7; i++) {
            int random = (int) (Math.random() * 60);
            nombreCambiado += abecedario[random];
        }

        return nombreCambiado;
    }

    public static String calcularBytes(long tamano) {

        DecimalFormat dc = new DecimalFormat("#.00");
        String nuevoTamano = "";

        if ((tamano >= 0) && (tamano<Math.pow(2,10))){
            nuevoTamano = tamano + "B";


        } else if ((tamano>Math.pow(2,10)) && (tamano<Math.pow(2,20))) {

            double nuevo = tamano / Math.pow(2,10);
            nuevoTamano = dc.format(nuevo) + "KB";


        } else if ((tamano>Math.pow(2,20)) && (tamano<Math.pow(2,30))) {
            double nuevo = tamano / Math.pow(2,20);
            nuevoTamano = dc.format(nuevo) + "MB";

        } else if ((tamano>Math.pow(2,30)) && (tamano<Math.pow(2,40))) {
            double nuevo = tamano / Math.pow(2,30);
            nuevoTamano = dc.format(nuevo) + "GB";

        }else {
            nuevoTamano = "> 1000 GB ";
        }
        return nuevoTamano;
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

    public long getTamano() {
        return tamano;
    }

    public void setTamano(long tamano) {
        this.tamano = tamano;
    }

    public String getsTamano() {
        return sTamano;
    }

    public void setsTamano(String sTamano) {
        this.sTamano = sTamano;
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
