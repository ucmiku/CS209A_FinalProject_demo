package cs209a.finalproject_demo.DataAnalysis.dto;

public enum IssueCategory {
    RACE_CONDITION("Race Condition"),
    DEADLOCK("Deadlock"),
    MEMORY_VISIBILITY("Memory Visibility / Volatile Issues"),
    THREADPOOL_MISUSE("ThreadPool / Executor Misuse"),
    CONCURRENT_MODIFICATION("Concurrent Modification Exception"),
    THREAD_INTERRUPTION("Thread Interruption Handling"),
    GENERAL_MULTITHREADING("General Multithreading Issue");

    private final String displayName;

    IssueCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}