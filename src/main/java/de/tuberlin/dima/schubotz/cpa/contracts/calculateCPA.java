/*        __
 *        \ \
 *   _   _ \ \  ______
 *  | | | | > \(  __  )
 *  | |_| |/ ^ \| || |
 *  | ._,_/_/ \_\_||_|
 *  | |
 *  |_|
 * 
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <rob ∂ CLABS dot CC> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */
package de.tuberlin.dima.schubotz.cpa.contracts;

import eu.stratosphere.api.java.record.functions.ReduceFunction;
import eu.stratosphere.api.java.record.operators.ReduceOperator;
import eu.stratosphere.configuration.Configuration;
import eu.stratosphere.types.DoubleValue;
import eu.stratosphere.types.IntValue;
import eu.stratosphere.types.Record;
import eu.stratosphere.util.Collector;

import java.util.Iterator;

@ReduceOperator.Combinable
public class calculateCPA extends ReduceFunction {

    private Integer threshold;

    @Override
    public void open(Configuration parameter) throws Exception {
        super.open( parameter );
        threshold = parameter.getInteger("THRESHOLD", 1);
    }

    /*
    Schema
                    0 target.addField(linkTuple);
                    1 target.addField(recDistance);
                    2 target.addField(count);
     */
    private void internalReduce(Iterator<Record> iterator, Collector<Record> collector, Integer minOut) {
        Record rec = null;
        int cnt = 0;
        double recDistance = 0.;
        while (iterator.hasNext()) {
            rec = iterator.next();
            cnt += rec.getField(2, IntValue.class).getValue();
            recDistance += rec.getField(1, DoubleValue.class).getValue();
        }
        if (cnt > minOut) {
            assert rec != null;
            rec.setField(1, new DoubleValue(recDistance));
            rec.setField(2, new IntValue(cnt));
            collector.collect(rec);
        }
    }

    @Override
    public void reduce(Iterator<Record> iterator, Collector<Record> collector) {
        internalReduce(iterator, collector, threshold);
    }

    @Override
    public void combine(Iterator<Record> iterator, Collector<Record> collector) {
        internalReduce(iterator, collector, 0);
    }
}