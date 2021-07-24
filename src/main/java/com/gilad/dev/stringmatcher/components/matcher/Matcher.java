package com.gilad.dev.stringmatcher.components.matcher;

import java.util.Collection;
import java.util.Map;

public interface Matcher<T> {
    Map<String, Collection<T>> match(String line);
}
