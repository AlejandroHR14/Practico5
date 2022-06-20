package Vista;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private Panel panel;

    public Frame(){
        panel = new Panel(new Dimension(800,600));
        init();
    }

    public void init(){
        this.setTitle("File System");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
