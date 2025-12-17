package cs209a.finalproject_demo.DataAnalysis;

import java.util.Set;

public enum JavaTopics {//用于topics到tags的映射

    GENERICS("generics"),
    COLLECTIONS("collections", "java-collections"),
    IO("io", "file-io", "nio","bufferedreader"),
    LAMBDA("lambda", "java-stream","stream"),
    MULTITHREADING("multithreading", "concurrency", "thread", "executorservice"),
    SPRING_BOOT("spring-boot","spring-data-jpa","spring-data-rest","spring"),
    REFLECTION("reflection"),
    SOCKET("socket", "networking","sockets"),
    TEST("junit5","testing");

    private final Set<String> tags;

    JavaTopics(String... tags) {
        this.tags = Set.of(tags);
    }

    public Set<String> getTags() {
        return tags;
    }
}

