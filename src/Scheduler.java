import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Scheduler {
    static class SchedulingResult {
        String output;
        List<GanttChartPanel.GanttEntry> ganttEntries;

        SchedulingResult(String output, List<GanttChartPanel.GanttEntry> ganttEntries) {
            this.output = output;
            this.ganttEntries = ganttEntries;
        }
    }

    public static SchedulingResult runNonPreemptive(List<Main.Process> input) {
        List<Main.Process> processes = new ArrayList<>(input);
        List<GanttChartPanel.GanttEntry> ganttEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder("Non-Preemptive Priority Scheduling\n\n");

        processes.sort(Comparator.comparingInt(p -> p.arrival));
        int time = 0;
        List<Main.Process> completed = new ArrayList<>();

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
            ganttEntries.add(new GanttChartPanel.GanttEntry(current.id, time, time + current.burst));
            time += current.burst;
            current.finish = time;
            current.turnaround = time - current.arrival;
            current.waiting = current.turnaround - current.burst;
            completed.add(current);
            processes.remove(current);
        }

        return new SchedulingResult(generateOutput(completed, sb), ganttEntries);
    }

    public static SchedulingResult runPreemptive(List<Main.Process> input) {
        List<Main.Process> processes = new ArrayList<>();
        for (Main.Process p : input) {
            processes.add(new Main.Process(p.id, p.arrival, p.burst, p.priority));
        }
        List<GanttChartPanel.GanttEntry> ganttEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder("Preemptive Priority Scheduling\n\n");

        int time = 0;
        int completedCount = 0;
        Main.Process current = null;
        List<Main.Process> readyQueue = new ArrayList<>();
        int lastProcessId = -1;
        int startTime = 0;

        while (completedCount < processes.size()) {
            for (Main.Process p : processes) {
                if (p.arrival == time) readyQueue.add(p);
            }
            readyQueue.sort(Comparator.comparingInt(p -> p.priority));

            if (!readyQueue.isEmpty()) {
                Main.Process next = readyQueue.get(0);
                if (current != next && current != null && lastProcessId != -1) {
                    ganttEntries.add(new GanttChartPanel.GanttEntry(lastProcessId, startTime, time));
                }
                current = next;
                lastProcessId = current.id;
                startTime = time;
                current.remaining--;
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

        return new SchedulingResult(generateOutput(processes, sb), ganttEntries);
    }

    private static String generateOutput(List<Main.Process> processes, StringBuilder sb) {
        double totalWaiting = 0, totalTurnaround = 0;

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
        sb.append(String.format("%-50s%.2f", "Average Turnaround Time:", totalTurnaround / processes.size()));

        return sb.toString();
    }
}