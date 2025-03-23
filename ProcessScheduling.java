/* Shruti Dabhi, Samuel Holison */
import java.io.*;
import java.util.*;

class Process {
    int pid, arrivalTime, burstTime, priority, waitingTime, turnaroundTime;

    public Process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }
}

public class ProcessScheduling {
    
    // Function to read processes from file
    public static List<Process> readProcesses(String filename) {
        List<Process> processes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+"); // Split by space or tab
                if (parts.length == 4) {
                    int pid = Integer.parseInt(parts[0]);
                    int arrivalTime = Integer.parseInt(parts[1]);
                    int burstTime = Integer.parseInt(parts[2]);
                    int priority = Integer.parseInt(parts[3]);
                    processes.add(new Process(pid, arrivalTime, burstTime, priority));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return processes;
    }

    // First-Come, First-Served (FCFS) Scheduling
    public static void fcfsScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrivalTime) {
                time = p.arrivalTime; // Wait until the process arrives
            }
            p.waitingTime = time - p.arrivalTime;
            p.turnaroundTime = p.waitingTime + p.burstTime;
            time += p.burstTime;
        }
        displayResults("FCFS", processes);
    }

    // Shortest Job First (SJF) Scheduling
    public static void sjfScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.burstTime)); // Sort by burst time
        
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrivalTime) {
                time = p.arrivalTime;
            }
            p.waitingTime = time - p.arrivalTime;
            p.turnaroundTime = p.waitingTime + p.burstTime;
            time += p.burstTime;
        }
        displayResults("SJF", processes);
    }

    // Priority Scheduling
    public static void priorityScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.priority)); // Sort by priority (lower value = higher priority)
        
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrivalTime) {
                time = p.arrivalTime;
            }
            p.waitingTime = time - p.arrivalTime;
            p.turnaroundTime = p.waitingTime + p.burstTime;
            time += p.burstTime;
        }
        displayResults("Priority Scheduling", processes);
    }

    // Function to display results
    public static void displayResults(String algorithm, List<Process> processes) {
        System.out.println("\n" + algorithm + " Scheduling:");
        System.out.println("PID | Waiting Time | Turnaround Time");
        
        int totalWT = 0, totalTAT = 0;
        for (Process p : processes) {
            System.out.println(p.pid + "   | " + p.waitingTime + "            | " + p.turnaroundTime);
            totalWT += p.waitingTime;
            totalTAT += p.turnaroundTime;
        }
        System.out.println("Average Waiting Time: " + (float) totalWT / processes.size());
        System.out.println("Average Turnaround Time: " + (float) totalTAT / processes.size());
    }

    public static void main(String[] args) {
        List<Process> processes = readProcesses("sample_input.txt"); // Read from file
        
        if (processes.isEmpty()) {
            System.out.println("No processes found in the file!");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose Scheduling Algorithm:");
        System.out.println("1. FCFS");
        System.out.println("2. SJF");
        System.out.println("3. Priority");
        System.out.print(">> ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                fcfsScheduling(processes);
                break;
            case 2:
                sjfScheduling(processes);
                break;
            case 3:
                priorityScheduling(processes);
                break;
            default:
                System.out.println("Invalid choice!");
        }
        scanner.close();
    }
}
