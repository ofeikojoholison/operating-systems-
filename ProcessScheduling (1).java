/* Shruti Dabhi, Samuel Holison */
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

class ProcessThread extends Thread {
    int pid, burstTime;

    public ProcessThread(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
    }

    public void run() {
        System.out.println("[Process " + pid + "] started.");
        try {
            Thread.sleep(burstTime * 1000); // Simulate CPU burst
        } catch (InterruptedException e) {
            System.out.println("[Process " + pid + "] interrupted.");
        }
        System.out.println("[Process " + pid + "] finished.");
    }
}

// Dining Philosophers Section
class DiningPhilosopher extends Thread {
    private final int id;
    private final Lock leftFork;
    private final Lock rightFork;

    public DiningPhilosopher(int id, Lock leftFork, Lock rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void think() throws InterruptedException {
        System.out.println("[Philosopher " + id + "] Thinking...");
        Thread.sleep((int) (Math.random() * 2000));
    }

    private void eat() throws InterruptedException {
        System.out.println("[Philosopher " + id + "] Eating...");
        Thread.sleep((int) (Math.random() * 2000));
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                think();

                System.out.println("[Philosopher " + id + "] Waiting for forks...");
                Lock firstFork = (id % 2 == 0) ? leftFork : rightFork;
                Lock secondFork = (id % 2 == 0) ? rightFork : leftFork;

                firstFork.lock();
                try {
                    System.out.println("[Philosopher " + id + "] Picked up first fork.");
                    secondFork.lock();
                    try {
                        System.out.println("[Philosopher " + id + "] Picked up second fork.");
                        eat();
                    } finally {
                        secondFork.unlock();
                        System.out.println("[Philosopher " + id + "] Released second fork.");
                    }
                } finally {
                    firstFork.unlock();
                    System.out.println("[Philosopher " + id + "] Released first fork.");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[Philosopher " + id + "] was interrupted.");
        }
    }
}

// Readers-Writers Section
class ReadWriteLock {
    private int readers = 0;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean writing = false;

    public void startRead(int id) throws InterruptedException {
        lock.lock();
        try {
            while (writing) {
                System.out.println("[Reader " + id + "] waiting: writer active");
                condition.await();
            }
            readers++;
            System.out.println("[Reader " + id + "] starts reading.");
        } finally {
            lock.unlock();
        }
    }

    public void endRead(int id) {
        lock.lock();
        try {
            readers--;
            System.out.println("[Reader " + id + "] finished reading.");
            if (readers == 0) condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void startWrite(int id) throws InterruptedException {
        lock.lock();
        try {
            while (writing || readers > 0) {
                System.out.println("[Writer " + id + "] waiting: readers/writer active");
                condition.await();
            }
            writing = true;
            System.out.println("[Writer " + id + "] starts writing.");
        } finally {
            lock.unlock();
        }
    }

    public void endWrite(int id) {
        lock.lock();
        try {
            writing = false;
            System.out.println("[Writer " + id + "] finished writing.");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

class Reader extends Thread {
    private final int id;
    private final ReadWriteLock rwLock;

    public Reader(int id, ReadWriteLock rwLock) {
        this.id = id;
        this.rwLock = rwLock;
    }

    public void run() {
        try {
            Thread.sleep((int) (Math.random() * 3000));
            rwLock.startRead(id);
            Thread.sleep(1000); // simulate read time
            rwLock.endRead(id);
        } catch (InterruptedException ignored) {}
    }
}

class Writer extends Thread {
    private final int id;
    private final ReadWriteLock rwLock;

    public Writer(int id, ReadWriteLock rwLock) {
        this.id = id;
        this.rwLock = rwLock;
    }

    public void run() {
        try {
            Thread.sleep((int) (Math.random() * 3000));
            rwLock.startWrite(id);
            Thread.sleep(2000); // simulate write time
            rwLock.endWrite(id);
        } catch (InterruptedException ignored) {}
    }
}

public class ProcessScheduling {
    public static List<ProcessThread> readProcesses(String filename) {
        List<ProcessThread> threads = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    int pid = Integer.parseInt(parts[0]);
                    int burstTime = Integer.parseInt(parts[2]);
                    threads.add(new ProcessThread(pid, burstTime));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading process file: " + e.getMessage());
        }
        return threads;
    }

    public static void runDiningPhilosophers() {
        final int NUM_PHILOSOPHERS = 5;
        Lock[] forks = new Lock[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
        }

        DiningPhilosopher[] philosophers = new DiningPhilosopher[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            Lock left = forks[i];
            Lock right = forks[(i + 1) % NUM_PHILOSOPHERS];
            philosophers[i] = new DiningPhilosopher(i, left, right);
            philosophers[i].start();
        }

        try {
            Thread.sleep(10000); // Run simulation for a while
        } catch (InterruptedException ignored) {}

        for (DiningPhilosopher p : philosophers) {
            p.interrupt();
        }
    }

    public static void runReadersWriters() {
        ReadWriteLock rwLock = new ReadWriteLock();
        for (int i = 0; i < 3; i++) {
            new Reader(i + 1, rwLock).start();
        }
        for (int i = 0; i < 2; i++) {
            new Writer(i + 1, rwLock).start();
        }

        try {
            Thread.sleep(10000); // Let it run
        } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Choose simulation:");
        System.out.println("1. Process simulation from processes.txt");
        System.out.println("2. Dining Philosophers");
        System.out.println("3. Readers-Writers");
        System.out.print(">> ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                List<ProcessThread> processes = readProcesses("processes.txt");
                if (processes.isEmpty()) {
                    System.out.println("No processes found.");
                    return;
                }
                for (ProcessThread p : processes) {
                    p.start();
                }
                for (ProcessThread p : processes) {
                    try {
                        p.join();
                    } catch (InterruptedException ignored) {}
                }
                break;
            case 2:
                runDiningPhilosophers();
                System.out.println("Dining Philosophers simulation ended.");
                break;
            case 3:
                runReadersWriters();
                System.out.println("Readers-Writers simulation ended.");
                break;
            default:
                System.out.println("Invalid choice.");
        }

        sc.close();
    }
}
