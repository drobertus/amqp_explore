package com.mechzombie.test.gpars;


import groovyx.gpars.MessagingRunnable;
import groovyx.gpars.actor.DynamicDispatchActor;
import groovyx.gpars.agent.Agent;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Created by David on 11/11/2014.
 */
public class AnonActorsTest {

    String setResponse = null;

    @Test
    public void testStatelessActor() throws Exception {


        final MyStatelessActor actor = new MyStatelessActor();
        actor.start();
        actor.send("Hello");
        actor.sendAndWait(10);
        actor.sendAndContinue(10.0, new MessagingRunnable<String>() {
            @Override protected void doRun(final String s) {
                System.out.println("Received a reply " + s);
                setResponse = s;
            }
        });

        Thread.sleep(100);
        actor.stop();

        assertEquals("Thank you 3", setResponse);
    }

    class MyStatelessActor extends DynamicDispatchActor {
        public void onMessage(final String msg) {
            System.out.println("Received " + msg);
            replyIfExists("Thank you 1");
        }
        public void onMessage(final Integer msg) {
            System.out.println("Received a number " + msg);
            replyIfExists("Thank you 2");
        }
        public void onMessage(final Object msg) {
            System.out.println("Received an object " + msg);
            replyIfExists("Thank you 3");
        }
    }

    @Test
    public void testAgent() throws Exception {
        final Agent counter = new Agent<Integer>(0);


        System.out.println("Current value: " + counter.getVal());
        counter.send(10);
        System.out.println("Current value: " + counter.getVal());
        counter.send(new MessagingRunnable<Integer>() {
            @Override protected void doRun(final Integer integer) {
                System.out.println("performing update " + integer);
                counter.updateValue(integer + 1);
            }
        });
        System.out.println("Current value: " + counter.getVal());

    }


    @Test
    public void testAgent2() throws Exception {
        final Agent counter = new Agent<SimpleTester>(new SimpleTester(0));


        System.out.println("Current value: " + counter.getVal());
        counter.send(10);
        System.out.println("Current value: " + counter.getVal());
        counter.send(new MessagingRunnable<Integer>() {
            @Override protected void doRun(final Integer integer) {
                System.out.println("performing update " + integer);
                counter.updateValue(integer + 1);
            }
        });
        System.out.println("final Current value: " + counter.getVal());
        counter.updateValue("Eat me");

        System.out.println("final 2 Current value: " + counter.getVal());
    }

    class SimpleTester {
        public SimpleTester(Integer i) {
            System.out.println("set val = " + i);
        }


    }
}
