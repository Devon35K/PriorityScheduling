import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Main{
    private JFrame frame;
    private JTable table;
    private JTextArea outputArea;
    private JButton preemptiveButton, nonPreemptiveButton, generateDataButton, explainButton;
    private JPanel ganttPanel;
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        // Initialize frame with modern look
        frame = new JFrame("Priority Scheduling Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(Color.WHITE);

        // Top Panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Priority Scheduling Simulator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);

        preemptiveButton = createStyledButton("Preemptive");
        nonPreemptiveButton = createStyledButton("Non-Preemptive");
        generateDataButton = createStyledButton("Generate Data");
        explainButton = createStyledButton("Explain");

        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(preemptiveButton);
        topPanel.add(nonPreemptiveButton);
        topPanel.add(generateDataButton);
        topPanel.add(explainButton);

        // Table setup
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 5);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Output area
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(SECONDARY_COLOR);
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Results"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Gantt Chart Panel
        ganttPanel = new GanttChartPanel();
        ganttPanel.setBackground(SECONDARY_COLOR);
        ganttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

        // Split pane for output and Gantt chart
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(outputArea), ganttPanel);
        bottomSplitPane.setDividerLocation(450);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tableScrollPane, bottomSplitPane);
        mainSplitPane.setDividerLocation(250);

        // Add components to frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplitPane, BorderLayout.CENTER);

        // Button actions
        preemptiveButton.addActionListener(e -> runScheduling(true));
        nonPreemptiveButton.addActionListener(e -> runScheduling(false));
        generateDataButton.addActionListener(e -> generateRandomData());
        explainButton.addActionListener(e -> showExplanationModal());

        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 195));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        return button;
    }

    private void showExplanationModal() {
        JDialog modal = new JDialog(frame, "Priority Scheduling Explanation", true);
        modal.setSize(600, 500);
        modal.setLayout(new BorderLayout());
        modal.setLocationRelativeTo(frame);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Preemptive", createExplanationPanel(true));
        tabbedPane.addTab("Non-Preemptive", createExplanationPanel(false));
        modal.add(tabbedPane, BorderLayout.CENTER);

        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> modal.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        modal.add(buttonPanel, BorderLayout.SOUTH);

        modal.setVisible(true);
    }

    private JPanel createExplanationPanel(boolean isPreemptive) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea explanationText = new JTextArea();
        explanationText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        explanationText.setEditable(false);
        explanationText.setLineWrap(true);
        explanationText.setWrapStyleWord(true);
        explanationText.setBackground(SECONDARY_COLOR);

        StringBuilder explanation = new StringBuilder();
        explanation.append(isPreemptive ? "Preemptive Priority Scheduling:\n\n" : "Non-Preemptive Priority Scheduling:\n\n");
        explanation.append("This algorithm schedules processes based on their priority, where lower priority numbers indicate higher priority.\n\n");
        explanation.append("Steps:\n");
        if (isPreemptive) {
            explanation.append("1. At each time unit, check for new arrivals.\n");
            explanation.append("2. Add arrived processes to a ready queue.\n");
            explanation.append("3. Select the process with the highest priority (lowest number).\n");
            explanation.append("4. Execute it for one time unit, reducing its remaining time.\n");
            explanation.append("5. If a higher-priority process arrives, preempt the current process.\n");
            explanation.append("6. Continue until all processes are completed.\n");
            explanation.append("7. Calculate waiting time (turnaround time - burst time) and turnaround time (finish time - arrival time).\n");
        } else {
            explanation.append("1. Sort processes by arrival time.\n");
            explanation.append("2. At each time unit, select available processes (arrival time ≤ current time).\n");
            explanation.append("3. Choose the process with the highest priority (lowest number).\n");
            explanation.append("4. Execute it completely without interruption.\n");
            explanation.append("5. Update the current time and repeat until all processes are completed.\n");
            explanation.append("6. Calculate waiting time (turnaround time - burst time) and turnaround time (finish time - arrival time).\n");
        }
        explanationText.setText(explanation.toString());

        // Create a simple example Gantt chart for explanation
        JPanel exampleGantt = new ExampleGanttChartPanel(isPreemptive);
        exampleGantt.setBorder(BorderFactory.createTitledBorder("Example Gantt Chart"));
        exampleGantt.setPreferredSize(new Dimension(500, 100));

        panel.add(new JScrollPane(explanationText), BorderLayout.CENTER);
        panel.add(exampleGantt, BorderLayout.SOUTH);
        return panel;
    }

    private void generateRandomData() {
        Random rand = new Random();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        int numProcesses = 5 + rand.nextInt(6);
        for (int i = 0; i < numProcesses; i++) {
            model.addRow(new Object[]{
                    i + 1,
                    rand.nextInt(10),
                    1 + rand.nextInt(10),
                    1 + rand.nextInt(5)
            });
        }
    }

    private void runScheduling(boolean isPreemptive) {
        List<Process> processes = new ArrayList<>();
        try {
            for (int i = 0; i < table.getRowCount(); i++) {
                if (table.getValueAt(i, 0) == null) continue;
                int id = Integer.parseInt(table.getValueAt(i, 0).toString());
                int arrival = Integer.parseInt(table.getValueAt(i, 1).toString());
                int burst = Integer.parseInt(table.getValueAt(i, 2).toString());
                int priority = Integer.parseInt(table.getValueAt(i, 3).toString());
                processes.add(new Process(id, arrival, burst, priority));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Please ensure all fields are filled with valid numbers.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please add at least one process.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result;
        List<GanttChartPanel.GanttEntry> ganttEntries;
        if (isPreemptive) {
            Scheduler.SchedulingResult res = Scheduler.runPreemptive(processes);
            result = res.output;
            ganttEntries = res.ganttEntries;
        } else {
            Scheduler.SchedulingResult res = Scheduler.runNonPreemptive(processes);
            result = res.output;
            ganttEntries = res.ganttEntries;
        }

        outputArea.setText(result);
        ((GanttChartPanel)ganttPanel).setGanttEntries(ganttEntries);
        ganttPanel.repaint();
    }

    static class Process {
        int id, arrival, burst, priority, remaining, waiting, turnaround, finish;

        Process(int id, int arrival, int burst, int priority) {
            this.id = id;
            this.arrival = arrival;
            this.burst = burst;
            this.priority = priority;
            this.remaining = burst;
        }
    }
}