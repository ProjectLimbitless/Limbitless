package com.example.limbitlesssummerproject19.camera;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {
    private final Queue<float[]> Dataset = new LinkedList<float[]>();
    // period is the size of the Dataset (in this case we have a set of 20 values being
    // average all together
    private final float period;
    private float[] sum = new float[3];

    public MovingAverage(float period) {
        this.period = period;
    }

    public void addData(float[] vector) {
        sum[0] += vector[0];
        sum[1] += vector[1];
        sum[2] += vector[2];

        if(vector[0] == 0.0 && vector[1] == 0.0 && vector[2]== 0.0) {

        } else {
            Dataset.add(vector);
        }

        if(Dataset.size() > period) {
            //subtract the values about to be removed
            float[] ret = Dataset.remove();
            sum[0] -= ret[0];
            sum[1] -= ret[1];
            sum[2] -= ret[2];
        }
    }

    // We take the average of the values and set it to answers. Then we return the array of
    // average answers. Returns and array of floats
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
