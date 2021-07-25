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
import java.util.stream.Collectors;

@Slf4j
public class BlockingQueueMatcherAggregator implements MatcherAggregator {

    public static final String LINE_FORMAT = "%s --> %s";

    private final CompletionService<Map<String, Collection<LineMatch>>> completionService;

    public BlockingQueueMatcherAggregator(CompletionService<Map<String, Collection<LineMatch>>> completionService){
        this.completionService = completionService;
    }

    private Optional<Map<String, Collection<LineMatch>>> getResult(){
        //TODO consider timeout for this call to avoid starvation
        try {
            Future<Map<String, Collection<LineMatch>>> result = completionService.take();   //blocking
            return Optional.ofNullable(result.get());

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.info("issue occurred while processing", e);
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
                .map(entry -> String.format(LINE_FORMAT, entry.getKey(), entry.getValue().stream().filter(LineMatch.CONTAINS_MATCH).collect(Collectors.toList())))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
