package org.wikipedia.citolytics.seealso.better;

import com.google.common.collect.Ordering;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.wikipedia.citolytics.seealso.types.SeeAlsoEvaluationResult;
import org.wikipedia.citolytics.seealso.types.WikiSimComparableResult;
import org.wikipedia.citolytics.seealso.types.WikiSimComparableResultList;
import org.wikipedia.citolytics.seealso.utils.EvaluationMeasures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EvaluateSeeAlso implements CoGroupFunction<
        Tuple2<String, ArrayList<String>>,
        Tuple2<String, WikiSimComparableResultList<Double>>,
        SeeAlsoEvaluationResult
        > {

    private int topK = 10;
    private boolean enableMRR = false; // If true, MRR is used instead of MAP

    public EvaluateSeeAlso(int topK) {
        this.topK = topK;
    }

    public EvaluateSeeAlso(int topK, boolean enableMRR) {
        this.topK = topK;
        this.enableMRR = enableMRR;
    }

    @Override
    public void coGroup(Iterable<Tuple2<String, ArrayList<String>>> a, Iterable<Tuple2<String, WikiSimComparableResultList<Double>>> b, Collector<SeeAlsoEvaluationResult> out) throws Exception {
        Iterator<Tuple2<String, ArrayList<String>>> iteratorA = a.iterator();
        Iterator<Tuple2<String, WikiSimComparableResultList<Double>>> iteratorB = b.iterator();

        if (iteratorA.hasNext()) {
            Tuple2<String, ArrayList<String>> recordA = iteratorA.next();
            List<String> seeAlsoList = recordA.getField(1);

            List<WikiSimComparableResult<Double>> sortedList = new ArrayList<>();

            double topKScore = 0;
            double hrr = 0;
            double performance = 0;

            int[] matches = new int[]{0, 0, 0};

            if (iteratorB.hasNext()) {
                Tuple2<String, WikiSimComparableResultList<Double>> recordB = iteratorB.next();

                sortedList = Ordering.natural().greatestOf((WikiSimComparableResultList<Double>) recordB.getField(1), topK);

                List<String> resultList = getResultNamesAsList(sortedList);

                topKScore = EvaluationMeasures.getTopKScore(resultList, seeAlsoList);
                hrr = EvaluationMeasures.getHarmonicReciprocalRank(resultList, seeAlsoList);

                if (enableMRR) {
                    performance = EvaluationMeasures.getMeanReciprocalRank(resultList, seeAlsoList);
                } else {
                    performance = EvaluationMeasures.getMeanAveragePrecision(resultList, seeAlsoList);
                }

                matches = EvaluationMeasures.getMatchesCount(resultList, seeAlsoList);
            }

            out.collect(new SeeAlsoEvaluationResult(
                    (String) recordA.getField(0),
                    (ArrayList<String>) recordA.getField(1),
                    ((ArrayList<String>) recordA.getField(1)).size(),
                    new WikiSimComparableResultList<Double>(sortedList),
                    sortedList.size(),
                    hrr,
                    topKScore,
                    performance,
                    matches[0],
                    matches[1],
                    matches[2]
            ));
        }
    }

    public static List<String> getResultNamesAsList(List<WikiSimComparableResult<Double>> sortedList) {
        List<String> resultList = new ArrayList<>();
        Iterator<WikiSimComparableResult<Double>> iterator = sortedList.listIterator();

        while (iterator.hasNext()) {
            resultList.add(iterator.next().getName());
        }
        return resultList;
    }
}
