package com.gilad.dev.stringmatcher.components.aggregator.impl;

import com.gilad.dev.stringmatcher.components.aggregator.MatcherAggregator;
import com.gilad.dev.stringmatcher.components.matcher.LineMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class BlockingQueueMatcherAggregator implements MatcherAggregator {

    private static final Predicate<Collection<LineMatch>> HAS_MATCHES = matches -> matches.stream().anyMatch(match-> match.getCharOffset().size() > 0);

    private final CompletionService<Map<String, Collection<LineMatch>>> completionService;
    public static final String LINE_FORMAT = "%s --> %s";

    public BlockingQueueMatcherAggregator(CompletionService<Map<String, Collection<LineMatch>>> completionService){
        this.completionService = completionService;
    }

    private Optional<Map<String, Collection<LineMatch>>> getResult(){

        try {
            Future<Map<String, Collection<LineMatch>>> result = completionService.take();
            return Optional.ofNullable(result.get());

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.info("issue occurred while processing");
            return Optional.empty();
        }
    }

    @Override
    public String asString(int expectedMatchCount) {
        if(expectedMatchCount <= 0){
            log.info("invalid input");
            throw new IllegalArgumentException("invalid input");
        }

        Map<String, Collection<LineMatch>> results = new HashMap<>(expectedMatchCount);
        for(int i=0; i<expectedMatchCount ; i++){
            Optional<Map<String, Collection<LineMatch>>> matchResult = getResult();
            if(matchResult.isPresent()){
                matchResult.get().forEach(
                        (searchWord, matchResults) -> {
                            if (results.containsKey(searchWord)) {
                                results.get(searchWord).addAll(matchResults);
                            } else {
                                results.put(searchWord, matchResults);
                            }
                        });
            }else {
                log.warn("something happened during matcher execution");
            }
        }

        return results.entrySet().stream()
                .map(entry -> String.format(LINE_FORMAT, entry.getKey(), entry.getValue().stream().filter(match -> match.getCharOffset().size() > 0).collect(Collectors.toList())))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
