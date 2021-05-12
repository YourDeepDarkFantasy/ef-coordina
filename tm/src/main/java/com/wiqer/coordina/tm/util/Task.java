package com.wiqer.coordina.tm.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author laowu
 * @date 2020/8/22 15:27
 * @desc 资源控制类
 */
public class Task {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public Lock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public void waitTask() {
        lock.lock();
        try {
            condition.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void signalTask() {
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
