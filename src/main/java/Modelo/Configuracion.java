package Modelo;

public class Configuracion {

    private String ruta;
    private static Configuracion instance;

    private Configuracion(String ruta) {
        this.ruta = ruta;
    }

    public static Configuracion getInstance(String ruta) {
        if (instance == null){
            instance = new Configuracion(ruta);
        }
        return instance;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
