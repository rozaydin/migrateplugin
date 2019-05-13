package org.rozaydin.migrateplugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static List<File> retrieveFiles(File targetFile) {
        return targetFile.isDirectory() ? Arrays.asList(targetFile.listFiles()) : Arrays.asList(targetFile);
    }

    public static File determineOutputFile(File inputFile, String suffix) {
        Path outputFilePath = inputFile.toPath();
        return outputFilePath.getParent().resolve(suffix).toFile();
    }
}
