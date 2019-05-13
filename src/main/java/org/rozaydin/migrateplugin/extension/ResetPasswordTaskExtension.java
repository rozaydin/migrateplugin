package org.rozaydin.migrateplugin.extension;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordTaskExtension {
    private String path;
    private String password = "changeit";
}
