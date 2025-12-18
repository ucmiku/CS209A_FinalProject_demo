package cs209a.finalproject_demo.DataAnalysis;

import java.util.Set;

public enum JavaTopics {//用于topics到tags的映射

    GENERICS("generics"),
    COLLECTIONS("collections", "java-collections","iterator","filter","nullpointerexception"),
    STREAM("stream","inputstream","outputstream","java-8","lambda","java-stream"),
    IO("io", "file-io", "nio","bufferedreader"),
    MULTITHREADING("multithreading", "concurrency", "thread", "executorservice"),
    SPRING("spring-boot","spring-data-jpa","spring-data-rest","spring"),
    REFLECTION("reflection","terminology"),
    SOCKET("socket", "networking","sockets"),
    JAVAEE("javaee","java-ee-6","jakarta-ee"),
    TEST("junit5","testing","junit");

    private final Set<String> tags;

    JavaTopics(String... tags) {
        this.tags = Set.of(tags);
    }

    public Set<String> getTags() {
        return tags;
    }
}

