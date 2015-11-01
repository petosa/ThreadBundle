# ThreadBundle
The ThreadBundle object allows you to pass in a List of threaded tasks for capped concurrent processing.

If li is a List of Threads, then you can declare a ThreadBundle: ThreadBundle tb = new ThreadBundle(li, #); where # is a counting number. # specifies the maximum number of threads that can run at once. Then call tb.process() to run # threads at once. Once a thread completes, another waiting thread is started. This chain continues until all threads are processed.

![alt tag](http://i.imgur.com/GovDURQ.png)

Boxes with the same colors are part of the same linked list. Notice how they they are 3 indices apart, as that is the maximum number of threads that can run at once. Upon processing this ThreadBundle, all colors will have one representative running concurrently at once. Once one red thread completes, it starts the next red thread.

ThreadBundle's process event waits for all threads to complete before returning.
