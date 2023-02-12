package ipaddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IPAddress {

    public static void main(String[] args) throws UnknownHostException {
        InetAddress[] ipaddr = InetAddress.getAllByName("www.netflix.com");
        //System.out.println(Arrays.toString(ipaddr));

        for(InetAddress ip: ipaddr){
            System.out.println(ip);
        }
    }
}
