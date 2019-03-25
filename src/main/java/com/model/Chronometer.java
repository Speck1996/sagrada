package com.model;

/**
 * A simple chronometer with multiple measurement intervals
 */
class Chronometer {

    private long pastTime;
    private long begin;

    /**
     * Starts a new measure.
     * If two o more start() are call in sequence, without a stop() call, a new interval begins and the time until the last call of this method will be lost.
     */
    void start() {
        begin = System.currentTimeMillis();
    }

    /**
     * Ends the current interval and store its time, adding to the previously time stored.
     * After a stop() call, a new measurement interval can ben start with a start() call.
     */
    void stop() {
        long end = System.currentTimeMillis();
        pastTime += end-begin;
    }

    //return the time in minutes

    /**
     * Returns the time in minutes.
     * The total time is the sum of the time of all the intervals completed.
     * @return the time in minutes
     */
    double getTime() {
        return pastTime/60000.0;
    }

}
