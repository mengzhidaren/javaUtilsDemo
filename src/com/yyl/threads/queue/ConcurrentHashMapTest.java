package com.yyl.threads.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

/**
 * ConcurrentHashMap--锁的分段技术
 * ConcurrentHashMap是Java 5中支持高并发、高吞吐量的线程安全HashMap实现。
 * <p>
 * <p>
 * HashTable容器在竞争激烈的并发环境下表现出效率低下的原因，是因为所有访问HashTable的线程都必须竞争同一把锁，那假如容器里有多把锁，每一把锁用于锁容器其中一部分数据，
 * 那么当多线程访问容器里不同数据段的数据时，线程间就不会存在锁竞争，从而可以有效的提高并发访问效率，这就是ConcurrentHashMap所使用的锁分段技术，首先将数据分成一段一段的存储，
 * 然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。
 * <p>
 * 模拟信息的发送和接收，场景是这样的：
 * 服务器向客户端发送信息，要保证信息100%的发送给客户端，那么发给客户端之后，客户端返回一个消息告诉服务器，已经收到。当服务器一直没有收到客户端返回的消息，
 * 那么服务器会一直发送这个信息，直到客户端接收并确认该信息，这时候再删除重复发送的这个信息。
 * <p>
 * 为了模拟该场景，这里写两个线程，一个是发送线程，一个是接收线程，把要发送的信息保存到线程安全的对象里面，防止发生线程安全问题，这里采用ConcurrentHashMap。
 */
public class ConcurrentHashMapTest {

    public static ConcurrentHashMap<Integer, String> pushMessage = new ConcurrentHashMap<Integer, String>();

    //这样两个线程可以轮流的进行各自的事情，并且不会造成数据安全的问题。用这种方式，再结合Androidpn的推送机制，会更加符合实际生产中的应用
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            pushMessage.put(i, "该消息是id为" + i + "的消息");
        }
        Thread sendThread = new SendThread();
        Thread receiveThread = new ReceiveThread();
        sendThread.start();
        receiveThread.start();
        for (int i = 5; i < 10; i++) {
            pushMessage.put(i, "该消息是id为" + i + "的消息");
        }
    }


    /**
     * 重新发送客户端未收到的消息，直到客户端接收并确认该信息
     *
     * @author Administrator
     */
    public static class SendThread extends Thread {
        @Override
        public void run() {
            try {
                sleep(6000);
                while (ConcurrentHashMapTest.pushMessage.size() > 0) {
                    for (Entry<Integer, String> hashMap : ConcurrentHashMapTest.pushMessage.entrySet()) {
                        System.out.println("消息id:" + hashMap.getKey() + "未发送成功，在此重发:" + hashMap.getValue());
                    }
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收发送过来的信息，并从内存中删除
     *
     * @author Administrator
     */
    public static class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 10000; i++) {
                    sleep(2000);
                    for (Entry<Integer, String> map : ConcurrentHashMapTest.pushMessage.entrySet()) {
                        if (map.getKey() == i) {
                            System.out.println("成功收到id为：" + map.getKey() + "返回的信息，删除该元素");
                            ConcurrentHashMapTest.pushMessage.remove(map.getKey());
                        }
                    }
                    System.out.println("内存对象中的元素数量为：" + ConcurrentHashMapTest.pushMessage.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
