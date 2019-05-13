package org.rozaydin.migrateplugin;

import org.rozaydin.migrateplugin.extension.MigrateTaskExtension;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

import static org.rozaydin.migrateplugin.Util.determineOutputFile;
import static org.rozaydin.migrateplugin.Util.retrieveFiles;

@Slf4j
@Getter
@Setter
public class MigrateTask extends DefaultTask {

    @TaskAction
    public void migrateTask() {

        MigrateTaskExtension extension = getProject().getExtensions().findByType(MigrateTaskExtension.class);
        if (extension == null) {
            extension = new MigrateTaskExtension();
        }
        //
        migrate(getProject(), extension);
    }

    private void migrate(Project project, MigrateTaskExtension extension) {

        String password = extension.getPassword();
        String relativePath = extension.getPath();

        // migrate to p12 format
        retrieveFiles(getProject().file(relativePath)).stream().filter((file) -> file.getName().endsWith(".jks"))
                .filter(file -> file.exists())
                .forEach(file -> migrateJksFileToP12Format(file, password));

    }

    private void migrateJksFileToP12Format(final File jksFile, final String password) {

        // convert to p12
        System.out.println("Converting " + jksFile.getName() + " file to p12 format: ");
        final String p12fileName = jksFile.getName() + ".p12";
        final File p12File = determineOutputFile(jksFile, p12fileName);

        try (FileInputStream fis = new FileInputStream(jksFile); FileOutputStream fos = new FileOutputStream(p12File)) {
            //
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(fis, password.toCharArray());
            //
            KeyStore p12keyStore = KeyStore.getInstance("PKCS12");
            p12keyStore.load(null, null);

            // load certificate and private_key
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                //
                String alias = aliases.nextElement();
                System.out.println("trying to migrate entry with alias: " + alias);

                try {
                    Key privateKey = keyStore.getKey(alias, password.toCharArray());
                    Certificate cert = keyStore.getCertificate(alias);
                    // store in p12
                    p12keyStore.setKeyEntry(alias, privateKey, password.toCharArray(), new Certificate[]{cert});
                    System.out.println("migrated entry " + alias);
                } catch (Exception exc) {
                    System.out.println("Failed to migrate entry: " + alias);
                    System.out.println(exc);
                }
            }

            // store in p12
            p12keyStore.store(fos, password.toCharArray());

        } catch (Exception exc) {
            log.error("Failed to migrate {} file!", jksFile.getName(), exc);
        }
        //
        System.out.println("-------------------------------------");
    }

}
