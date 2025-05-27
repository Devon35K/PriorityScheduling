import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Scheduler {
    static class SchedulingResult {
        String output;
        List<GanttChartPanel.GanttEntry> ganttEntries;
        List<ExecutionStep> executionSteps;
        String firstProcessInfo;

        SchedulingResult(String output, List<GanttChartPanel.GanttEntry> ganttEntries,
                         List<ExecutionStep> executionSteps, String firstProcessInfo) {
            this.output = output;
            this.ganttEntries = ganttEntries;
            this.executionSteps = executionSteps;
            this.firstProcessInfo = firstProcessInfo;
        }
    }

    static class ExecutionStep {
        int time;
        int processId;
        int remainingTime;
        String action;
        boolean isCompletion;

        ExecutionStep(int time, int processId, int remainingTime, String action, boolean isCompletion) {
            this.time = time;
            this.processId = processId;
            this.remainingTime = remainingTime;
            this.action = action;
            this.isCompletion = isCompletion;
        }

        @Override
        public String toString() {
            if (isCompletion) {
                return String.format("Time %d: Process P%d completed", time, processId);
            } else {
                return String.format("Time %d: Process P%d executing (remaining: %d)",
                        time, processId, remainingTime);
            }
        }
    }

    public static SchedulingResult runNonPreemptive(List<Main.Process> input) {
        List<Main.Process> processes = new ArrayList<>(input);
        List<GanttChartPanel.GanttEntry> ganttEntries = new ArrayList<>();
        List<ExecutionStep> executionSteps = new ArrayList<>();
        StringBuilder sb = new StringBuilder("Non-Preemptive Priority Scheduling\n\n");

        processes.sort(Comparator.comparingInt(p -> p.arrival));
        int time = 0;
        List<Main.Process> completed = new ArrayList<>();
        String firstProcessInfo = "";
        boolean firstProcessFound = false;

        while (!processes.isEmpty()) {
            List<Main.Process> available = new ArrayList<>();
            for (Main.Process p : processes) {
                if (p.arrival <= time) available.add(p);
            }
            if (available.isEmpty()) {
                time++;
                continue;
            }

            available.sort(Comparator.comparingInt(p -> p.priority));
            Main.Process current = available.get(0);

            // Track first process to execute
            if (!firstProcessFound) {
                firstProcessInfo = String.format("First Process to Execute: P%d (Priority: %d, Arrival: %d, Burst: %d)",
                        current.id, current.priority, current.arrival, current.burst);
                firstProcessFound = true;
            }

            ganttEntries.add(new GanttChartPanel.GanttEntry(current.id, time, time + current.burst));

            // Add execution steps for each time unit
            for (int i = 0; i < current.burst; i++) {
                int remainingTime = current.burst - i - 1;
                boolean isLastStep = (i == current.burst - 1);
                executionSteps.add(new ExecutionStep(time + i, current.id, remainingTime,
                        isLastStep ? "completing" : "executing", isLastStep));
            }

            time += current.burst;
            current.finish = time;
            current.turnaround = time - current.arrival;
            current.waiting = current.turnaround - current.burst;
            completed.add(current);
            processes.remove(current);
        }

        return new SchedulingResult(generateOutput(completed, sb, executionSteps), ganttEntries,
                executionSteps, firstProcessInfo);
    }

    public static SchedulingResult runPreemptive(List<Main.Process> input) {
        List<Main.Process> processes = new ArrayList<>();
        for (Main.Process p : input) {
            processes.add(new Main.Process(p.id, p.arrival, p.burst, p.priority));
        }
        List<GanttChartPanel.GanttEntry> ganttEntries = new ArrayList<>();
        List<ExecutionStep> executionSteps = new ArrayList<>();
        StringBuilder sb = new StringBuilder("Preemptive Priority Scheduling\n\n");

        int time = 0;
        int completedCount = 0;
        Main.Process current = null;
        List<Main.Process> readyQueue = new ArrayList<>();
        int lastProcessId = -1;
        int startTime = 0;
        String firstProcessInfo = "";
        boolean firstProcessFound = false;

        while (completedCount < processes.size()) {
            // Add newly arrived processes to ready queue
            for (Main.Process p : processes) {
                if (p.arrival == time) readyQueue.add(p);
            }
            readyQueue.sort(Comparator.comparingInt(p -> p.priority));

            if (!readyQueue.isEmpty()) {
                Main.Process next = readyQueue.get(0);

                // Handle process switching
                if (current != next && current != null && lastProcessId != -1) {
                    ganttEntries.add(new GanttChartPanel.GanttEntry(lastProcessId, startTime, time));
                }

                current = next;

                // Track first process to execute
                if (!firstProcessFound) {
                    firstProcessInfo = String.format("First Process to Execute: P%d (Priority: %d, Arrival: %d, Burst: %d)",
                            current.id, current.priority, current.arrival, current.burst);
                    firstProcessFound = true;
                }

                if (lastProcessId != current.id) {
                    lastProcessId = current.id;
                    startTime = time;
                }

                // Execute current process for one time unit
                current.remaining--;
                executionSteps.add(new ExecutionStep(time, current.id, current.remaining,
                        "executing", current.remaining == 0));

                if (current.remaining == 0) {
                    current.finish = time + 1;
                    current.turnaround = current.finish - current.arrival;
                    current.waiting = current.turnaround - current.burst;
                    ganttEntries.add(new GanttChartPanel.GanttEntry(current.id, startTime, time + 1));
                    readyQueue.remove(current);
                    completedCount++;
                    lastProcessId = -1;
                }
            }
            time++;
        }

        return new SchedulingResult(generateOutput(processes, sb, executionSteps), ganttEntries,
                executionSteps, firstProcessInfo);
    }

    private static String generateOutput(List<Main.Process> processes, StringBuilder sb,
                                         List<ExecutionStep> executionSteps) {
        double totalWaiting = 0, totalTurnaround = 0;

        // Add execution timeline
        sb.append("Execution Timeline:\n");
        sb.append("-".repeat(50)).append("\n");
        for (ExecutionStep step : executionSteps) {
            sb.append(step.toString()).append("\n");
        }
        sb.append("\n");

        // Add process completion summary
        sb.append("Process Completion Summary:\n");
        sb.append(String.format("%-12s%-18s%-18s%-18s\n", "Process", "Completion Time", "Waiting Time", "Turnaround Time"));
        sb.append("-".repeat(68)).append("\n");

        processes.sort(Comparator.comparingInt(p -> p.id));
        for (Main.Process p : processes) {
            sb.append(String.format("%-12s%-18d%-18d%-18d\n", "P" + p.id, p.finish, p.waiting, p.turnaround));
            totalWaiting += p.waiting;
            totalTurnaround += p.turnaround;
        }

        sb.append("-".repeat(68)).append("\n");
        sb.append(String.format("%-50s%.2f\n", "Average Waiting Time:", totalWaiting / processes.size()));
        sb.append(String.format("%-50s%.2f\n", "Average Turnaround Time:", totalTurnaround / processes.size()));
        sb.append(String.format("%-50s%d", "Total Execution Time:",
                processes.stream().mapToInt(p -> p.finish).max().orElse(0)));

        return sb.toString();
    }
}