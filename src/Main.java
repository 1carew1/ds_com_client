import rabbitmq.Rabbit;
import rmi.ComplextRMIObject;
import rmi.RMIInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        Date rmiStart = new Date();
        System.out.println(rmiStart.toString() + " : RMI Start");
        try {

            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
            RMIInterface rmi = (RMIInterface) reg.lookup("testRMI");
            String testString = "Some String   mad   weird  thing  ";
            String result = rmi.obtainString(testString);
//            System.out.println("Using " + testString + " as input and output is : " + result);

//            System.out.println("Creating a complex object");
            ComplextRMIObject cro = rmi.createWithId(12L);
            System.out.println(cro.getId() + " " + cro.getName() + " " + cro.getPhone());


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
            Rabbit rabbitSend = new Rabbit("localhost", "admin", "admin", "admin", "testRXQueue");
            String message = "Hello World!";
            rabbitSend.sendMessage(message);
            Rabbit rabbitRx = new Rabbit("localhost", "admin", "admin", "admin", "testRXQueue");
            rabbitRx.receive();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Date rabbitEnd = new Date();
        System.out.println(rabbitEnd.toString() + " : Rabbit Finish");
        System.out.println("Total time to Run RabbitMq  : " + dateDiffMillSec(rabbitEnd, rabbitStart) + " ms");
        System.out.println("\n\n");

    }

    public static long dateDiffMillSec(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long miliSeconds = TimeUnit.MILLISECONDS.toMillis(diff);
        if (miliSeconds < 0) {
            miliSeconds = miliSeconds * -1;
        }
        return miliSeconds;
    }
}
