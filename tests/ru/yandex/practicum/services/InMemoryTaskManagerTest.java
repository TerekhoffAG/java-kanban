package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }
}