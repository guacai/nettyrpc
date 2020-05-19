package entity;


import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
@Getter
@Setter
public class RpcRegistered {
    private String ip;
    private int port;
    private int serviceId;
    private String serviceName;

    public RpcRegistered(String ip, int port, int serviceId, String serviceName) {
        this.ip = ip;
        this.port = port;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public String endPoint(){
        return ip + ":" + port;
    }
}
