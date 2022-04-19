package com.chenlin.stackandqueue;

import java.util.Stack;

/**
 * 简介：实现一个特殊的栈，在实现栈的基本功能的基础上，再实现返回栈中最小元素的操作。
 * 【要求】
 * 1．pop、push、getMin 操作的时间复杂度都是 O(1)。
 * 2．设计的栈类型可以使用现成的栈结构。
 *
 * 方案一和方案二其实都是用 stackMin 栈保存着 stackData 每一步的最小值。共同点是所有
 * 操作的时间复杂度都为 O(1)、空间复杂度都为 O(n)。区别是：方案一中 stackMin 压入时稍省空
 * 间，但是弹出操作稍费时间；方案二中 stackMin 压入时稍费空间，但是弹出操作稍省时间
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/24 15:53
 * @since: 1.0.0
 */
public class MyStack1 {
    public static void main(String[] args) {
        MyStack1.Stack2 stack = new MyStack1.Stack2();
        stack.push(1);
        stack.push(2);
        stack.push(1);
        stack.push(5);
        stack.push(4);
        stack.push(3);

        for (int i = 0; i < 5; i++) {
            System.out.println(stack.getMin());
            stack.pop();
        }
    }

    /**
     * 方案1
     */
    static class Stack1 {
        /** 数据容器 */
        private Stack<Integer> stackData;
        /** 最小值容器：保存每次添加时的最小值 */
        private Stack<Integer> stackMin = new Stack<>();

        public Stack1() {
            stackData = new Stack<>();
            stackMin = new Stack<>();
        }

        /**
         * 添加元素到栈顶，如果小于等于最小值则添加stackMin
         * @param newNum
         */
        public void push(int newNum) {
            if (stackMin.isEmpty() || newNum <=this.getMin()) {
                stackMin.push(newNum);
            }
            stackData.push(newNum);
        }

        /**
         * 取出栈顶元素，如果栈顶元素等于stackMin的栈顶元素时，将stackMin栈顶元素移除。
         * @return
         */
        public int pop() {
            if (stackData.isEmpty()) {
                throw new RuntimeException("Your stack is empty!");
            }
            int value = stackData.pop();//取出栈顶元素
            if (getMin() == value) {
                stackMin.pop();
            }
            return value;
        }

        public int getMin() {
            if (stackMin.isEmpty()) {
                throw new RuntimeException("Your stack is empty!");
            }
            return stackMin.peek();//获取栈顶元素
        }

        public int size() {
            return stackData.size();
        }
    }

    /**
     * 方案2
     */
    static class Stack2 {
        /** 数据容器 */
        private Stack<Integer> stackData;
        /** 最小值容器：保存每次添加时的最小值 */
        private Stack<Integer> stackMin = new Stack<>();

        public Stack2() {
            stackData = new Stack<>();
            stackMin = new Stack<>();
        }

        /**
         * 添加元素到栈顶，如果stackMin为空或新值小于最小值则添加到stackMin栈顶，否则将stackMin栈顶元素拷贝一份再添加到stackMin
         * @param newNum
         */
        public void push(int newNum) {
            if (stackMin.isEmpty() || newNum<getMin()) {
                stackMin.push(newNum);
            } else {
                int minNum = getMin();
                stackMin.push(minNum);
            }
            stackData.push(newNum);
        }

        /**
         * 获取当前最小值
         * @return
         */
        public int getMin() {
            if (stackMin.isEmpty()) {
                throw new RuntimeException("Your stack is empty!");
            }
            return stackMin.peek();
        }

        /**
         * 取出栈顶元素，同时移除当前最小值
         * @return
         */
        public int pop() {
            if (stackData.isEmpty()) {
                throw new RuntimeException("Your stack is empty!");
            }
            stackMin.pop();
            return stackData.pop();
        }
    }
}