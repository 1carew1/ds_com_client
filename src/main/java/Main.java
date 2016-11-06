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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

public class Main {
    private static final boolean looping = true;

    public static void main(String[] args) {
        int numberOfRuns = 1;
        if (looping) {
            numberOfRuns = 500;
        }
        for (int i = 0; i < numberOfRuns; i++) {


            //Rest Start
            Date restStart = new Date();
            try

            {
                Client client = Client.create();
                WebResource webResource = client.resource("http://localhost:8091/createComplexObj");
                String jsonRestRequest = webResource.get(String.class);
                ComplextRMIObject obj = jsonStringToComplexObj(jsonRestRequest);
                printComplextObj(obj);
            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }

            Date restEnd = new Date();
            Long restTimeDiffmS = dateDiffMillSec(restEnd, restStart);
            System.out.println("Total time to Run Rest  : " + restTimeDiffmS + " ms");
            System.out.println("");

            //Rest Stop
            writeResultsToDB("REST", restTimeDiffmS);


            // File Read Start
            Date fileStart = new Date();
            String path = System.getProperty("user.home") + "/Desktop/obj.json";
            try

            {
                String fileJson = new String(Files.readAllBytes(Paths.get(path)));
                ComplextRMIObject compObj = jsonStringToComplexObj(fileJson);
                printComplextObj(compObj);

            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }

            Date fileEnd = new Date();
            Long fileTime = dateDiffMillSec(fileEnd, fileStart);
            System.out.println("Total time to Read File  : " + fileTime + " ms");
            System.out.println("");

            // File read end
            writeResultsToDB("FILE", fileTime);

            //Corba Start
            Date corbaStart = new Date();
            try

            {
                ORB orb = ORB.init(args, null);
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContextExt namingContextExt = NamingContextExtHelper.narrow(objRef);

                Hello helloObj = (Hello) HelloHelper.narrow(namingContextExt.resolve_str("ABC"));
                String corbaString = helloObj.hellomessage();
                ComplextRMIObject compleObj = jsonStringToComplexObj(corbaString);
                printComplextObj(compleObj);

            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }

            Date corbaEnd = new Date();
            Long corbaTime = dateDiffMillSec(corbaEnd, corbaStart);
            System.out.println("Total time to Run Corba  : " + corbaTime + " ms");
            System.out.println("");

            //Corba End
            writeResultsToDB("CORBA", corbaTime);

            //Java RMI Start
            Date rmiStart = new Date();
            try

            {

                Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                RMIInterface rmi = (RMIInterface) reg.lookup("testRMI");
                ComplextRMIObject cro = rmi.createWithId(12L);
                printComplextObj(cro);

            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }

            Date rmiEnd = new Date();
            Long rmiTime = dateDiffMillSec(rmiEnd, rmiStart);
            System.out.println("Total time to Run RMI  : " + rmiTime + " ms");
            System.out.println("");

            // Java RMI End
            writeResultsToDB("RMI", rmiTime);

            // Rabbit RPC Start
            Date rabbitStart = new Date();
            try

            {
                RabbitRPCClient rabbitRPCClient = new RabbitRPCClient();
                String response = rabbitRPCClient.call("Give me an Object");
                ComplextRMIObject complextRMIObject = jsonStringToComplexObj(response);
                rabbitRPCClient.close();
                printComplextObj(complextRMIObject);
            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }

            Date rabbitEnd = new Date();
            Long rabbitTime = dateDiffMillSec(rabbitEnd, rabbitStart);
            System.out.println("Total time to Run RabbitMq  : " + rabbitTime + " ms");
            System.out.println("");
            //Rabbit RPC End
            writeResultsToDB("RABBITMQ", rabbitTime);


            //Socket Start
            Date socketStart = new Date();
            Socket socket;
            BufferedReader bufferedReader;
            PrintStream printStream;
            try {
                socket = new Socket("localhost", 1025);
                bufferedReader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                String socketString;
                socketString = bufferedReader.readLine();
                ComplextRMIObject obj = jsonStringToComplexObj(socketString);
                bufferedReader.close();
                socket.close();
                printComplextObj(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Date socketEnd = new Date();
            Long socketTime = dateDiffMillSec(socketEnd, socketStart);
            System.out.println("Total time to Run Sockets  : " + socketTime + " ms");
            System.out.println("");
            //Socket End
            writeResultsToDB("SOCKET", socketTime);


            try

            {
                Thread.sleep(100);
            } catch (
                    InterruptedException e)

            {
                e.printStackTrace();
            }
        }

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
//        System.out.println(new Date().toString() + " : " + complextRMIObject.getId() + " " + complextRMIObject.getName() + " " + complextRMIObject.getPhone());
    }

    public static void writeResultsToDB(String type, Long runtime) {
        /* Create Table for DB Table

          CREATE TABLE data_results (
          id int(10) unsigned NOT NULL AUTO_INCREMENT,
          date datetime DEFAULT NULL,
          type varchar(32) DEFAULT NULL,
          runtime int(11) DEFAULT NULL,
          PRIMARY KEY (`id`)
         */
        try {
            // create a mysql database connection
            String myDriver = "com.mysql.jdbc.Driver";
            String myUrl = "jdbc:mysql://localhost/distributed";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "pacemaker", "writer");

            // create a sql date object so we can use it in our INSERT statement
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

            // the mysql insert statement
            String query = " insert into distributed.data_results(date, type, runtime)"
                    + " values (now(), ?, ?)";

            if (looping) {
                query = " insert into distributed.looped_data_results(date, type, runtime)"
                        + " values (now(), ?, ?)";
            }


            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, type);
            preparedStmt.setLong(2, runtime);

            // execute the preparedstatement
            preparedStmt.execute();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

/*
Query to get individual runs :

SELECT
    res.type type,
    AVG(res.runtime) averageRuntime,
    MAX(res.runtime) maxRunTime,
    MIN(res.runtime) minRunTime,
    COUNT(*) numberOfDataPoints
FROM
    distributed.data_results res
GROUP BY res.type
ORDER BY averageRuntime DESC;

Median :
SELECT @type := 'FILE';
SELECT
    AVG(t1.runtime) AS median_val, t1.type
FROM
    (SELECT
        @rownum:=@rownum + 1 AS `row_number`, d.runtime, d.type
    FROM
        distributed.data_results d, (SELECT @rownum:=0) r
    WHERE
        1 AND d.type = @type
    ORDER BY d.runtime) AS t1,
    (SELECT
        COUNT(*) AS total_rows
    FROM
        distributed.data_results d
    WHERE
        1 AND d.type = @type) AS t2
WHERE
    1
        AND t1.row_number IN (FLOOR((total_rows + 1) / 2) , FLOOR((total_rows + 2) / 2))
        AND t1.type = @type;

 */
