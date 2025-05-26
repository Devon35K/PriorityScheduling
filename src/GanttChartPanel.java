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

    GanttChartPanel() {
        setPreferredSize(new Dimension(800, 300)); // Default size
    }

    void setGanttEntries(List<GanttEntry> entries) {
        this.ganttEntries = entries;
        updatePreferredSize();
        revalidate();
        repaint();
    }

    private void updatePreferredSize() {
        if (ganttEntries.isEmpty()) {
            setPreferredSize(new Dimension(800, 300));
            return;
        }

        int maxTime = ganttEntries.stream().mapToInt(e -> e.end).max().orElse(1);
        if (maxTime == 0) maxTime = 1;

        // Calculate scale for preferred width
        int xScale = Math.max(25, 800 / maxTime); // Minimum scale to avoid cramping
        int preferredWidth = 60 + maxTime * xScale; // Margin + content width
        setPreferredSize(new Dimension(preferredWidth, 300));
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

        // Calculate scale based on maximum time
        int xScale = Math.max(25, width / maxTime);

        int y = 50;
        int barHeight = Math.min(30, height / 3);
        int margin = 20;

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);

        // Draw all time markers from 0 to maxTime
        for (int t = 0; t <= maxTime; t++) {
            int x = margin + t * xScale;

            // Draw vertical grid lines
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawLine(x, y - 10, x, y + barHeight + 10);

            // Draw time labels
            g2d.setColor(Color.BLACK);
            String timeLabel = String.valueOf(t);
            int labelWidth = g2d.getFontMetrics().stringWidth(timeLabel);

            // Center the label under the grid line
            g2d.drawString(timeLabel, x - labelWidth / 2, y + barHeight + 25);
        }

        // Draw horizontal baseline
        g2d.drawLine(margin, y + barHeight + 10, margin + maxTime * xScale, y + barHeight + 10);

        // Draw Gantt bars
        for (GanttEntry entry : ganttEntries) {
            int xStart = margin + entry.start * xScale;
            int xEnd = margin + entry.end * xScale;
            int barWidth = Math.max(10, xEnd - xStart);

            // Fill the bar with process color
            g2d.setColor(COLORS[entry.processId % COLORS.length]);
            g2d.fillRect(xStart, y, barWidth, barHeight);

            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(xStart, y, barWidth, barHeight);

            // Draw process label inside the bar
            String label = "P" + entry.processId;
            int fontSize = Math.min(12, barHeight - 6);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();

            // Only draw label if there's enough space
            if (barWidth > labelWidth + 4) {
                int labelX = xStart + (barWidth - labelWidth) / 2;
                int labelY = y + (barHeight - labelHeight) / 2 + labelHeight;
                g2d.drawString(label, labelX, labelY);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Scrollable Gantt Chart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);

            GanttChartPanel panel = new GanttChartPanel();
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

            // Sample Gantt entries with a long timeline
            List<GanttEntry> entries = new ArrayList<>();
            entries.add(new GanttEntry(1, 0, 5));
            entries.add(new GanttEntry(2, 5, 15));
            entries.add(new GanttEntry(3, 15, 25));
            entries.add(new GanttEntry(1, 25, 35));
            entries.add(new GanttEntry(4, 35, 50));
            panel.setGanttEntries(entries);

            frame.add(scrollPane);
            frame.setVisible(true);
        });
    }
}