import com.google.gson.Gson;
import rabbitmq.RabbitRPCClient;
import rmi.ComplextRMIObject;
import rmi.RMIInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {

        Date rmiStart = new Date();
        System.out.println(rmiStart.toString() + " : RMI Start");
        try {

            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
            RMIInterface rmi = (RMIInterface) reg.lookup("testRMI");
            ComplextRMIObject cro = rmi.createWithId(12L);
            printComplextObj(cro);


        } catch (Exception e) {
            e.printStackTrace();
        }
        Date rmiEnd = new Date();
        System.out.println(rmiEnd.toString() + " : RMI Finish");
        System.out.println("Total time to Run RMI  : " + dateDiffMillSec(rmiEnd, rmiStart) + " ms");
        System.out.println("\n\n");


        Date rabbitStart = new Date();
        System.out.println(rabbitStart.toString() + " : Rabbit Start");
        try {
            RabbitRPCClient rabbitRPCClient = new RabbitRPCClient();
            String response = rabbitRPCClient.call("Give me an Object");
            ComplextRMIObject complextRMIObject = jsonStringToComplexObj(response);
            printComplextObj(complextRMIObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Date rabbitEnd = new Date();
        System.out.println(rabbitEnd.toString() + " : Rabbit Finish");
        System.out.println("Total time to Run RabbitMq  : " + dateDiffMillSec(rabbitEnd, rabbitStart) + " ms");
        System.out.println("\n\n");

        exit(1);

    }

    public static long dateDiffMillSec(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long miliSeconds = TimeUnit.MILLISECONDS.toMillis(diff);
        if (miliSeconds < 0) {
            miliSeconds = miliSeconds * -1;
        }
        return miliSeconds;
    }

    public static ComplextRMIObject jsonStringToComplexObj(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ComplextRMIObject.class);
    }

    public static void printComplextObj(ComplextRMIObject complextRMIObject) {
        System.out.println(new Date().toString() + " : " + complextRMIObject.getId() + " " + complextRMIObject.getName() + " " + complextRMIObject.getPhone());
    }
}
