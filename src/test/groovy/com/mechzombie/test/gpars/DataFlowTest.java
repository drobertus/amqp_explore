package com.mechzombie.test.gpars;

import groovyx.gpars.DataflowMessagingRunnable;
import groovyx.gpars.dataflow.Dataflow;
import groovyx.gpars.dataflow.DataflowQueue;
import groovyx.gpars.dataflow.operator.DataflowProcessor;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David on 11/11/2014.
 */
public class DataFlowTest {

    @Test
    public void testdataFlow() throws Exception {

        final DataflowQueue stream1 = new DataflowQueue();
        final DataflowQueue stream2 = new DataflowQueue();
        final DataflowQueue stream3 = new DataflowQueue();
        final DataflowQueue stream4 = new DataflowQueue();

        final DataflowProcessor op1 = Dataflow.selector(Arrays.asList(stream1), Arrays.asList(stream2), new DataflowMessagingRunnable(1)
        {
            @Override
            protected void doRun(final Object... objects) {
                System.out.println("op1 do Run " + objects.length);
                System.out.println("op " + objects[0]);
                getOwningProcessor().bindOutput(2 * (Integer) objects[0]);
            }
        });
        final List secondOperatorInput = Arrays.asList(stream2, stream3);
        final DataflowProcessor op2 = Dataflow.operator(secondOperatorInput, Arrays.asList(stream4), new DataflowMessagingRunnable(2) {
            @Override
            protected void doRun(final Object... objects) {
                System.out.println("op2 do Run " + objects.length);
                System.out.println("op 0: " + objects[0]);
                System.out.println("op 1: " + objects[1]);
                getOwningProcessor().bindOutput(((Integer) objects[0] * 2)  + (Integer) objects[1]);
            }
        });
        stream1.bind(1);
        stream1.bind(2);
        stream3.bind(100);
        stream1.bind(3);
        Thread.sleep(500);
        System.out.println("Result: " + stream4.getVal());

        stream3.bind(200);
        stream3.bind(300);
        Thread.sleep(500);
        System.out.println("Result: " + stream4.getVal());
        System.out.println("Result: " + stream4.getVal());

        //op1.stop();
        //op2.stop();
    }
}
