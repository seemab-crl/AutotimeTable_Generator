package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BackgroundPanel extends JPanel {

    private BufferedImage bgImage;

    public BackgroundPanel(String path) {
        try {
            bgImage = ImageIO.read(new File(path)); // read image from file
        } catch (Exception e) {
            System.out.println("Background image not found: " + path);
            bgImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            // Scale image to fill the panel
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
