package com.gilad.dev.stringmatcher.components.matcher;

import lombok.Value;

import java.util.Set;
import java.util.function.Predicate;

@Value
public class LineMatch {

    public static final Predicate<LineMatch> CONTAINS_MATCH = match -> match.getCharOffset().size() > 0;

    int lineOffset;
    Set<Integer> charOffset;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        charOffset.forEach(cOffset -> sb.append(String.format("[lineOffset=%d, charOffset=%d]", lineOffset, cOffset)));
        return sb.toString();
    }
}
