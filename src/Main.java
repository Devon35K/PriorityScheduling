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
    private JScrollPane ganttScrollPane; // Changed to JScrollPane
    private GanttChartPanel ganttPanel;
    private JSpinner processCountSpinner;
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        // Initialize frame with modern look
        frame = new JFrame("Priority Scheduling Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(Color.WHITE);

        // Set custom window icon
        try {
            Image icon = new ImageIcon("image/icon.png").getImage();
            frame.setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        // Top Panel with buttons and process count input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title row
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Priority Scheduling Simulator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Controls row
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setBackground(Color.WHITE);

        // Process count input
        JLabel processCountLabel = new JLabel("Number of Processes:");
        processCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        processCountSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 20, 1));
        processCountSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        processCountSpinner.setPreferredSize(new Dimension(60, 30));

        preemptiveButton = createStyledButton("Preemptive");
        nonPreemptiveButton = createStyledButton("Non-Preemptive");
        generateDataButton = createStyledButton("Generate Data");
        explainButton = createStyledButton("Explain");

        controlsPanel.add(processCountLabel);
        controlsPanel.add(processCountSpinner);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(generateDataButton);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(preemptiveButton);
        controlsPanel.add(nonPreemptiveButton);
        controlsPanel.add(explainButton);

        topPanel.add(titlePanel);
        topPanel.add(controlsPanel);

        // Table setup
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 5);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(SECONDARY_COLOR);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Process Information"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Output area
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(SECONDARY_COLOR);
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Results"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Gantt Chart Panel with Scroll Pane
        ganttPanel = new GanttChartPanel();
        ganttPanel.setBackground(Color.WHITE);

        // Create scroll pane for Gantt chart
        ganttScrollPane = new JScrollPane(ganttPanel);
        ganttScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        ganttScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Gantt Chart"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        ganttScrollPane.setBackground(SECONDARY_COLOR);
        ganttScrollPane.getViewport().setBackground(Color.WHITE);

        // Split pane for output and Gantt chart
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(outputArea), ganttScrollPane);
        bottomSplitPane.setDividerLocation(450);
        bottomSplitPane.setResizeWeight(0.4); // Give more space to Gantt chart

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tableScrollPane, bottomSplitPane);
        mainSplitPane.setDividerLocation(280);

        // Add components to frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplitPane, BorderLayout.CENTER);

        // Button actions
        preemptiveButton.addActionListener(e -> runScheduling(true));
        nonPreemptiveButton.addActionListener(e -> runScheduling(false));
        generateDataButton.addActionListener(e -> generateRandomData());
        explainButton.addActionListener(e -> showExplanationModal());

        frame.setLocationRelativeTo(null);
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
        button.setPreferredSize(new Dimension(120, 35));
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

        int numProcesses = (Integer) processCountSpinner.getValue();

        // Create a list of unique priorities to avoid duplicates
        Set<Integer> usedPriorities = new HashSet<>();
        List<Integer> availablePriorities = new ArrayList<>();

        // First, try to assign unique priorities 1 through numProcesses
        for (int i = 1; i <= numProcesses; i++) {
            availablePriorities.add(i);
        }

        // If we need more priorities than available unique ones, add higher numbers
        if (numProcesses > 10) {
            for (int i = numProcesses + 1; i <= numProcesses + 5; i++) {
                availablePriorities.add(i);
            }
        }

        Collections.shuffle(availablePriorities);

        for (int i = 0; i < numProcesses; i++) {
            int priority;
            if (i < availablePriorities.size()) {
                priority = availablePriorities.get(i);
            } else {
                // Fallback: if we somehow run out of unique priorities
                do {
                    priority = 1 + rand.nextInt(Math.max(5, numProcesses));
                } while (usedPriorities.contains(priority) && usedPriorities.size() < numProcesses);
            }

            usedPriorities.add(priority);

            model.addRow(new Object[]{
                    i + 1,                          // Process ID
                    rand.nextInt(10),               // Arrival Time (0-9)
                    1 + rand.nextInt(10),           // Burst Time (1-10)
                    priority                        // Unique Priority
            });
        }

        // Show a message about the generated data
        JOptionPane.showMessageDialog(frame,
                String.format("Generated %d processes with unique priorities.\n" +
                                "Process IDs: 1-%d\n" +
                                "Arrival Times: 0-9\n" +
                                "Burst Times: 1-10\n" +
                                "Priorities: Unique values from available range",
                        numProcesses, numProcesses),
                "Data Generated",
                JOptionPane.INFORMATION_MESSAGE);
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

                // Validate input
                if (burst <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Burst time must be greater than 0 for Process " + id,
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (priority <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Priority must be greater than 0 for Process " + id,
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (arrival < 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Arrival time cannot be negative for Process " + id,
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                processes.add(new Process(id, arrival, burst, priority));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please ensure all fields are filled with valid numbers.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please add at least one process.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate priorities and warn user
        Set<Integer> priorities = new HashSet<>();
        Set<Integer> duplicatePriorities = new HashSet<>();
        for (Process p : processes) {
            if (!priorities.add(p.priority)) {
                duplicatePriorities.add(p.priority);
            }
        }

        if (!duplicatePriorities.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(frame,
                    "Warning: Duplicate priorities found: " + duplicatePriorities + "\n" +
                            "This may affect scheduling results as processes will be selected arbitrarily among equal priorities.\n" +
                            "Do you want to continue?",
                    "Duplicate Priorities Detected",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
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
        ganttPanel.setGanttEntries(ganttEntries);

        // Ensure the scroll pane updates its scrollbars
        ganttScrollPane.revalidate();
        ganttScrollPane.repaint();

        // Optional: Scroll to the beginning to show the start of the chart
        SwingUtilities.invokeLater(() -> {
            ganttScrollPane.getHorizontalScrollBar().setValue(0);
        });
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