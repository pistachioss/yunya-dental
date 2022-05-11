package com.chenlin.stackandqueue;

import java.util.List;
import java.util.Stack;

/**
 * 简介：编写一个类，用两个栈实现队列，支持队列的基本操作（add、poll、peek）。
 *  栈的特点是先进后出，而队列的特点是先进先出。我们用两个栈正好能把顺序反过来实现
 * 类似队列的操作
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/24 16:41
 * @since: 1.0.0
 */
public class TwoStackQueue {
    /** 压入栈 */
    private Stack<Integer> stackPush;
    /** 弹出栈 */
    private Stack<Integer> stackPop;

    public TwoStackQueue() {
        stackPush = new Stack<>();
        stackPop = new Stack<>();
    }

    public void push2Pop() {
        if (stackPop.isEmpty()) {// stackPop为空则说明当前还没进行反向存储。否则已经存储过了不需要再来一遍了。
            while (!stackPush.isEmpty()) {
                stackPop.push(stackPush.pop());
            }
        }
    }

    /**
     * 压入元素到stackPush
     * @param newNum
     */
    public void add(int newNum) {
        stackPush.push(newNum);
//        push2Pop();
    }

    /**
     * 弹出栈时，先从stackPush取出栈顶元素（最后添加的），将其重新添加到stackPop，直到stackPush为空时，stackPop则反向的存储了一遍。（此过程必须一次性完成）
     * @return
     */
    public int poll() {
        if (stackPop.empty() && stackPush.empty()) {
            throw new RuntimeException("Queue is empty!");
        }
        push2Pop();
        return stackPop.pop();
    }

    public int peek() {
        if (stackPop.empty() && stackPush.empty()) {
            throw new RuntimeException("Queue is empty!");
        }
        push2Pop();
        return stackPop.peek();
    }

    public static void main(String[] args) {
        TwoStackQueue twoStackQueue = new TwoStackQueue();
        twoStackQueue.add(1);
        twoStackQueue.add(2);
        twoStackQueue.add(3);
        twoStackQueue.add(4);
        twoStackQueue.add(5);
        twoStackQueue.add(6);

        for (int i = 0; i < 6; i++) {
            System.out.println(twoStackQueue.poll());
        }
    }
}
