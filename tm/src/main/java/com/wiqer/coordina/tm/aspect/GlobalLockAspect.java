package com.wiqer.coordina.tm.aspect;

import com.wiqer.coordina.tm.lock.GlobalLockManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

//PointLock模拟实现
@Aspect
@Component
public class GlobalLockAspect extends ReentrantLock  implements Ordered {
    private ConcurrentHashMap<String, ReentrantLock> prMap=new ConcurrentHashMap<>();

    private int times=0;
    public GlobalLockAspect(){
        super(true);
    }
    public static void main(String[] args) {


        GlobalLockAspect pointLock=new GlobalLockAspect();
        pointLock.lockTest();
    }
    public void lockTest(){
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Thread.currentThread().interrupt();
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    String key="PointLock.main";
                    ReentrantLock lock=null;
                    try {
                        if((lock=prMap.get(key))==null){
                            lock();
                            try {
                                if((lock=prMap.get(key))==null){
                                    lock= new ReentrantLock();
                                    lock.lock();
                                    ReentrantLock oldLock= prMap.put(key,lock);
                                    if(oldLock==null){
                                        System.out.println("没问题啊");
                                    }else {
                                        if (oldLock!=lock){
                                            System.out.println("锁被替换，不符合业务需求");
                                        }else {
                                            System.out.println("？？？");
                                        }
                                    }
                                }else {
                                    lock.lock();

                                }
                            }finally {
                                unlock();
                            }

                        }else {
                            lock.lock();
                            System.out.println("获取锁成功，准备排队阻塞");
                        }
                    }
                    finally {
                        lock.unlock();
                    }
                }
            }).start();
        }

    }

    private void pessimisticLock(String lockKey){
        ReentrantLock lock=null;

        if((lock=prMap.get(lockKey))==null){
            lock();
            try {
                if((lock=prMap.get(lockKey))==null){
                    lock= new ReentrantLock();
                    lock.lock();
                    ReentrantLock oldLock= prMap.put(lockKey,lock);
                    if(oldLock==null){
                        System.out.println("没问题啊");
                    }else {
                        if (oldLock!=lock){
                            System.out.println("锁被替换，不符合业务需求");
                        }else {
                            System.out.println("？？？");
                        }
                    }
                }else {
                    lock.lock();

                }
            }finally {
                unlock();
            }

        }else {
            lock.lock();
            System.out.println("获取锁成功，准备排队阻塞");
        }
            //执行位置

    }
    private void pessimisticUnlock(String lockKey){
        ReentrantLock lock=null;

        if((lock=prMap.get(lockKey))==null){
            throw new IllegalStateException("pessimisticUnlock传入了不存在的Key");
        }
        lock.unlock();
        //执行位置

    }
    private String distributedLock( String lockKey) throws InterruptedException {
        //同步请求Netty服务端/Redis/zk
        //锁id
        String lockId = UUID.randomUUID().toString().replace("-", "");
        GlobalLockManager.Lock(lockKey,lockId);
        return lockId;
    }
    private void distributedUnlock( String lockKey,String lockId){
        //同步请求Netty服务端/Redis/zk
        GlobalLockManager.Unlock(lockKey,lockId);
    }
    @Around("@annotation(com.wiqer.coordina.tm.annotation.GlobalLock)")
    public void invoke(ProceedingJoinPoint point) {
        // before
        String lockKeyGroup[]=  point.toLongString().split("[(]");
        String lockKey=lockKeyGroup[1].replace(")","");
        String  lockId="";
        try {

            pessimisticLock(lockKey);
            lockId =  distributedLock(lockKey);
            point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable.getMessage());
        } finally {
            pessimisticUnlock(lockKey);
            if(!"".equals(lockId)){
                distributedUnlock(lockKey,lockId);
            }
        }
    }
    //靠后执行
    @Override
    public int getOrder() {
        return 1001;
    }
}
