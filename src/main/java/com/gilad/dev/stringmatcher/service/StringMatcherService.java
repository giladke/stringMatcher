package com.gilad.dev.stringmatcher.service;

import java.io.IOException;
import java.nio.file.Path;


public interface StringMatcherService {
    void process(Path contentLocation) throws IOException;
}
