package com.gilad.dev.stringmatcher.components.matcher;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class LineMatcher implements Matcher<LineMatch>{

    private final Set<String> text;

    public LineMatcher(Collection<String> text) {
        this.text = new HashSet<>(text);
    }


    @Override
    public Map<String, Collection<LineMatch>> match(String searchWord) {
        if(CollectionUtils.isEmpty(text)){
            return Collections.singletonMap(searchWord, Collections.emptySet());
        }

        Set<LineMatch> results = new HashSet<>();
        int i=0;
        for(String textLine: text){
            i++;
            Set<Integer> matches = findMatches(textLine, searchWord);
            if(! matches.isEmpty()) {
                results.add(new LineMatch(i, matches));
            }
        }

        return Collections.singletonMap(searchWord, results);
    }

    private static Set<Integer> findMatches(String text, String str) {
        if (! StringUtils.hasText(text) || ! StringUtils.hasText(str)) {
            return Collections.emptySet();
        }

        Set<Integer> indexes = new HashSet<>();
        int index = 0;
        while (true)
        {
            index = text.indexOf(str, index);
            if (index == -1) {
                break;
            }
            indexes.add(index);
            index += str.length();
        }

        return indexes;
    }
}
