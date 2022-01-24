package com.tsystems.javaschool.tasks.pyramid;

import java.util.Iterator;
import java.util.List;

public class PyramidBuilder {

    /**
     * Builds a pyramid with sorted values (with minumum value at the top line and maximum at the bottom,
     * from left to right). All vacant positions in the array are zeros.
     *
     * @param inputNumbers to be used in the pyramid
     * @return 2d array with pyramid inside
     * @throws {@link CannotBuildPyramidException} if the pyramid cannot be build with given input
     */
    public int[][] buildPyramid(List<Integer> inputNumbers) {
        // TODO : Implement your solution here

        if (inputNumbers.contains(null)) throw new CannotBuildPyramidException();

        int[] shape = calculateShape(inputNumbers.size());
        int depth = shape[0];

        int[][] result = new int[shape[0]][shape[1]];
        try {
            inputNumbers.sort(Integer::compare);
        } catch (Throwable e) {
            throw new CannotBuildPyramidException();
        }
        Iterator<Integer> iterator = inputNumbers.iterator();
        for (int i = 0; i < depth; i++) {
            for (int j = depth-i-1; j <= depth+i-1; j+=2) {
                result[i][j] = iterator.next();
            }
        }

        return result;
    }

    private int[] calculateShape(int arraySize) {
        int sum = 0;
        int width = 0;
        int depth = 0;
        for (int i = 1; i < arraySize; i++) {
            sum += i;
            if (sum > arraySize) throw new CannotBuildPyramidException();
            if (sum == arraySize) {
                depth = i;
                width = 2*i-1;
                break;
            }
        }
        return new int[]{depth, width};
    }
}
