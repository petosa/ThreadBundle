/**
 * Created by Nick on 11/1/2015.
 * Sample task which waits a certain amount of time before exiting.
 */
public class SampleTask extends Thread {

    private String msg;
    private int sleepTime;

    public SampleTask(String s, int sl) {
        msg = s;
        sleepTime = sl;
    }

    public void run() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(msg);
    }
}
