package com.chenlin.stackandqueue;

import java.util.Stack;

/**
 * 简介：一个栈中元素的类型为整型，现在想将该栈从顶到底按从大到小的顺序排序，只许申请一
 * 个栈。除此之外，可以申请新的变量，但不能申请额外的数据结构。如何完成排序？
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/19 20:23
 * @since: 1.0.0
 */
public class MyStack3 {

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 5; i > 0; i--) {
            stack.push(i);
        }
        sortStackByStack(stack);
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }

    /**
     * 用一个栈实现另一个栈的排序
     *  使用辅助栈对原栈中的所有数据进行过滤（顺序），将符合要求的元素重新压入到原来的栈，而不符号要求的元素压入到辅助栈，最终将辅助栈的数据压入原栈。
     *  边比较边分离。
     * @param stack 待排序的栈
     */
    public static void sortStackByStack(Stack<Integer> stack) {
        /** 辅助栈 */
        Stack<Integer> help = new Stack<>();
        while (!stack.isEmpty()) {
            int cur = stack.pop();
            //将辅助栈中所有小于当前栈顶元素的值全部挪到原栈中
            while (!help.isEmpty() && cur>help.peek()) {
                int value = help.pop();
                stack.push(value);
            }
            //将栈顶元素压入辅助栈
            help.push(cur);
        }
        while (!help.isEmpty()) {
            stack.push(help.pop());
        }
    }
}
