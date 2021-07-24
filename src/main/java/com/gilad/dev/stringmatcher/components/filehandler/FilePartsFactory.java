package com.gilad.dev.stringmatcher.components.filehandler;

import com.gilad.dev.stringmatcher.config.AppConfig;
import com.gilad.dev.stringmatcher.model.FilePart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

public class FilePartsFactory {


    public static List<FilePart> toParts(Path bigFile, int maxPartSize) throws IOException {
        Stream<String> lines = Files.lines(bigFile);
        List<FilePart> parts = new LinkedList<>();
        Spliterator<String> split = lines.spliterator();

        boolean hasMoreElements = true;
        while (hasMoreElements) {
            List<String> chunk = new ArrayList<>(maxPartSize);
            for (int i = 0; i < maxPartSize; i++) {
                hasMoreElements = split.tryAdvance(chunk::add);
                if (!hasMoreElements) {
                    break;
                }
            }
            parts.add(new FilePart(chunk));
        }

        return parts;
    }

}
