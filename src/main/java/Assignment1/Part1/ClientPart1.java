package Assignment1.Part1;

import Assignment1.Parameter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class ClientPart1 {

  public static void main(String[] args) {

    System.out.println("--Client 1 start running--");
    int successRequests = 0;
    int unSucceddRequests = 0;
    Long startTime = (long)0;
    Long endTime = (long)0;
    List<StoreThreadPart1> threadPool = new ArrayList<>();

    try {
      Parameter parameter = Parameter.parse(args);
      int threads = parameter.getMaxStores();
      System.out.println( "Total threads: " + threads);

      // create new CountDownLatch object
      CountDownLatch countDown3Hour = new CountDownLatch(1);
      CountDownLatch countDown5Hour = new CountDownLatch(1);
      CountDownLatch complete = new CountDownLatch(parameter.getMaxStores());

      // phase1: start thread/4
      startTime = System.currentTimeMillis();
      System.out.println(String
          .format("Start at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(startTime)));
      System.out.println("--Store opens in east--");
      int storeId = 0;
      for (int i = 0; i < parameter.getMaxStores() / 4; i++) {
        StoreThreadPart1 storeThread = new StoreThreadPart1(storeId, parameter, countDown3Hour, countDown5Hour, complete);
        new Thread(storeThread).start();
        storeId++;
        threadPool.add(storeThread);
      }
      System.out.println("Wait for central store to open...");
      countDown3Hour.await();
      // phase2: start another thread/4
      System.out.println("--Store opens in central--");
      for (int i = 0; i < parameter.getMaxStores() / 4; i++) {
        StoreThreadPart1 storeThread = new StoreThreadPart1(storeId, parameter, countDown3Hour, countDown5Hour, complete);
        new Thread(storeThread).start();
        storeId++;
        threadPool.add(storeThread);
      }
      System.out.println("Wait for western store to open...");
      countDown5Hour.await();
      // phase3: start the rest thread/2
      System.out.println("--Store opens in west--");
      while (storeId < parameter.getMaxStores()) {
        StoreThreadPart1 storeThread = new StoreThreadPart1(storeId, parameter, countDown3Hour, countDown5Hour, complete);
        new Thread(storeThread).start();
        storeId++;
        threadPool.add(storeThread);
      }

      System.out.println("Wait for all threads to complete...");
      complete.await();
      endTime = System.currentTimeMillis();
      System.out.println(String.format(
          "All threads complete at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(endTime)));

      for (StoreThreadPart1 storeThread : threadPool) {
        successRequests += storeThread.getSuccessRequests();
        unSucceddRequests += storeThread.getUnSuccessRequests();
      }

      // total number of successful requests sent
      System.out.println("\n--------------------\n");
      System.out.println("Total number of successful requests sent: " + successRequests);
      System.out.println("Total number of unsuccessful requests sent: " + unSucceddRequests);


    } catch (Exception e) {
      System.err.println("Exception when calling PurchaseApi#newPurchase");
      e.printStackTrace();

    }

    //  Take another timestamp after all Phase 3 threads are complete

    Long wallTime = endTime - startTime;
    System.out.println("Wall Time: " + wallTime/1000.0 + "s");

    // Print out throughput: requests per second = total number of requests/wall time
    System.out.println("Throughput is: " + (double) successRequests / (double) wallTime + "s");
  }
}