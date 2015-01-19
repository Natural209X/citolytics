package de.tuberlin.dima.schubotz.cpa.evaluation.operators;

import de.tuberlin.dima.schubotz.cpa.evaluation.types.ListResult;
import de.tuberlin.dima.schubotz.cpa.evaluation.types.WikiSimComparableResult;
import de.tuberlin.dima.schubotz.cpa.types.list.StringListValue;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.shaded.com.google.common.collect.MinMaxPriorityQueue;
import org.apache.flink.types.StringValue;
import org.apache.flink.util.Collector;

import java.util.Iterator;

/**
 * Transform result set into EvaluationResult format:
 * <p/>
 * article name | array( result1, result2, ... ) | number of results
 *
 * @param <IN> input format (Tuple)
 */
public class ListBuilder<SORT extends Comparable, IN extends Tuple> implements GroupReduceFunction<IN, ListResult> {
    int maxListLength;

    public ListBuilder(int maxListLength) {
        this.maxListLength = maxListLength;
    }

    @Override
    public void reduce(Iterable<IN> results, Collector<ListResult> out) throws Exception {
        Iterator<IN> iterator = results.iterator();
        IN record = null;

        StringListValue resultList = new StringListValue();

        // Convert to ComparableResult, add to SortedList with fixed length
        MinMaxPriorityQueue<WikiSimComparableResult<SORT>> queue = MinMaxPriorityQueue.maximumSize(maxListLength).create();


        while (iterator.hasNext()) {
            record = iterator.next();
            queue.add(new WikiSimComparableResult<SORT>((String) record.getField(0), (String) record.getField(1), (SORT) record.getField(2)));
        }

        // Add to ResultList
        for (WikiSimComparableResult<SORT> item : queue) {
            resultList.add(new StringValue((String) item.getField(1)));
        }

        out.collect(new ListResult((String) record.getField(0), resultList, resultList.size()));
    }
}
