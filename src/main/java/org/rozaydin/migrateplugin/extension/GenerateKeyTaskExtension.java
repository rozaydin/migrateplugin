package org.rozaydin.migrateplugin.extension;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateKeyTaskExtension {

    private int validityInDays;
    private String issuer;
    private String relativePath;
    private String pkcs12File;
    private String password;

}
