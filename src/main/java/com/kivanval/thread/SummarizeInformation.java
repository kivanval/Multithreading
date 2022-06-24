package com.kivanval.thread;

import java.util.Collection;
import java.util.StringJoiner;

public class SummarizeInformation {
    private Collection<String> minWords;
    private long min;

    private Collection<String> maxWords;
    private long max;

    private long count;

    public Collection<String> getMinWords() {
        return minWords;
    }

    public void setMinWords(Collection<String> minWords) {
        this.minWords = minWords;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public Collection<String> getMaxWords() {
        return maxWords;
    }

    public void setMaxWords(Collection<String> maxWords) {
        this.maxWords = maxWords;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SummarizeInformation.class.getSimpleName() + "[", "]")
                .add("minWords=" + minWords)
                .add("min=" + min)
                .add("maxWords=" + maxWords)
                .add("max=" + max)
                .add("count=" + count)
                .toString();
    }
}
