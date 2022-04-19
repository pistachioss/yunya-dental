package com.chenlin.stackandqueue;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 生成窗口最大值数组
 *
 * 简介：有一个整型数组 arr 和一个大小为 w 的窗口从数组的最左边滑到最右边，窗口每次向右边
 * 滑一个位置。
 * 例如，数组为[4,3,5,4,3,3,6,7]，窗口大小为 3 时：
 * [4 3 5] 4 3 3 6 7 窗口中最大值为 5
 * 4 [3 5 4] 3 3 6 7 窗口中最大值为 5
 * 4 3 [5 4 3] 3 6 7 窗口中最大值为 5
 * 4 3 5 [4 3 3] 6 7 窗口中最大值为 4
 * 4 3 5 4 [3 3 6] 7 窗口中最大值为 6
 * 4 3 5 4 3 [3 6 7] 窗口中最大值为 7
 * 如果数组长度为 n，窗口大小为 w，则一共产生 n-w+1 个窗口的最大值。
 * 请实现一个函数。
 *       输入：整型数组 arr，窗口大小为 w。
 *       输出：一个长度为 n-w+1 的数组 res，res[i]表示每一种窗口状态下的最大值。
 * 以本题为例，结果应该返回{5,5,5,4,6,7}
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/20 13:56
 * @since: 1.0.0
 */
public class MyDeque {

    public static void main(String[] args) {
        int[] arr = {4,3,5,4,3,3,6,7};
        int[] res = getMaxWindowArr(arr, 3);
        System.out.println(Arrays.toString(res));
    }

    /**
     * 获取arr中被w框住元素中的最大值
     *  利用双端队列来实现窗口最大值的更新
     *  每个下标值最多进qmax一次，出qmax一次。所以遍历的过程中进出双端队列的操作是时间复杂度为 O(N)，整体的时间复杂度也为 O(N)。
     * @param arr
     * @param w
     * @return
     */
    public static int[] getMaxWindowArr(int[] arr, int w) {
        if (arr==null || w<1 || arr.length<w) {
            return null;
        }

        // 存放arr中的下标，维护窗口为w的子数组的最大值更新
        Deque<Integer> qmax = new LinkedList<>();
        int[] res = new int[arr.length - w + 1];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            //从qmax尾端开始遍历，如果qmax指向值小于当前值，则从尾端移除掉
            while (!qmax.isEmpty() && arr[qmax.peekLast()]<=arr[i]) {
                qmax.pollLast();
            }
            //直到qmax指向值大于当前值，则将当前索引添加到qmax尾端
            qmax.addLast(i);
            //qmax队头元素（索引） == i-w时，qmax队头的下标已过期，需要移除头元素。（清除上次最大值）
            if (qmax.peekFirst() == i-w) {
                qmax.pollFirst();
            }
            //如果当前索引i+1 >= w时，则将qmax头元素指向的值添加到res
            if (i+1 >= w) {
                res[index++] = arr[qmax.peekFirst()];
            }
        }
        return res;
    }
}
