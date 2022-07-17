package net.kris.gc_caller;

public class GCCallerThread implements Runnable {
    private static final GCCallerThread instance = new GCCallerThread();
    private Thread thread;

    private GCCallerThread() {
        renew();
    }

    private void renew() {
        thread = new Thread(this, "gc_caller");
        thread.setDaemon(true);
    }

    @Override
    public void run() {
        GCCaller.logger.info("starting garbage collector");
        long start = System.nanoTime();
        System.gc();
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
        }
        System.gc();
        GCCaller.logger.info("garbage collector ended " + ((System.nanoTime() - start) / 1000000) + "ms");
    }

    public static GCCallerThread getInstance() {
        return instance;
    }

    public static boolean gc() {
        if (instance.thread.isAlive()) {
            return false;
        } else {
            instance.renew();
            instance.thread.start();
            return true;
        }
    }
}