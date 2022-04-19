package com.chenlin.stackandqueue;

import java.util.Stack;

/**
 * 简介：汉诺塔问题比较经典，这里修改一下游戏规则：现在限制不能从最左侧的塔直接移动到最
 * 右侧，也不能从最右侧直接移动到最左侧，而是必须经过中间。求当塔有 N 层的时候，打印最
 * 优移动过程和最优移动总步数。
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/19 20:59
 * @since: 1.0.0
 */
public class MyStack4 {
    /** 左柱标识 */
    private static final String LEFT_PILLAR = "left";
    /** 中柱 */
    private static final String MID_PILLAR = "mid";
    /** 右柱 */
    private static final String RIGHT_PILLAR = "right";

    public static void main(String[] args) {
        //当前左柱上第10层塔想要移动到右柱上
        int stepCount = hanoiProblem1(10, "left", "right");
        System.out.println("本次移动一共花费了" + stepCount + "步");

        int step = hanoiProblem2(5);
        System.out.println("本次移动一共花费了" + step + "步");
    }

    /**
     * 递归的终止条件（顶层塔移动的打印过程）如下：
     * 左 -> 中：打印 Move num from left to mid
     * 中 -> 左：打印 Move num from mid to left
     * 中 -> 右：打印 Move num from mid to right
     * 右 -> 中：打印 Move num from right to mid
     * 左 -> 右：打印 Move num from left to mid  和  Move num from mid to right
     * 右 -> 左：打印 Move num from right to mid  和  Move num from mid to left
     *
     * 从上至下（1-N）N层塔
     * 1、全在左柱上，需要全部移动到中柱上：
     *  1、先将1到N-1层塔全部从左 -> 右，交给递归过程
     *  2、将第N层塔从左->中
     *  3、再将1到N-1层塔全部从右 -> 中，交给递归过程
     * 2、全在中柱上，需要全部移动到左柱上的过程与情况1同理
     * 3、全在左柱上，需要全部移动到右柱上：
     *  1、先将1到N-1层塔全部从左 -> 右，交给递归过程
     *  2、将第N层塔从左->中
     *  3、再将1到N-1层塔全部从右 -> 左，交给递归过程
     *  4、将第N层塔从中->右
     *  5、将1到N-1层塔全部从左 -> 右，交给递归过程
     * 4、全在右柱上，需要全部移动到左柱上的过程与情况2同理
     * @param num 当前塔所在层数
     * @param from 当前塔所在柱
     * @param to 当前塔将要移动到目标柱
     * @return 移动总共需要花费多少步
     */
    public static int hanoiProblem1(int num, String from, String to) {
        if (num < 1) {//无效值
            return 0;
        }
        return process(num, from, to);
    }

    /**
     * 递归处理
     * @param num 第几层塔
     * @param from 当前塔所在柱
     * @param to 当前塔将要移动到目标柱
     * @return
     */
    private static int process(int num, String from, String to) {
        //递归终止条件
        if (num == 1) {
            if (from.equals(MID_PILLAR) || to.equals(MID_PILLAR)) {
                System.out.println("Move 1 from " + from + " to " + to);
                return 1;
            } else {
                System.out.println("Move 1 from " + from + " to " + MID_PILLAR);
                System.out.println("Move 1 from " + MID_PILLAR + " to " + to);
                return 2;
            }
        }

        // 当前塔如果在中柱上或者目标柱是中柱。即要移动到中柱或从中柱移动到其他柱
        if (from.equals(MID_PILLAR) || to.equals(MID_PILLAR)) {
            //确定其他塔需要先为最上层塔让路，需要移动到哪个目标柱。
            String othersTo = LEFT_PILLAR;
            //当前柱在左柱或者目标柱在左柱
            if (from.equals(LEFT_PILLAR) || to.equals(LEFT_PILLAR)) {
                othersTo = RIGHT_PILLAR;
            }
            //第一步：将1至num-1塔，从当前塔移动到他们的目标塔
            int part1 = process(num - 1, from, othersTo);
            //第二步：将第num层塔从当前塔移动到最终目标塔
            int part2 = 1;
            System.out.println("Move " + num + " from " + from + " to " + to);
            //第三步：将第1至num-1层塔，从他们所在的塔移动到最终目标塔
            int part3 = process(num - 1, othersTo, to);
            return part1 + part2 + part3;
        } else {
            //当前塔在右柱上或者目标柱在右柱
            //第一步：将1至num-1层塔，从当前塔移动到最终目标柱
            int part1 = process(num - 1, from, to);
            int part2 = 1;
            System.out.println("Move " + num + " from " + from + " to " + MID_PILLAR);
            int part3 = process(num - 1, to, from);
            int part4 = 1;
            System.out.println("Move " + num + " from " + MID_PILLAR + " to " + to);
            int part5 = process(num - 1, from, to);
            return part1 + part2 + part3 + part4 + part5;
        }
    }

