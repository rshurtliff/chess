package service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;


public class ServiceUnitTests {

@BeforeAll
public static void setUpTests(){
    var server = new Server();
    var port = server.run(0);
    System.out.println("Started test HTTP server on " + port);

}
@Test
public void simpleClear(){

}
}
