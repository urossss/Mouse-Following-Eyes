package eyes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

public class Renderer {

    private BufferStrategy bs;
    private Graphics2D g;

    private Display display;

    private String title;
    private int width, height;

    private int eyeWidth = 170, eyeHeight = 220;

    public Renderer(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void init() {
        display = new Display(title, width, height);
    }

    private void drawEye(Graphics2D g, int x, int y) {
        AffineTransform old = g.getTransform();

        g.translate(x, y);

        g.setColor(Color.BLACK);
        g.fillOval(-eyeWidth / 2 - 5, -eyeHeight / 2 - 5, eyeWidth + 10, eyeHeight + 10);

        g.setColor(Color.WHITE);
        g.fillOval(-eyeWidth / 2, -eyeHeight / 2, eyeWidth, eyeHeight);

        g.setColor(Color.BLACK);

        int xMouse = MouseInfo.getPointerInfo().getLocation().x - display.getCanvas().getLocationOnScreen().x - x;
        int yMouse = MouseInfo.getPointerInfo().getLocation().y - display.getCanvas().getLocationOnScreen().y - y;

        int xPupil = 50, yPupil = 80, centerX, centerY;

        double res = Math.pow(xMouse, 2) / Math.pow(xPupil, 2) + Math.pow(yMouse, 2) / Math.pow(yPupil, 2);

        if (res <= 1) {
            centerX = xMouse;
            centerY = yMouse;
        } else {
            if (xMouse == 0) {
                centerX = 0;
                centerY = yPupil * (yMouse > 0 ? 1 : -1);
            } else {
                double a = 1.0 * xPupil, b = 1.0 * yPupil, k = (double) yMouse / (double) xMouse;
                double xDoubleSquare = 1.0 / (1.0 / (a * a) + (k * k) / (b * b));
                centerX = (int) Math.sqrt(xDoubleSquare) * ((xMouse > 0) ? 1 : -1);
                centerY = (int) (Math.sqrt(1.0 - xDoubleSquare / (a * a)) * b * ((yMouse > 0) ? 1 : -1));
            }
            if (centerX == 0) {
                centerY = yPupil * (yMouse > 0 ? 1 : -1);
            }
        }

        g.fillOval(centerX - 40, centerY - 40, 80, 80);

        g.setTransform(old);
    }

    public void render(Graphics2D g) {
        bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = (Graphics2D) bs.getDrawGraphics();

        // Draw here
        g.clearRect(0, 0, width, height);
        
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);

        drawEye(g, 200, 230);
        drawEye(g, 400, 230);

        // End drawing
        bs.show();
        g.dispose();
    }

    public void start() {
        init();

        int fps = 60;
        double timePerTick = 1000000000.0 / fps, delta = 0;
        long now, last = System.nanoTime();

        while (true) {
            now = System.nanoTime();
            delta += (now - last) / timePerTick;
            last = now;

            if (delta >= 1) {
                render(g);
                delta--;
            }
        }
    }

}
