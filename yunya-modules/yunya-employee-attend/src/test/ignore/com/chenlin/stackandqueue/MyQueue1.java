package com.chenlin.stackandqueue;

import java.util.*;

/**
 * 简介：实现一种狗猫队列的结构，要求如下：
 *  用户可以调用 add 方法将 cat 类或 dog 类的实例放入队列中；
 *  用户可以调用 pollAll 方法，将队列中所有的实例按照进队列的先后顺序依次弹出；
 *  用户可以调用 pollDog 方法，将队列中 dog 类的实例按照进队列的先后顺序依次弹出；
 *  用户可以调用 pollCat 方法，将队列中 cat 类的实例按照进队列的先后顺序依次弹出；
 *  用户可以调用 isEmpty 方法，检查队列中是否还有 dog 或 cat 的实例；
 *  用户可以调用 isDogEmpty 方法，检查队列中是否有 dog 类的实例；
 *  用户可以调用 isCatEmpty 方法，检查队列中是否有 cat 类的实例
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/24 19:18
 * @since: 1.0.0
 */
public class MyQueue1 {
    public static void main(String[] args) {
        DogCatQueue dogCatQueue = new DogCatQueue();
        dogCatQueue.add(new Dog());
        dogCatQueue.add(new Cat());
        dogCatQueue.add(new Dog());
        dogCatQueue.add(new Cat());
        dogCatQueue.add(new Dog());
        dogCatQueue.add(new Cat());
        dogCatQueue.add(new Dog());
        dogCatQueue.add(new Cat());
        dogCatQueue.add(new Dog());
        System.out.println(dogCatQueue.isEmpty());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollDog());
        System.out.println(dogCatQueue.pollAll());
        System.out.println(dogCatQueue.pollCat());
        System.out.println(dogCatQueue.pollCat());
        System.out.println(dogCatQueue.isDogEmptry());
        System.out.println(dogCatQueue.isCatEmpty());
    }
}

class DogCatQueue {
    private Queue<PetEnterQueue> dogQ;
    private Queue<PetEnterQueue> catQ;
    private long sort;

    public DogCatQueue() {
        this.dogQ = new LinkedList<>();
        this.catQ = new LinkedList<>();
        this.sort = 0;
    }

    /**
     * 将 cat 类或 dog 类的实例放入队列中
     * @param pet
     */
    public void add(Pet pet) {
        String type= pet.getType();
        if ("dog".equals(type)) {
            dogQ.add(new PetEnterQueue(pet, sort++));
        } else if ("cat".equals(type)) {
            catQ.add(new PetEnterQueue(pet, sort++));
        } else {
            throw new RuntimeException("err, not dog or cat");
        }
    }

    /**
     * 将队列中所有的实例按照进队列的先后顺序依次弹出
     * @return
     */
    public Pet pollAll() {
        PetEnterQueue firstDog = dogQ.peek();
        PetEnterQueue firstCat = catQ.peek();
        if (firstDog!=null && firstCat!=null) {
            long dogTimestamp = firstDog.getTimestamp();
            long catTimestamp = firstCat.getTimestamp();
            if (dogTimestamp < catTimestamp) {
                return firstDog.getPet();
            } else {
                return firstCat.getPet();
            }
        } else if (firstDog!=null) {
            return firstDog.getPet();
        } else if (firstCat!=null) {
            return firstCat.getPet();
        } else {
            throw new RuntimeException("err, queue is empty!");
        }
    }

    /**
     * 将队列中dog类的实例按照进队列的先后顺序依次弹出；
     * @return
     */
    public Dog pollDog() {
        PetEnterQueue petEnterQueue = dogQ.poll();
        if (petEnterQueue == null) {
            throw new RuntimeException("err,dog queue is empty!");
        }
        return (Dog) petEnterQueue.getPet();
    }

    /**
     * 将队列中cat类的实例按照进队列的先后顺序依次弹出
     * @return
     */
    public Cat pollCat() {
        PetEnterQueue petEnterQueue = catQ.poll();
        if (petEnterQueue == null) {
            throw new RuntimeException("err,cat queue is empty!");
        }
        return (Cat) petEnterQueue.getPet();
    }

    /**
     * 检查队列中是否还有 dog 或 cat 的实例；
     * @return
     */
    public boolean isEmpty() {
        return dogQ.isEmpty()&&catQ.isEmpty();
    }

    /**
     * 检查队列中是否有 dog 类的实例
     * @return
     */
    public boolean isDogEmptry() {
        return dogQ.isEmpty();
    }

    /**
     * 检查队列中是否有 cat 类的实例
     * @return
     */
    public boolean isCatEmpty() {
        return catQ.isEmpty();
    }
}


class PetEnterQueue {
    private Pet pet;
    /** 进入队列的时间戳 */
    private long timestamp;

    public PetEnterQueue(Pet pet, long timestamp) {
        this.pet = pet;
        this.timestamp = timestamp;
    }

    public Pet getPet() {
        return pet;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

class Pet {
    private String type;

    public Pet(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

class Dog extends Pet {
    public Dog() {
        super("dog");
    }
}

class Cat extends Pet {
    public Cat() {
        super("cat");
    }
}