import javax.swing.*;
import java.awt.*;

class ExampleGanttChartPanel extends JPanel {
    private final boolean isPreemptive;
    private static final Color[] COLORS = {
            new Color(255, 102, 102), new Color(102, 255, 102), new Color(102, 102, 255)
    };

    ExampleGanttChartPanel(boolean isPreemptive) {
        this.isPreemptive = isPreemptive;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 60;
        int height = getHeight() - 40;
        int maxTime = isPreemptive ? 7 : 6;
        int xScale = width / maxTime;
        int y = 20;
        int barHeight = 30;
        int margin = 20;

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        for (int t = 0; t <= maxTime; t++) {
            int x = margin + t * xScale;
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawLine(x, y - 10, x, y + barHeight + 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(t), x - 5, y + barHeight + 25);
        }
        g2d.drawLine(margin, y + barHeight + 10, margin + maxTime * xScale, y + barHeight + 10);

        if (isPreemptive) {
            // Example: P1 (0-1, priority 2), P2 arrives at 1 (priority 1), P1 resumes at 4
            drawGanttBar(g2d, 1, 0, 1, xScale, y, margin, barHeight);
            drawGanttBar(g2d, 2, 1, 4, xScale, y, margin, barHeight);
            drawGanttBar(g2d, 1, 4, 7, xScale, y, margin, barHeight);
        } else {
            // Example: P1 (0-3, priority 2), P2 (3-6, priority 1)
            drawGanttBar(g2d, 1, 0, 3, xScale, y, margin, barHeight);
            drawGanttBar(g2d, 2, 3, 6, xScale, y, margin, barHeight);
        }
    }

    private void drawGanttBar(Graphics2D g2d, int processId, int start, int end, int xScale, int y, int margin, int barHeight) {
        int xStart = margin + start * xScale;
        int xEnd = margin + end * xScale;
        int barWidth = xEnd - xStart;
        g2d.setColor(COLORS[processId % COLORS.length]);
        g2d.fillRect(xStart, y, barWidth, barHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(xStart, y, barWidth, barHeight);
        String label = "P" + processId;
        int fontSize = 12;
        g2d.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        int labelWidth = g2d.getFontMetrics().stringWidth(label);
        if (barWidth > labelWidth + 5) {
            g2d.drawString(label, xStart + (barWidth - labelWidth) / 2, y + barHeight / 2 + 5);
        }
    }
}