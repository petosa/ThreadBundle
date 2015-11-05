import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Nick on 10/13/2015.
 *
 * Apply a bottleneck to a group of threaded tasks to conform to hardware limitations.
 */
public class ThreadBundle {

    private int MAXTHREADS;
    private List<? extends Thread> THREADLIST;
    private CountDownLatch latch;

    /**
     * Default constructor for ThreadBundle object.
     */
    public ThreadBundle() {

    }

    /**
     * ThreadList constructor for ThreadBundle object.
     *
     * @param l List of objects extending Thread. These will be chained.
     */
    public ThreadBundle(List<? extends Thread> l) {
        setThreadList(l);
    }

    /**
     * Max constructor for ThreadBundle object.
     *
     * @param max maximum number of threads in each bundle.
     */
    public ThreadBundle(int max) {
        setMaxThreads(max);
    }

    /**
     * ThreadList and max constructor for ThreadBundle object.
     *
     * @param l List of objects extending Thread. These will be chained.
     * @param max maximum number of threads in each bundle.
     */
    public ThreadBundle(List<? extends Thread> l, int max) {
        setThreadList(l);
        setMaxThreads(max);
    }

    //Link threads recursively
    private List<BundledThread> bundleThreads(List<? extends Thread> l, int max) {
        List<BundledThread> b = new ArrayList<BundledThread>();
        for (int x = 0; x < max; x++) {
            if (l.get(x) == null) {
                throw new NullPointerException("A thread was null");
            }
            b.add(buildChain(new BundledThread(l.get(x)), l, x, max));
        }
        return b;
    }

    //Recursive bundling
    private BundledThread buildChain(BundledThread curr, List<? extends Thread> l,
                                     int index, int max) {
        if (index + max >= l.size()) {
            return curr;
        }
        curr.setNext(buildChain(new BundledThread(l.get(index + max)), l, index + max, max));
        return curr;
    }

    public void setThreadList(List<? extends Thread> l) {
        if (l == null) {
            throw new IllegalArgumentException("List is null");
        }
        THREADLIST = l;
    }

    public void setMaxThreads(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max threads must be greater than 0");
        }
        MAXTHREADS = max;
    }

    //Run all thread chains
    public void process(){
        if (THREADLIST == null) {
            throw new RuntimeException("Thread list was never specified");
        }
        if (MAXTHREADS <= 0) {
            throw new IllegalArgumentException("Max number of threads was never specified");
        }
        //Specified max too large; setting max threads to size of list.
        if (MAXTHREADS > THREADLIST.size()) {
            MAXTHREADS = THREADLIST.size();
        }
        List<BundledThread> bundles = bundleThreads(THREADLIST, MAXTHREADS);
        latch = new CountDownLatch(MAXTHREADS);
        for (BundledThread t : bundles) {
            Runnable r = () -> t.start();
            new Thread(r).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Private inner wrapper class for BundledThread
    private class BundledThread extends Thread {

        private BundledThread next;
        private Thread me;

        private BundledThread(Thread t) {
            me = t;
        }

        @Override
        public void start() {
            me.start();
            try {
                me.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (hasNext()) {
                getNext().start();
            } else {
                latch.countDown();
            }
        }

        private void setNext(BundledThread bt) {
            next = bt;
        }

        private BundledThread getNext() {
            return next;
        }

        private boolean hasNext() {
            return next != null;
        }

    }
}