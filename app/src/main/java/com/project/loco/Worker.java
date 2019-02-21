package com.project.loco;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread {
    private static final String TAG = "Worker";

    private AtomicBoolean alive = new AtomicBoolean(true);
    private ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    public Worker(){
        super(TAG);
        start();
    }

    public void run(){
        while (alive.get()){
            Runnable task = taskQueue.poll();
            if (task != null){
                task.run();
            }
        }
        Log.i(TAG, "Worker Terminated");
    }

    public  Worker execute(Runnable task){
        taskQueue.add(task);
        return this;
    }

    public void quit(){
        alive.set(false);
    }
}
