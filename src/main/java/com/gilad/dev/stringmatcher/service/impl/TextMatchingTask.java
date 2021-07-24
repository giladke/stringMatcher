package com.gilad.dev.stringmatcher.service.impl;

import com.gilad.dev.stringmatcher.components.matcher.LineMatcher;
import com.gilad.dev.stringmatcher.components.matcher.LineMatch;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

public class TextMatchingTask implements Callable<Map<String, Collection<LineMatch>>> {

    private final LineMatcher matcher;
    private final String searchWord;

    public TextMatchingTask(LineMatcher matcher, String searchWord){
        this.matcher = matcher;
        this.searchWord = searchWord;
    }

    @Override
    public Map<String, Collection<LineMatch>> call() throws Exception {
        return matcher.match(searchWord);
    }
}
