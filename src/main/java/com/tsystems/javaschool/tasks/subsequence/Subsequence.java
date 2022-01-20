package com.tsystems.javaschool.tasks.subsequence;

import java.util.List;

public class Subsequence {

    /**
     * Checks if it is possible to get a sequence which is equal to the first
     * one by removing some elements from the second one.
     *
     * @param x first sequence
     * @param y second sequence
     * @return <code>true</code> if possible, otherwise <code>false</code>
     */
    @SuppressWarnings("rawtypes")
    public boolean find(List x, List y) {
        // TODO: Implement the logic here
        if (x == null || y == null) throw new IllegalArgumentException();
        if (x.isEmpty()) return true;
        boolean flag = true;
        int prevInnerIndex = 0;
//        first
        for (int i = 0; i < x.size(); i++) {
            boolean innerFlag = false;
            for (int j = prevInnerIndex; j < y.size(); j++) {
                if (x.get(i).equals(y.get(j))) {
                    innerFlag = true;
                    prevInnerIndex = j + 1;
                    break;
                }
            }
            if (!innerFlag) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
