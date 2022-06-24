package Vista;

import Modelo.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panel extends JPanel {

    private Dimension tamano;
    private Arbol<ArchivoCarpeta> arbol;

    private String path;
    private String idNodoEnPantalla;

    private JTable tabla;
    private DefaultTableModel dt;

    private Configuracion config;

    private Logger logger;

    public Panel(Dimension tamanoPanel){

        this.tamano = tamanoPanel;

        arbol = new Arbol<>();
        Carpeta root = new Carpeta("root");
        arbol.insertar(root,"/",null);

        path = "/";
        idNodoEnPantalla = arbol.idPadre();

        init();
        componentes();
    }

    public void init(){
        this.setLayout(null);

        config = Configuracion.getInstance(System.getProperty("user.dir")+"\\temp");
        logger = LogManager.getRootLogger();
        logger.info("La ruta de archivos temporales predeterminada es: "+config.getRuta());
    }

    private void componentes() {
        JLabel lblPath = new JLabel(""+ idNodoEnPantalla);
        lblPath.setBounds(110,20,640,30);
        lblPath.setFont(new Font("SansSerif", Font.BOLD, 18));

        dt = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        dt.addColumn("Nombre");
        dt.addColumn("Tipo");
        dt.addColumn("Tamano");
        dt.addColumn("Nombre Fisico");

        tabla = new JTable(dt);

        tabla.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    String tipo = (String) tabla.getValueAt(tabla.getSelectedRow(), 1);

                    if (tipo.equalsIgnoreCase("F")){
                        idNodoEnPantalla = (String) tabla.getValueAt(tabla.getSelectedRow(), 0);
                        path += idNodoEnPantalla +"/";
                        lblPath.setText(path);
                        borrarFilas();
                        enlistar();

                    }else {

                        String idArchivoSucio = (String) tabla.getValueAt(tabla.getSelectedRow(), 3);

                        char[] arrAux = idArchivoSucio.toCharArray();

                        StringBuilder idArchivo = new StringBuilder();

                        for (int i = arrAux.length-1; i > 0; i--) {
                            if (arrAux[i] == '\\'){
                                break;
                            }
                            idArchivo.append(arrAux[i]);
                        }

                        Arbol.Nodo<ArchivoCarpeta> aux = arbol.buscar(idArchivo.reverse().toString());

                        Archivo archivoSeleccionado = (Archivo) aux.getContenido();

                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        if (chooser.showDialog(null, "Seleccionar") == JFileChooser.APPROVE_OPTION) {

                            String path = chooser.getSelectedFile().getPath();

                            File original = new File(archivoSeleccionado.getPathTemp());

                            File copied = new File(path+"\\"+archivoSeleccionado.getNombre()+"."+archivoSeleccionado.getTipo());

                            try {
                                FileUtils.copyFile(original, copied);
                                logger.info("Archivo "+"'"+archivoSeleccionado.getNombre()+"."+archivoSeleccionado.getTipo()+"'"+" ha sido copiado correctamente en : "+path+"\\");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                    }

                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(50,50,700, 500);

        JButton btnCargarArchivo = new JButton("Cargar archivo");
        btnCargarArchivo.setBounds(50,570,150,20);
        btnCargarArchivo.addActionListener(e->{
            JFileChooser chooser = new JFileChooser();

            if (chooser.showDialog(null, "Cargar") == JFileChooser.APPROVE_OPTION) {
                borrarFilas();

                String name = chooser.getSelectedFile().getName();
                long tamano = chooser.getSelectedFile().length();
                Archivo aux = new Archivo(name,tamano);

                String path = chooser.getSelectedFile().getPath();
                String rutaTemp = config.getRuta()+"\\"+aux.getNombreFisico();

                File original = new File(path);
                File copied = new File(rutaTemp);

                aux.setPathTemp(rutaTemp);

                try {
                    FileUtils.copyFile(original, copied);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                arbol.insertar(aux,aux.getNombreFisico(),idNodoEnPantalla);
                logger.info("Archivo "+"'"+name+"'"+" ha sido cargado correctamente!");

                enlistar();
            }
        });

        JButton btnCrearCarpeta = new JButton("Crear carpeta");
        btnCrearCarpeta.setBounds(600,570,150,20);
        btnCrearCarpeta.addActionListener(e->{
            String name = JOptionPane.showInputDialog("Escribe el nombre de la carpeta");
            String validar = "^[0-9a-zA-Z]+$";

            Pattern patron = Pattern.compile(validar);
            if (name == null){
                return;
            }
            Matcher m = patron.matcher(name);

            if (!m.find()){
                JOptionPane.showMessageDialog(null,"Por favor ingrese un nombre válido");
            }else {
                name = name.trim();
                Lista<Arbol.Nodo<ArchivoCarpeta>> listaAux = arbol.buscar(idNodoEnPantalla).getHijos();
                for (int i = 0; i < listaAux.tamano(); i++) {
                    ArchivoCarpeta aux = listaAux.obtener(i).getContenido();
                    if (aux instanceof Carpeta){
                        Carpeta carpetaAux = (Carpeta) aux;
                        if (name.equals(carpetaAux.getNombre())){
                            JOptionPane.showMessageDialog(null,"Ya existe una carpeta con ese nombre");
                            return;
                        }
                    }

                }
                Carpeta aux = new Carpeta(name);
                arbol.insertar(aux, name, idNodoEnPantalla);
                logger.info("Carpeta "+"'"+name+"'"+" ha sido creada correctamente!");
                borrarFilas();
                enlistar();
            }

        });

        JButton btnVolver = new JButton("<-");
        btnVolver.setBounds(50,20,50,30);
        btnVolver.addActionListener(e->{

            char[] arrChar = path.toCharArray();
            if (arrChar.length==1){
                return;
            }
            int slash = 0;

            for (int i = 0; i < arrChar.length; i++) {
                if (arrChar[i] == '/'){
                    slash++;
                }
            }

            slash--;

            String nuevoPath = "";
            int contador = 0;

            int i = 0;
            while (contador!=slash){
                if (arrChar[i] == '/'){
                    contador++;
                }
                nuevoPath += arrChar[i];
                i++;
            }

            String[] aux = nuevoPath.split("/");
            if (aux.length ==0){
                idNodoEnPantalla = "/";
            }else {
                idNodoEnPantalla = aux[aux.length-1];
            }

            if (idNodoEnPantalla.equalsIgnoreCase("/")){
                path="/";
                lblPath.setText("/");
            }else {
                lblPath.setText(nuevoPath);
                path = nuevoPath;
            }

            borrarFilas();
            enlistar();
        });


        add(btnCargarArchivo);
        add(btnCrearCarpeta);
        add(btnVolver);
        add(lblPath);
        add(scrollPane);
    }

    public void enlistar(){

        String[] fila = new String[dt.getColumnCount()];
        Lista<Arbol.Nodo<ArchivoCarpeta>> lista = arbol.hijosDelPadre(idNodoEnPantalla);

        for(int i = 0; i < lista.tamano(); i++){
            if (lista.obtener(i).getContenido() instanceof Archivo){
                Archivo aux = (Archivo) lista.obtener(i).getContenido();
                fila[0] = aux.getNombre();
                fila[1] = aux.getTipo();
                fila[2] = aux.getsTamano();
                fila[3] = aux.getPathTemp();
                dt.addRow(fila);
            }else {

                Carpeta aux = (Carpeta) lista.obtener(i).getContenido();
                Lista<Arbol.Nodo<ArchivoCarpeta>> listaCarpeta = arbol.hijosDelPadre(aux.getNombre());
                long tamanoTotal = 0;

                for (int j = 0; j < listaCarpeta.tamano(); j++) {
                    ArchivoCarpeta auxArchivoCarpeta = listaCarpeta.obtener(j).getContenido();
                    if (auxArchivoCarpeta instanceof Archivo){
                        Archivo auxArchivo = (Archivo) auxArchivoCarpeta;
                        tamanoTotal += auxArchivo.getTamano();
                    }else {
                        Carpeta auxCarpeta = (Carpeta) auxArchivoCarpeta;
                        tamanoTotal += auxCarpeta.getTamanoTotal();
                    }
                }

                aux.setTamanoTotal(tamanoTotal);

                fila[0] = aux.getNombre();
                fila[1] = "F";
                fila[2] = Archivo.calcularBytes(aux.getTamanoTotal());
                fila[3] = "-";
                dt.addRow(fila);
            }

        }

    }

    public void borrarFilas(){
        int filas=dt.getRowCount();
        for (int i = 0;filas>i; i++) {
            dt.removeRow(0);
        }
    }

    public Configuracion getConfig() {
        return config;
    }

    public void setConfig(Configuracion config) {
        this.config = config;
    }

    @Override
    public Dimension getPreferredSize() {
        return tamano;
    }
}
