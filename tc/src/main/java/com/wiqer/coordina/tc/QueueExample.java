package com.wiqer.coordina.tc;

import java.util.LinkedList;
import java.util.Queue;

public class QueueExample {
    public static void main(String[] args)
    {
        Queue<Integer> q
                = new LinkedList<>();

        // 给q增加元素 {0, 1, 2, 3, 4}
        for (int i = 0; i < 5; i++)
            q.add(i);

        // 显示队列
        System.out.println("Elements of queue "
                + q);

        // 移除队列头
        int removedele = q.remove();
        System.out.println("removed element-"
                + removedele);

        System.out.println(q);

        // 查看队列头
        int head = q.peek();
        System.out.println("head of queue-"
                + head);

        // 也继承了Collection接口的一些方法，如size()
        int size = q.size();
        System.out.println("Size of queue-"
                + size);
    }
}
