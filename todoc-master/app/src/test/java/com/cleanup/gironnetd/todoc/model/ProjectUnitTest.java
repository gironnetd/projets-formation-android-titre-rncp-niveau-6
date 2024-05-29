package com.cleanup.gironnetd.todoc.model;

import com.cleanup.gironnetd.todoc.model.Task;

import org.junit.Test;

import java.util.Date;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProjectUnitTest {

    @Test
    public void test_projects() {
        final Task task1 = new Task(1, 1, "task 1", new Date().getTime());
        final Task task2 = new Task(2, 2, "task 2", new Date().getTime());
        final Task task3 = new Task(3, 3, "task 3", new Date().getTime());
        final Task task4 = new Task(4, 4, "task 4", new Date().getTime());
        assertEquals("Projet Tartampion", Objects.requireNonNull(task1.getProject()).getName());
        assertEquals("Projet Lucidia", Objects.requireNonNull(task2.getProject()).getName());
        assertEquals("Projet Circus", Objects.requireNonNull(task3.getProject()).getName());
        assertNull(task4.getProject());
    }
}
