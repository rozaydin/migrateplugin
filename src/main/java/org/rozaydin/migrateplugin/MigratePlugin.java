package org.rozaydin.migrateplugin;

import org.rozaydin.migrateplugin.extension.MigrateTaskExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MigratePlugin implements Plugin<Project> {

    public void apply(Project project) {
         MigrateTaskExtension extension = project.getExtensions().create("migrate", MigrateTaskExtension.class);
         project.getTasks().create("migrate", MigrateTask.class);
         project.getTasks().create("showkeys", ShowKeysTask.class);
         project.getTasks().create("resetpassword", ResetPasswordTask.class);
         project.getTasks().create("generatekey", GenerateKeyTask.class);
    }

}
