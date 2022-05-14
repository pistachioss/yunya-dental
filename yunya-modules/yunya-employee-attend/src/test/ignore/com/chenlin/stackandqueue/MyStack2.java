package com.chenlin.stackandqueue;

import io.swagger.models.auth.In;
import org.omg.CORBA.INTERNAL;

import java.util.Stack;

/**
 * 简介：一个栈依次压入 1、2、3、4、5，那么从栈顶到栈底分别为 5、4、3、2、1。将这个栈转置
 * 后，从栈顶到栈底为 1、2、3、4、5，也就是实现栈中元素的逆序，但是只能用递归函数来实现，不能用其他数据结构。
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/24 17:24
 * @since: 1.0.0
 */
public class MyStack2 {

    /**
     * 递归：
     *  1、递归本质是一系列相同方法组成的双向方法链条，有调用链和回返链。
     *  2、必须有返回点和递进点
     *  3、组成递归的所有方法中的变量可以利用起来存储数据
     *  4、递进点之前的操作是顺序的，递进点之后的操作是逆序的。
     *
     * 通过递归的方法回返形成的反向调用链表。
     *
     * 将栈stack的栈底元素返回并移除
     * 先递归的取出栈顶元素，并在递归时，使用方法中的变量暂存每个元素。
     * 当找到栈底元素时，方法回返，将其他元素重新压入栈。并返回栈底元素。
     * @return
     */
    public static int getAndRemoveLastElement(Stack<Integer> stack) {
        int result = stack.pop();
        if (stack.isEmpty()) {
            return result;//栈底元素
        }
        int last = getAndRemoveLastElement(stack);
        stack.push(result);
        return last;
    }

    /**
     * 元素逆序
     *
     * 通过递归方法，从栈底部取出（反向取出）所有元素，并用递归方法的变量暂存起来。
     * 当所有元素都已经反向取出时，方法回返，并将取出的元素重新压入栈。
     * @param stack
     */
    public static void reverse(Stack<Integer> stack) {
       if (stack.isEmpty()) {
           return;
       }
       int last = getAndRemoveLastElement(stack);
       reverse(stack);
       stack.push(last);
    }


    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);// 栈底元素
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);// 栈顶元素

//        System.out.println(getAndRemoveLastElement(stack));
        reverse(stack);
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
}
