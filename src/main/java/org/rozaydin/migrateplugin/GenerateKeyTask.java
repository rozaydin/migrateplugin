package org.rozaydin.migrateplugin;

import org.rozaydin.migrateplugin.extension.GenerateKeyTaskExtension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class GenerateKeyTask extends DefaultTask {

    @TaskAction
    public void generateKey() {

        Project project = getProject();
        GenerateKeyTaskExtension extension = getProject().getExtensions().findByType(GenerateKeyTaskExtension.class);
        if (extension == null) {
            extension = new GenerateKeyTaskExtension();
        }
        //

        int validityInDays = extension.getValidityInDays();
        String issuer = extension.getIssuer();
        String keystoreRelativePath = extension.getRelativePath();
        String pkcs12File = extension.getPkcs12File();
        char[] password = extension.getPassword().toCharArray();

        // 1. Check if file exists
        final File _pkcsOrigFile = project.file(keystoreRelativePath + "/" + pkcs12File);
        final File _pkcsDstFile = project.file(keystoreRelativePath + "/" + pkcs12File + "_updated");

        // 2. register bc provider
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try {
            // load key store
            final KeyStore pkcs12Store = KeyStore.getInstance("PKCS12");

            if (!_pkcsOrigFile.exists()) {
                pkcs12Store.load(null, null);
                System.out.println("keystore file does not exist creating it!");
            } else {
                try (FileInputStream fis = new FileInputStream(_pkcsOrigFile)) {
                    pkcs12Store.load(fis, password);
                }
            }

            // enumerate over keys ids
            String newAlias = determineKeyAlias(pkcs12Store.aliases());

            // Create new alias and store
            KeyPair keyPair = createKeyPair("RSA", 2048);
            X509Certificate certificate = createCertificate(new X500Principal(issuer), validityInDays, keyPair);

            // store in keystore file
            pkcs12Store.setKeyEntry(newAlias, keyPair.getPrivate(), password, new Certificate[]{certificate});

            // store file and close stream once we are done
            try (FileOutputStream fos = new FileOutputStream(project.file(_pkcsDstFile))) {
                pkcs12Store.store(fos, password);
            }

        } catch (Exception exc) {
            System.out.println("Failed to execute due to" + exc);
            System.out.println(exc);
        }
    }

    // utility methods
    private String determineKeyAlias(Enumeration<String> keyAliases) {

        int index = 0;
        List<String> aliases = new LinkedList<>();
        while (keyAliases.hasMoreElements()) {
            aliases.add(keyAliases.nextElement());
        }

        if (!aliases.isEmpty()) {
            // sort the collection and create a new alias
            Collections.sort(aliases);
            String alias = aliases.get(aliases.size() - 1);
            // key-id-1
            String[] tokens = alias.split("-");
            if (tokens.length == 3) {
                index = Integer.valueOf(tokens[2]);
            }
        }

        final String alias = "key-id-" + (index + 1);
        System.out.println("new keyId index is: " + alias);
        return alias;
    }


    private KeyPair createKeyPair(String algorithm, int keySize) throws Exception {

        // Public and Private Key Pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    private X509Certificate createCertificate(X500Principal issuer, int validityInDays, KeyPair issuerKP)
            throws Exception {

        // now and expiration
        Instant now = Instant.now();
        Date nbf = Date.from(now);
        Date exp = Date.from(now.plus(validityInDays, ChronoUnit.DAYS)); // 10 year

        // serial number
        BigInteger serialNo = BigInteger.valueOf(now.toEpochMilli());

        X509v3CertificateBuilder certificateBuilder = // self signed certificate
                new JcaX509v3CertificateBuilder(issuer, serialNo, nbf, exp, issuer, issuerKP.getPublic());

        return new JcaX509CertificateConverter()
                .getCertificate(
                        certificateBuilder.build(
                                new JcaContentSignerBuilder("SHA256WithRSA").build(issuerKP.getPrivate())));
    }

}
