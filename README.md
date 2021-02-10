# CS6650_A1

**Note: Description of my client desegin, requred plots and outputs of each run are all included in file "Assignment 1 Client Design_WEN_ZHAO.PDF". **

The following is major introduction for this project. 

## -----Major classes and Relation Introduction-----

--File Location: src/main/java/Assignment1/MarketServlet

--MarketServlet: This MarketServlet class is used to process two APIs in the A1 project, which does server implementation work. 

--File Location: src/main/java/Assignment1/Parameter

--Parameter: This Parameter class is used to parse and validate the arguments from the command line. In this class, I choose the map to store the arguments, the key is the name of the argument, value is the corresponding data. What’s more, this class meets the requirements of parameters about the default value and valid range. 


In Part1 Package, ClientPart1 class and StoreThread1 are included.

--File Location: src/main/java/Assignment1/Part1/StoreThread1

> --StoreThread1: 
This StoreThread1 class implements Runnable and play as a single thread for every store to complete sending requests task. What’s more, the methods to randomly select a custID and itemIDs for the order are implemented in this class by employing the PurchaseItems class and Purchase class from the Swagger client API package.


--File Location: src/main/java/Assignment1/Part1/ClientPart1

> --ClientPart1: 
This ClientPart1 class accepts a set of arguments from the command line at startup. In the main function, it will generate a Parameter object to help parse arguments. Then it will generate max threads based on the Parameter.maxStores, then run in three phases with the help of CountDownLarch to set up barriers, so that it could simulate the staggered opening times of stores across 3 timezones. 

In Part2 Package, ClientPart2 class and StoreThread2 are included.

--File Location: src/main/java/Assignment1/Part2/

> --StoreThread2 and ClientPar2: 
It is basically the same as Part 1, but there is a little changes to have deeper insights into the performance of the system. It adds timestamps to calculate the latency and write records to CSV file for performance analysis.


## ------Good Design practices used------

To follow good design practice, inheritance is adopted in the StoreThread1 class so that we could use Runnable object to create a thread and it is more flexible.
To enhance thread-safe, StoreThread2 employs CopyOnWriteArrayLis instead of ArrayList to store the record data of each thread, which is especially useful in multithreaded code when reads frequent.

------Accept command-line options------

—maxStores 32 --ipAddress http://localhost:8080/A1_war_exploded

--maxStores 64 --ipAddress http://localhost:8080/A1_war_exploded

--maxStores 128 --ipAddress http://localhost:8080/A1_war_exploded

--maxStores 256 --ipAddress http://localhost:8080/A1_war_exploded
