package Vista;

import Modelo.Configuracion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private Panel panel;
    private Logger logger;

    public Frame(){
        panel = new Panel(new Dimension(800,600));
        logger = LogManager.getRootLogger();
        init();
    }

    public void init(){
        this.setTitle("File System");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(panel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Configuración");
        JMenuItem configuracion = new JMenuItem("Ruta de archivos temporales");

        configuracion.addActionListener(e->{

            Configuracion config = panel.getConfig();

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (chooser.showDialog(null, "Seleccionar") == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getPath();
                config.setRuta(path);
                logger.info("La nueva ruta de archivos temporales es: "+path);
            }

        });

        menu.add(configuracion);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
