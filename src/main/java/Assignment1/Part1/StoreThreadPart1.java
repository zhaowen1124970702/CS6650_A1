package Assignment1.Part1;

import Assignment1.Parameter;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.httpclient.HttpStatus;

public class StoreThreadPart1 implements Runnable{
  private Integer storeId;
  private Parameter parameter;
  private PurchaseApi apiInstance = new PurchaseApi();
  private static Random random = new Random();
  private int successRequests = 0;
  private int unSuccessRequests = 0;
  private CountDownLatch countDown3Hour;
  private CountDownLatch countDown5Hour;
  private CountDownLatch complete;


  public StoreThreadPart1(Integer storeId, Parameter parameter, CountDownLatch countDown3Hour, CountDownLatch countDown5Hour, CountDownLatch complete) {
    this.storeId = storeId;
    this.parameter = parameter;
    this.countDown3Hour = countDown3Hour;
    this.countDown5Hour = countDown5Hour;
    this.complete = complete;

    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(parameter.getIpAddress());
//    apiClient.setBasePath("http://localhost:8080/A1_war_exploded");
    this.apiInstance.setApiClient(apiClient);
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    for(int hour = 0;hour < 9;hour++) {
      try {
        for(int send = 0;send < parameter.getNumPurchases();send++) {
          Purchase body = new Purchase();
          body.items(genItems(parameter));
//          System.out.println("hah");
          // change storeId, genCustomerId(parameter,storeId), parameter.getDate()
          ApiResponse<Void> response = apiInstance.newPurchaseWithHttpInfo(
              body, storeId, genCustomerId(parameter,storeId), parameter.getDate());
          Integer resCode = response.getStatusCode();
//          System.out.println("resCode"+resCode);
          if (resCode == HttpStatus.SC_OK){
            this.successRequests++;
            continue;
          }else if (String.valueOf(resCode).equals(String.valueOf(5))){
            this.unSuccessRequests ++;
            System.err.println(resCode + ": There is a web error");
          } else if (String.valueOf(resCode).equals(String.valueOf(4))){
            this.unSuccessRequests ++;
            System.err.println(resCode + ": Not found");
          }
        }
      }catch (Exception e) {
        System.out.println("Exception when calling StoreThread");
        e.printStackTrace();
      }
      if(hour == 2) {
        countDown3Hour.countDown();
      }
      if(hour == 4) {
        countDown5Hour.countDown();
      }
    }
    this.complete.countDown();
  }


  // Generate purchase items
  private List<PurchaseItems> genItems(Parameter parameter) {
    List<PurchaseItems> list = new ArrayList<>();
    for(int i = 0 ;i<parameter.getNumItemPerPurchase(); i++) {
      PurchaseItems purchaseItems = new PurchaseItems();
      purchaseItems.setItemID(String.valueOf(random.nextInt(parameter.getMaxItemID())));
      purchaseItems.setNumberOfItems(1);
      list.add(purchaseItems);
    }
    return list;
  }

  // Randomly generate customerID
  private Integer genCustomerId(Parameter parameter, Integer storeId) {
    Integer customerId = random.nextInt(parameter.getMaxCustomerPerStore());
    customerId += storeId * parameter.getMaxStores();
    return customerId;
  }

  public int getSuccessRequests() {
    return this.successRequests;
  }

  public int getUnSuccessRequests() {
    return this.unSuccessRequests;
  }
}
