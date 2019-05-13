package com.siemens.plugins;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.rozaydin.migrateplugin.GenerateKeyTask;
import org.rozaydin.migrateplugin.MigrateTask;
import org.rozaydin.migrateplugin.ResetPasswordTask;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class MigratePluginTest {

    @Test
    public void should_be_able_to_add_migrate_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        Task task = project.getTasks().create("migrate", MigrateTask.class);
        assertTrue(task instanceof MigrateTask);
    }

    @Test
    public void should_be_able_to_add_generateKey_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        Task task = project.getTasks().create("generatekey", GenerateKeyTask.class);
        assertTrue(task instanceof GenerateKeyTask);
    }

    @Test
    public void should_be_able_to_add_resetPassword_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        Task task = project.getTasks().create("resetpassword", ResetPasswordTask.class);
        assertTrue(task instanceof ResetPasswordTask);
    }

}
