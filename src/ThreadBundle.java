import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Nick on 10/13/2015.
 */
public class ThreadBundle {

    private int MAXTHREADS;
    private List<Thread> THREADLIST;
    private List<BundledThread> BUNDLEDTHREADLIST;
    private CountDownLatch latch;

    public ThreadBundle(List<Thread> l, int max) {
        if (l == null) {
            throw new IllegalArgumentException("List is null");
        }
        if (max <= 0) {
            throw new IllegalArgumentException("Max threads must be greater than 0");
        }
        if (max > l.size()) {
           max = l.size();
            System.out.println("Specified max too large; setting max threads to " + l.size() + ".");
        }
        MAXTHREADS = max;
        THREADLIST = l;
        BUNDLEDTHREADLIST = bundleThreads(l, max);
    }

    //Link threads recursively
    private List<BundledThread> bundleThreads(List<Thread> l, int max) {
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
    private BundledThread buildChain(BundledThread curr, List<Thread> l, int index, int max) {
        if (index + max >= l.size()) {
            return curr;
        }
        curr.setNext(buildChain(new BundledThread(l.get(index + max)), l, index + max, max));
        return curr;
    }

    //Run all thread chains
    public void process() throws InterruptedException{
        latch = new CountDownLatch(MAXTHREADS);
        for (BundledThread t : BUNDLEDTHREADLIST) {
            t.run();
        }
        latch.await();
    }

    //Private inner wrapper class for thread
    private class BundledThread extends Thread {

        private BundledThread next;
        private Thread me;

        public BundledThread(Thread t) {
            me = t;
        }

        @Override
        public void run() {
            me.run();
            try {
                me.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (hasNext()) {
                next.run();
            } else {
                latch.countDown();
            }
        }

        public void setNext(BundledThread bt) {
            next = bt;
        }

        public BundledThread getNext() {
            return next;
        }

        public boolean hasNext() {
            return next != null;
        }

    }
}