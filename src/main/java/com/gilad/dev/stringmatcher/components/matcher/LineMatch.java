package com.gilad.dev.stringmatcher.components.matcher;

import lombok.Value;

import java.util.Set;

@Value
public class LineMatch {
    int lineOffset;
    Set<Integer> charOffset;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        charOffset.forEach(cOffset -> sb.append(String.format("[lineOffset=%d, charOffset=%d]", lineOffset, cOffset)));
        return sb.toString();
    }
}
