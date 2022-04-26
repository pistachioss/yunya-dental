package com.chenlin.stackandqueue;

import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 单调栈结构：单调递增或递减的栈结构
 * 简介：给定一个不含有重复值的数组 arr，找到每一个i位置左边和右边离i位置最近且值比arr[i]小的位置。返回所有位置相应的信息。
 *
 * 例如：arr = {3,4,1,5,6,2,7}
 * 返回如下二维数组作为结果：
 * {
 *  {-1, 2},
 *  { 0, 2},
 *  {-1,-1},
 *  { 2, 5},
 *  { 3, 5},
 *  { 2,-1},
 *  { 5,-1}
 * }
 * -1 表示不存在。所以上面的结果表示在 arr 中，
 *      0位置左边和右边离0位置最近且值比arr[0]小的位置是-1 和 2；
 *      1位置左边和右边离1位置最近且值比arr[1]小的位置是0 和 2；
 *      2位置左边和右边离2位置最近且值比arr[2]小的位置是-1 和-1……
 * 进阶问题：给定一个可能含有重复值的数组arr，找到每一个i位置左边和右边离i位置最
 * 近且值比arr[i]小的位置。返回所有位置相应的信息。
 *
 * 要求：如果arr长度为 N，实现原问题和进阶问题的解法，时间复杂度都达到O(N)。
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/20 15:03
 * @since: 1.0.0
 */
public class SingleStack {

    public static void main(String[] args) {
        int[] arr = {3,4,1,5,6,2,7};
//        int[][] res = rightWay(arr);
//        int[][] res = getNearLessNoRepeat(arr);
        int[][] res = getNearLess(arr);
        System.out.println(JSONObject.toJSON(res));
    }

    public static int[][] getNearLess(int[] arr) {
        int[][] res = new int[arr.length][2];
        Stack<List<Integer>> stack = new Stack<>();
        for (int i = 0; i < arr.length; i++) {
            while (!stack.isEmpty() && arr[stack.peek().get(0)] > arr[i]) {
                List<Integer> popIs = stack.pop();
                // 取位于下面位置的列表中，最晚加入的那个
                int leftLessIndex = -1;
                if (!stack.isEmpty()) {
                    leftLessIndex = stack.peek().get(stack.peek().size() - 1);
                }
                for (Integer popi : popIs) {
                    res[popi][0] = leftLessIndex;
                    res[popi][1] = i;
                }
            }
            if (!stack.isEmpty() && arr[stack.peek().get(0)] == arr[i]) {
                stack.peek().add(Integer.valueOf(i));
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(i);
                stack.push(list);
            }
        }

        while (!stack.isEmpty()) {
            List<Integer> popIs = stack.pop();
            // 取位于下面位置的列表中，最晚加入的那个
            int leftLessIndex = -1;
            if (!stack.isEmpty()) {
                leftLessIndex = stack.peek().get(stack.peek().size() - 1);
            }
            for (Integer popi : popIs) {
                res[popi][0] = leftLessIndex;
                res[popi][1] = -1;
            } }
        return res;
    }

    public static int[][] getNearLessNoRepeat(int[] arr) {
        int[][] res = new int[arr.length][2];
        // 存放数组的索引：先进后出
        Stack<Integer> stack = new Stack<>();

//      如果找到每一个i位置左边和右边离i位置最近且值比arr[i]小的位置，
//      那么需要让stack从栈顶到栈底的位置所代表的值是严格递减的；
        for (int i = 0; i < arr.length; i++) {
            while (!stack.isEmpty() && arr[stack.peek()]>arr[i]) {
                int popIndex = stack.pop();
                int leftLessIndex = stack.isEmpty()?-1:stack.peek();
                res[popIndex][0] = leftLessIndex;
                res[popIndex][1] = i;
            }
            stack.push(i);
        }

        //处理剩余索引
        while (!stack.isEmpty()) {
            int popIndex = stack.pop();
            int leftLessIndex = stack.isEmpty()?-1:stack.peek();
            res[popIndex][0] = leftLessIndex;
            res[popIndex][1] = -1;
        }
        return res;
    }

    /**
     * 时间复杂度O(N^2)
     * @param arr
     * @return
     */
    public static int[][] rightWay(int[] arr) {
        int[][] res = new int[arr.length][2];
        for (int i = 0; i < arr.length; i++) {
            //-1不存在
            int leftLessIndex = -1;
            int rightLessIndex = -1;

            //上一个值
            int cur = i - 1;
            while (cur >= 0) {
                // 当前值与上一个值比较，如果小于则记录下位置
                if (arr[cur] <arr[i]) {
                    leftLessIndex = cur;
                    break;
                }
                cur--;//向前追溯
            }

            //下一个值
            cur = i + 1;
            while (cur < arr.length) {
                if (arr[cur] < arr[i]) {
                    rightLessIndex = cur;
                    break;
                }
                cur++;//向后追溯
            }
            res[i][0] = leftLessIndex;
            res[i][1] = rightLessIndex;
        }
        return res;
    }
}
