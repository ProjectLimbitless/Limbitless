package com.example.limbitlesssummerproject19;

import java.util.LinkedList;
import java.util.Queue;

/**
 * File: MovingAverage.java
 *
 *
 * This class computes a moving average from the streamed hardware sensor data.
 *
 */
public class MovingAverage {
    private final Queue<float[]> Dataset = new LinkedList<float[]>();
    /**
     * period is the size of the Dataset (in this case we have a set of 20 values being
     * average all together
     */
    private final float period;
    private float[] sum = new float[3];

    /**
     * Function: constructor
     * Purpose: initialize a moving average object with a specified queue size
     * Parameters: float period = the size of the queue (larger queue size = more accurate averages)
     * Return: none
     */
    public MovingAverage(float period) {
        this.period = period;
    }


    /**
     * Function: addData()
     * Purpose: adds new hardware sensor data to the moving average queue
     * Parameters: float[] vector = sensor data to be added to the moving average
     * Return: none
     */
    public void addData(float[] vector) {
        sum[0] += vector[0];
        sum[1] += vector[1];
        sum[2] += vector[2];

        if(vector[0] == 0.0 && vector[1] == 0.0 && vector[2]== 0.0) {

        } else {
            Dataset.add(vector);
        }

        /** subtract the values about to be removed (if movingAverage is full*/
        if(Dataset.size() > period) {
            float[] ret = Dataset.remove();
            sum[0] -= ret[0];
            sum[1] -= ret[1];
            sum[2] -= ret[2];
        }
    }


    /**
     * Function: getAverage()
     * Purpose: takes the average of the values inside the queue
     * Parameters: none
     * Return: float[] = the average position value over all values in the queue
     */
    public float[] getAverage() {
        float[] answer = new float[3];
        if(Dataset.size() < period) {
            answer[0] = sum[0]/ (float) Dataset.size();
            answer[1] = sum[1]/ (float) Dataset.size();
            answer[2] = sum[2]/ (float) Dataset.size();
        } else {
            answer[0] = sum[0] / period;
            answer[1] = sum[1] / period;
            answer[2] = sum[2] / period;
        }

        return answer;
    }
}
