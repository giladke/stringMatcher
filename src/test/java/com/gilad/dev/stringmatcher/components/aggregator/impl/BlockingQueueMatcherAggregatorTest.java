package com.gilad.dev.stringmatcher.components.aggregator.impl;

import com.gilad.dev.stringmatcher.components.matcher.LineMatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockingQueueMatcherAggregatorTest {

    private final Map<String, Collection<LineMatch>> DATA = initData();

    CompletionService<Map<String, Collection<LineMatch>>> completionServiceMock;
    BlockingQueueMatcherAggregator aggregator;

    @BeforeAll
    public void setUp() throws InterruptedException, ExecutionException {
        completionServiceMock = Mockito.mock(CompletionService.class);
        mockTake(DATA);
        aggregator = new BlockingQueueMatcherAggregator(completionServiceMock);
    }


    @Test
    void asString_when_expectedMatchCount_0_expect_exception(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->aggregator.asString(0));
    }

    @Test
    void asString_when_expectedMatchCount_positive_expect_formatted_result(){
        String aggregationResult = aggregator.asString(2);

        Assertions.assertEquals(createExpectedFormatted(), aggregationResult);
    }

    private void mockTake(Map<String, Collection<LineMatch>> data) throws InterruptedException, ExecutionException {
        Future<Map<String, Collection<LineMatch>>> futureMock = Mockito.mock(Future.class);
        Mockito.doReturn(data).when(futureMock).get();
        Mockito.doReturn(futureMock).when(completionServiceMock).take();
    }


    //TODO find better solution in case parameter order is reversed
    private String createExpectedFormatted(){
        return "bb --> [[lineOffset=15, charOffset=34], [lineOffset=1, charOffset=3]]"+System.lineSeparator()+
                "aa --> [[lineOffset=1, charOffset=22]]";
    }

    private Map<String, Collection<LineMatch>> initData() {
        Set<LineMatch> matches = new HashSet<>();
        matches.add(new LineMatch(1, Set.of(22)));

        Set<LineMatch> matchesBB = new HashSet<>();
        matchesBB.add(new LineMatch(1, Set.of(3)));
        matchesBB.add(new LineMatch(15, Set.of(34)));
        return Map.of(
                "aa", matches,
                "bb", matchesBB
        );
    }
}