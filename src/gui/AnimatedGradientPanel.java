package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatedGradientPanel extends JPanel implements ActionListener {

    private float hue = 0f;
    private final Timer timer;

    public AnimatedGradientPanel() {
        setLayout(new BorderLayout());

        // 30 FPS smooth animation
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        // Animated colors using HSB
        Color color1 = Color.getHSBColor(hue, 0.6f, 0.8f);
        Color color2 = Color.getHSBColor((hue + 0.15f) % 1f, 0.6f, 0.9f);

        GradientPaint gp = new GradientPaint(
                0, 0, color1,
                getWidth(), getHeight(), color2
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hue += 0.002f;   // speed of animation
        if (hue > 1f) hue = 0f;
        repaint();
    }
}
