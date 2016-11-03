import HelloApp.Hello;
import HelloApp.HelloHelper;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import rabbitmq.RabbitRPCClient;
import rmi.ComplextRMIObject;
import rmi.RMIInterface;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        //Rest Start
        Date restStart = new Date();
        try {
            Client client = Client.create();
            WebResource webResource = client.resource("http://localhost:8091/createComplexObj");
            String jsonRestRequest = webResource.get(String.class);
            ComplextRMIObject obj = jsonStringToComplexObj(jsonRestRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date restEnd = new Date();
        System.out.println("Total time to Run Rest  : " + dateDiffMillSec(restEnd, restStart) + " ms");
        System.out.println("");
        //Rest Stop


        // File Read Start
        Date fileStart = new Date();
        String path = System.getProperty("user.home") + "/Desktop/obj.json";
        try {
            String fileJson = new String(Files.readAllBytes(Paths.get(path)));
            ComplextRMIObject compObj = jsonStringToComplexObj(fileJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Date fileEnd = new Date();
        System.out.println("Total time to Read File  : " + dateDiffMillSec(fileEnd, fileStart) + " ms");
        System.out.println("");
        // File read end

        //Corba Start
        Date corbaStart = new Date();

        try {
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt namingContextExt = NamingContextExtHelper.narrow(objRef);

            Hello helloObj = (Hello) HelloHelper.narrow(namingContextExt.resolve_str("ABC"));
            String corbaString = helloObj.hellomessage();
            ComplextRMIObject compleObj = jsonStringToComplexObj(corbaString);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Date corbaEnd = new Date();
        System.out.println("Total time to Run Corba  : " + dateDiffMillSec(corbaEnd, corbaStart) + " ms");
        System.out.println("");
        //Corba End

        //Java RMI Start
        Date rmiStart = new Date();
        try {

            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
            RMIInterface rmi = (RMIInterface) reg.lookup("testRMI");
            ComplextRMIObject cro = rmi.createWithId(12L);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Date rmiEnd = new Date();
        System.out.println("Total time to Run RMI  : " + dateDiffMillSec(rmiEnd, rmiStart) + " ms");
        System.out.println("");
        // Java RMI End

        // Rabbit RPC Start
        Date rabbitStart = new Date();
        try {
            RabbitRPCClient rabbitRPCClient = new RabbitRPCClient();
            String response = rabbitRPCClient.call("Give me an Object");
            ComplextRMIObject complextRMIObject = jsonStringToComplexObj(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Date rabbitEnd = new Date();
        System.out.println("Total time to Run RabbitMq  : " + dateDiffMillSec(rabbitEnd, rabbitStart) + " ms");
        System.out.println("");
        //Rabbit RPC End


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
