import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GanttChartPanel extends JPanel {
    static class GanttEntry {
        int processId, start, end;
        GanttEntry(int processId, int start, int end) {
            this.processId = processId;
            this.start = start;
            this.end = end;
        }
    }

    private List<GanttEntry> ganttEntries = new ArrayList<>();
    private static final Color[] COLORS = {
            new Color(255, 102, 102), new Color(102, 255, 102), new Color(102, 102, 255),
            new Color(255, 255, 102), new Color(255, 102, 255), new Color(102, 255, 255),
            new Color(255, 178, 102), new Color(178, 102, 255)
    };

    void setGanttEntries(List<GanttEntry> entries) {
        this.ganttEntries = entries;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (ganttEntries.isEmpty()) {
            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g.setColor(Color.GRAY);
            g.drawString("No schedule data available", getWidth() / 2 - 80, getHeight() / 2);
            return;
        }

        int width = getWidth() - 60;
        int height = getHeight() - 100;
        int maxTime = ganttEntries.stream().mapToInt(e -> e.end).max().orElse(1);
        if (maxTime == 0) maxTime = 1;

        int xScale = Math.max(10, width / maxTime);
        int y = 50;
        int barHeight = Math.min(30, height / 3);
        int margin = 20;

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.setColor(Color.BLACK);
        int timeInterval = Math.max(1, maxTime / 10);
        for (int t = 0; t <= maxTime; t += timeInterval) {
            int x = margin + t * xScale;
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawLine(x, y - 10, x, y + barHeight + 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(t), x - 5, y + barHeight + 25);
        }
        g2d.drawLine(margin, y + barHeight + 10, margin + maxTime * xScale, y + barHeight + 10);

        for (GanttEntry entry : ganttEntries) {
            int xStart = margin + entry.start * xScale;
            int xEnd = margin + entry.end * xScale;
            int barWidth = Math.max(10, xEnd - xStart);
            g2d.setColor(COLORS[entry.processId % COLORS.length]);
            g2d.fillRect(xStart, y, barWidth, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(xStart, y, barWidth, barHeight);

            String label = "P" + entry.processId;
            int fontSize = Math.min(12, barHeight - 10);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            if (barWidth > labelWidth + 5) {
                g2d.drawString(label, xStart + (barWidth - labelWidth) / 2, y + barHeight / 2 + 5);
            }
        }

        drawLegend(g2d, margin, y + barHeight + 50, COLORS);
    }

    private void drawLegend(Graphics2D g2d, int x, int y, Color[] colors) {
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        int legendY = y;
        Set<Integer> processIds = new HashSet<>();
        for (GanttEntry entry : ganttEntries) {
            processIds.add(entry.processId);
        }

        int xOffset = 0;
        int boxSize = 15;
        for (Integer id : processIds) {
            g2d.setColor(colors[id % colors.length]);
            g2d.fillRect(x + xOffset, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + xOffset, legendY, boxSize, boxSize);
            g2d.drawString("P" + id, x + xOffset + boxSize + 5, legendY + boxSize - 2);
            xOffset += 60;
            if (xOffset > getWidth() - 100) {
                xOffset = 0;
                legendY += 20;
            }
        }
    }


}