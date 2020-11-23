package com.yunya.modules.appointment;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2020-10-13 20:07
 **/
public class CustomizeThreadPool {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch1 = new CountDownLatch(1);

        ThreadFactory build = new ThreadFactoryBuilder().setNameFormat("线程%d-").build();
        ExecutorService pool = new ThreadPoolExecutor(3,
                4,
                0L,
                TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(3),
                build,
                new ThreadPoolExecutor.AbortPolicy());

        Future<User> submit = pool.submit(() -> {
            System.out.println(Thread.currentThread().getName() + "进入线程");
            User user1 = new User("张三", "123456789", 20);
            System.out.println(Thread.currentThread().getName() + "继续执行");
            System.out.println(Thread.currentThread().getName() + user1.toString());
            countDownLatch1.countDown();
            return user1;
        });

        countDownLatch1.await();
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        System.out.println("等待下一个1");
        Future<User> submit2 = pool.submit(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "进入线程");
                User user = submit.get();
                user.setUsername("李四");
                System.out.println(Thread.currentThread().getName() + "继续执行");
                System.out.println(Thread.currentThread().getName() + user.toString());
                countDownLatch2.countDown();
                return user;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });
        countDownLatch2.await();
        System.out.println("等待下一个2");
//        for (int i = 0; i<2;i++) {
            pool.submit(()->{
                try {
                    System.out.println(Thread.currentThread().getName() + "最后线程开始执行");
                    System.out.println("isCancelled=" + submit2.isCancelled());
                    System.out.println("isDone=" + submit2.isDone());
                    User user = submit2.get();
                    System.out.println(Thread.currentThread().getName() + user.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
//        }
        System.out.println("ss = " + pool.isShutdown());
        pool.shutdown();

    }

    static class User{
        private String username;
        private String telephone;
        private Integer age;

        public User(String username, String telephone, Integer age) {
            this.username = username;
            this.telephone = telephone;
            this.age = age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Integer getAge() {
            return age;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "User{" +
                    "username='" + username + '\'' +
                    ", telephone='" + telephone + '\'' +
                    ", age=" + age +
                    '}';
        }
    }



}
