package Vista;

import Modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class Panel extends JPanel {

    private Dimension tamano;
    private Arbol<ArchivoCarpeta> arbol;

    private String path;
    private String idNodoEnPantalla;

    private JTable tabla;
    private DefaultTableModel dt;

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
                        JOptionPane.showMessageDialog(null,"Chupala");
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
                //path = chooser.getSelectedFile().getPath();
                String name = chooser.getSelectedFile().getName();
                long tamano = chooser.getSelectedFile().length();
                Archivo aux = new Archivo(name,tamano);
                arbol.insertar(aux,aux.getNombreFisico(),idNodoEnPantalla);
                enlistar();
            }
        });

        JButton btnCrearCarpeta = new JButton("Crear carpeta");
        btnCrearCarpeta.setBounds(600,570,150,20);
        btnCrearCarpeta.addActionListener(e->{
            String name = JOptionPane.showInputDialog("Escribe el nombre de la carpeta");
            Carpeta aux = new Carpeta(name);
            arbol.insertar(aux, name, idNodoEnPantalla);
            borrarFilas();
            enlistar();
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
                fila[3] = aux.getNombreFisico();
                dt.addRow(fila);
            }else {

                Carpeta aux = (Carpeta) lista.obtener(i).getContenido();
                Lista<Arbol.Nodo<ArchivoCarpeta>> listaCarpeta = arbol.hijosDelPadre(aux.getNombre());
                long tamanoTotal = 0;

                for (int j = 0; j < listaCarpeta.tamano(); j++) {
                    ArchivoCarpeta auxArchivoCarpeta = listaCarpeta.obtener(j).getContenido();
                    if (auxArchivoCarpeta instanceof Archivo){
                        Archivo auxArchivo = (Archivo) listaCarpeta.obtener(j).getContenido();
                        tamanoTotal += auxArchivo.getTamano();
                    }else {
                        Carpeta auxCarpeta = (Carpeta) listaCarpeta.obtener(j).getContenido();
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

    public void eliminar() {

        int row = tabla.getSelectedRow();
        //logger.info("Persona: "+ arbol.obtener(row).getNombre() +". ELIMINADO");
        //arbol.eliminar(row);
        dt.removeRow(row);

    }

    public void guardarArchivo(){

        try {
            File archivo = new File(path);
            FileWriter fw = new FileWriter(archivo, false);
            BufferedWriter buffer = new BufferedWriter(fw);

            /*for (int i = 0; i < arbol.size(); i++) {
                //Persona aux = arbol.obtener(i);
                //String fila = aux.getCI()+" "+aux.getNombre()+" "+aux.getApellido()+" "+aux.getEdad();
                //buffer.write(fila);
                //buffer.newLine();
            }*/

            //buffer.flush();
            //buffer.close();
            //logger.info("ARCHIVO GUARDADO CORRECTAMENTE");

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return tamano;
    }
}
