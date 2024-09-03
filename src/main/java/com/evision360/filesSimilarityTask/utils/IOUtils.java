package com.evision360.filesSimilarityTask.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class IOUtils {

    /**
     *
     * @param filePath path of the file.
     * @return String representes the trimmed content of the file.
     * @throws IOException
     */
    public static String readTrimmedFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath)).trim();
    }

    /**
     *
     * @param directory directory path.
     * @return List of paths of the files inside the directory.
     * @throws IOException
     */
    public static List<String> getDirectoryFiles(String directory) throws IOException {
        return Files.walk(Path.of(directory)).filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
    }

}