    /**
     * 使用栈模拟3个柱子LS、MS、RS，使用他们的入栈出栈来模拟移动过程：
     *  汉诺塔问题：
     *   1、主要涉及4个动作：
     *      1、左->中：LS.pop出栈 + MS.push入栈（L2M）
     *      2、中->左：MS.pop出栈 + LS.push入栈（M2L）
     *      3、中->右：MS.pop出栈 + RS.push入栈（M2R）
     *      4、右->中：RS.pop出栈 + MS.push入栈（R2M）
     *   2、L2M与M2L过程是互为逆过程；M2R与R2M互为逆过程
     *   3、不违反小值压大值原则：即两个相邻动作一定不能相同，比如当前动作是L2M，那么下一个动作一定不能是L2M；
     *   4、最小步数原则：任何两个相邻的动作都不是互为逆过程。即L2M的上一个或下一个动作不能是M2L
     *  非递归方法的核心结论：
     *      1、第一个动作一定是L2M。
     *      2、在走出最少步数过程中的任何时刻，4个动作中只有一个动作不违反小压大和相邻不可逆原则，另外三个动作一定都会违反。
     *      所以每一步只会有一个动作（4个动作之一）达标（根据2原则考查4个动作），哪个动作达标就走哪个。
     * @param num
     * @return
     */
    public static int hanoiProblem2(int num) {
        Stack<Integer> ls = new Stack<>();
        Stack<Integer> ms = new Stack<>();
        Stack<Integer> rs = new Stack<>();
        ls.push(Integer.MAX_VALUE);
        ms.push(Integer.MAX_VALUE);
        rs.push(Integer.MAX_VALUE);
        //先将1至num层塔倒序压入栈ls
        for (int i=num; i>0; i--) {
            ls.push(i);
        }

        //初始步骤
        Action[] record = {Action.NO};
        int step = 0;
        //rs未填满时移动
        while (rs.size() != num+1) {
            //假设前一步的动作是 L->M：
            //  1．根据小压大原则，L->M 的动作不会重复发生。
            //  2．根据相邻不可逆原则，M->L 的动作也不该发生。
            //  3．根据小压大原则，M->R 和 R->M 只会有一个达标。
            //假设前一步的动作是 M->L：
            //  1．根据小压大原则，M->L 的动作不会重复发生。
            //  2．根据相邻不可逆原则，L->M 的动作也不该发生。
            //  3．根据小压大原则，M->R 和 R->M 只会有一个达标。
            //假设前一步的动作是 M->R：
            //  1．根据小压大原则，M->R 的动作不会重复发生。
            //  2．根据相邻不可逆原则，R->M 的动作也不该发生。
            //  3．根据小压大原则，L->M 和 M->L 只会有一个达标。
            //假设前一步的动作是 R->M：
            //  1．根据小压大原则，R->M 的动作不会重复发生。
            //  2．根据相邻不可逆原则，M->R 的动作也不该发生。
            //  3．根据小压大原则，L->M 和 M->L 只会有一个达标。
//            综上所述，每一步只会有一个动作达标。那么只要每走一步都根据这两个原则考查所有的
//            动作就可以，哪个动作达标就走哪个动作，反正每次都只有一个动作满足要求，按顺序走下来
//            即可。
            step += fromStack2Stack(record, Action.M2L, Action.L2M, ls, ms, LEFT_PILLAR, MID_PILLAR);
            step += fromStack2Stack(record, Action.L2M, Action.M2L, ms, ls, MID_PILLAR, LEFT_PILLAR);
            step += fromStack2Stack(record, Action.R2M, Action.M2R, ms, rs, MID_PILLAR, RIGHT_PILLAR);
            step += fromStack2Stack(record, Action.M2R, Action.R2M, rs, ms, RIGHT_PILLAR, MID_PILLAR);
        }
        return step;
    }

    /**
     * 从源栈（柱子）移动到目标栈
     * @param record 存储上一个执行的实际动作。
     * @param preNoAct 指定的上一个动作
     * @param nowAct 当前动作
     * @param fromStack 动作源栈
     * @param toStack 动作目标栈
     * @param from 当前所在柱
     * @param to 目标柱
     * @return 步数
     */
    private static int fromStack2Stack(Action[] record, Action preNoAct, Action nowAct, Stack<Integer> fromStack,
                                       Stack<Integer> toStack, String from, String to) {
        //指定的上一个动作不是实际的上个动作，并且动作源栈顶值小于目标栈顶值时，将源栈顶元素压入目标栈中。并更新存储动作
        if (record[0] != preNoAct && fromStack.peek()<toStack.peek()) {
            toStack.push(fromStack.pop());
            System.out.println("Move " + toStack.peek() + " from " + from + " to " + to);
            record[0] = nowAct;
            return 1;
        }
        return 0;
    }
}

enum Action {
    NO, L2M, M2L, M2R, R2M
}
