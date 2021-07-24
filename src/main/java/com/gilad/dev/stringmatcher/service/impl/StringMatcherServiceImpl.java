package com.gilad.dev.stringmatcher.service.impl;

import com.gilad.dev.stringmatcher.components.aggregator.MatcherAggregator;
import com.gilad.dev.stringmatcher.components.aggregator.impl.BlockingQueueMatcherAggregator;
import com.gilad.dev.stringmatcher.components.filehandler.FilePartsFactory;
import com.gilad.dev.stringmatcher.components.filehandler.PartProperties;
import com.gilad.dev.stringmatcher.components.matcher.LineMatcher;
import com.gilad.dev.stringmatcher.components.matcher.Matcher;
import com.gilad.dev.stringmatcher.components.resourceloader.ResourceLoader;
import com.gilad.dev.stringmatcher.config.AppConfig;
import com.gilad.dev.stringmatcher.model.FilePart;
import com.gilad.dev.stringmatcher.components.matcher.LineMatch;
import com.gilad.dev.stringmatcher.service.StringMatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class StringMatcherServiceImpl implements StringMatcherService {

    private final int maxPartSize;
    private final ResourceLoader resourceLoader;

    @Autowired
    public StringMatcherServiceImpl(PartProperties partProperties, ResourceLoader resourceLoader) throws MalformedURLException {
        this.maxPartSize = partProperties.getMaxPartSize();
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void process(Path contentDestination) throws IOException {
        Set<String> searchWords = readSearchWords(AppConfig.RESOURCE_NAME);

        List<FilePart> parts = FilePartsFactory.toParts(contentDestination, maxPartSize);

        //for production ready solution this will go into some external queue

        //runSingleThread(searchWords, parts);
        runMultiThreaded(searchWords, parts);
    }

    private void runMultiThreaded(Set<String> searchWords, List<FilePart> parts) {
        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(searchWords.size());
            CompletionService<Map<String, Collection<LineMatch>>> completionService = new ExecutorCompletionService<>(executorService);

            List<Callable<Map<String, Collection<LineMatch>>>> callables = addToExecutionQueue(searchWords, parts, completionService);
            executorService.invokeAll(callables);

            aggregateResultsToLog(callables.size(), completionService);
        } catch (InterruptedException e) {
            log.info("exception has occurred, ", e);
            Thread.currentThread().interrupt();
        } finally {
            if(executorService != null){
                executorService.shutdown();
            }
        }
    }

    private void aggregateResultsToLog(int numberOfTasks, CompletionService<Map<String, Collection<LineMatch>>> completionService) {
        MatcherAggregator matcherAggregator = new BlockingQueueMatcherAggregator(completionService);
        log.info("aggregation results: {}", matcherAggregator.asString(numberOfTasks));
    }

    private List<Callable<Map<String, Collection<LineMatch>>>> addToExecutionQueue(Set<String> searchWords, List<FilePart> parts, CompletionService<Map<String, Collection<LineMatch>>> completionService) {
        List<Callable<Map<String, Collection<LineMatch>>>> callables = new ArrayList<>();
        for(FilePart part: parts){
            LineMatcher matcher = new LineMatcher(part.getContent());
            for(String sWord: searchWords){
                Callable<Map<String, Collection<LineMatch>>> task = new TextMatchingTask(matcher, sWord);
                completionService.submit(task);
                callables.add(task);
            }
        }
        return callables;
    }


    private Set<String> readSearchWords(String fileName) throws IOException {
        String resourceFileAsString = resourceLoader.getResourceAsString(fileName);
        return StringUtils.commaDelimitedListToSet(resourceFileAsString);
    }


    private void runSingleThread(Set<String> searchWords, List<FilePart> parts) {
        Map<String, Collection<LineMatch>> results = new HashMap<>();
        for(FilePart part: parts){
            Matcher<LineMatch> matcher = new LineMatcher(part.getContent());
            for (String sWord : searchWords){
                results.putAll(matcher.match(sWord));
            }
            //log.info("done with part - results {}", results);
            boolean hasMatch = results.values().stream()
                    .anyMatch(matches -> matches.stream().anyMatch(match -> match.getCharOffset().size() > 0));
            if(hasMatch) {
                log.info("has match");
            }
        }
        log.info("done");
    }
}
