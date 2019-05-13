package org.rozaydin.migrateplugin;

import org.rozaydin.migrateplugin.experiment.JKS;
import org.rozaydin.migrateplugin.extension.ResetPasswordTaskExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.rozaydin.migrateplugin.Util.determineOutputFile;
import static org.rozaydin.migrateplugin.Util.retrieveFiles;

public class ResetPasswordTask extends DefaultTask {

    @TaskAction
    public void resetPasswordTask() {

        ResetPasswordTaskExtension extension = getProject().getExtensions().findByType(ResetPasswordTaskExtension.class);
        if (extension == null) {
            extension = new ResetPasswordTaskExtension();
        }
        //
        final String password = extension.getPassword();
        final String relativePath = extension.getPath();

        // load all files from target directory
        retrieveFiles(getProject().file(relativePath))
                .stream()
                .filter((file) -> file.getName().endsWith(".jks"))
                .filter(file -> file.exists())
                .forEach(file -> resetPassword(file, password));
    }

    private void resetPassword(File file, String password) {

        JKS jks = new JKS();
        // load all files from relative directory
        final String resetedFileName = file.getName() + "_password_reset.jks";
        try (FileInputStream fis = new FileInputStream(file); FileOutputStream fos = new FileOutputStream(determineOutputFile(file, resetedFileName))) {
            //
            jks.engineLoad(fis, password.toCharArray());
            jks.engineStore(fos, password.toCharArray());
        } catch (Exception exc) {
            System.out.println("Failed to reset password for jks file: " + file.getName());
            System.out.println(exc);
        }
    }
}
