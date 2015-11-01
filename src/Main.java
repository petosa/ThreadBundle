import java.util.ArrayList;

/**
 * Created by Nick on 11/1/2015.
 * Sample main method which uses a ThreadBundle to run SampleTasks.
 */
public class Main {
    public static void main(String[] args) {
        //Create an ArrayList of SampleTasks with incremental messages
        ArrayList<SampleTask> l = new ArrayList<SampleTask>();
        for (int x = 0; x < 100; x++) {
            l.add(new SampleTask((x + 1) + "", 1000));
        }

        //Create ThreadBundle
        ThreadBundle tb = new ThreadBundle(l, 10);
        //Process ThreadBundle;
        tb.process();
    }
}

