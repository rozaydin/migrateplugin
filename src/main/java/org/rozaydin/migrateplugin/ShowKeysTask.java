package org.rozaydin.migrateplugin;

import org.rozaydin.migrateplugin.extension.MigrateTaskExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import static org.rozaydin.migrateplugin.Util.retrieveFiles;

public class ShowKeysTask extends DefaultTask {

    @TaskAction
    public void showKeys() {

        MigrateTaskExtension extension = getProject().getExtensions().findByType(MigrateTaskExtension.class);
        if (extension == null) {
            extension = new MigrateTaskExtension();
        }
        System.out.println("i am invoked!");
        //
        showKeys(getProject(), extension);

    }

    private void showKeys(Project project, MigrateTaskExtension extension) {

        String password = extension.getPassword();
        String relativePath = extension.getPath();

        // migrate to p12 format
        retrieveFiles(getProject().file(relativePath))
                .stream()
                .filter((file) -> file.getName().endsWith(".jks"))
                .filter(File::exists)
                .forEach(file -> showKey(file, password));

    }

    private void showKey(File jksFile, String password) {

        System.out.println("showing keys for " + jksFile.getName());

        try (FileInputStream fis = new FileInputStream(jksFile)) {
            //
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(fis, password.toCharArray());
            //
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {

                String alias = aliases.nextElement();
                System.out.println("listing certificate and privatekey for alias: " + alias);

                try {

                    KeyStore.Entry entry = keyStore.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
                    if (entry instanceof KeyStore.PrivateKeyEntry) {
                        System.out.println("Entry is of type PrivateKeyEntry");
                        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
                        Certificate certificate = ((KeyStore.PrivateKeyEntry) entry).getCertificate();
                        System.out.println(privateKey);
                    }
                    else {
                        System.out.println("Entry is not of type PrivateKeyEntry");
                        System.out.println(entry);
                    }

                } catch (Exception exc) {
                    System.out.println("Failed to display entry for alias: " + alias);
                    System.out.println(exc);
                }
            }

        } catch (Exception exc) {
            System.out.println("Failed to open jks file:" + jksFile.getName());
            System.out.println(exc);
        }
    }

}
