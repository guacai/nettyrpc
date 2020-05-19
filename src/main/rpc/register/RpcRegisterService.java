package register;

import entity.RpcRegistered;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @description:
 * @author: zhangtb
 * @Date: 2020-05-12 11:04
 */
@Service
public class RpcRegisterService {

    private Client client;
    /** 
    * @description:   ectd register
    * @param:  [registered] 
    * @return: void 
    * @author: zhangtb
    * @date:   2020-05-13 14:32 
    * @throws  */
    public void register(RpcRegistered registered){
        client = Client.builder().endpoints(registered.endPoint()).build();
        add(registered);
    }


    private void add(RpcRegistered registered){
        ByteSequence value =  ByteSequence.from(registered.endPoint().getBytes());
        ByteSequence key =  ByteSequence.from(String.valueOf(registered.getServiceName()).getBytes());
        KV kvClient = client.getKVClient();
        kvClient.put(key, value);
    }

    public void delete(RpcRegistered registered){
        ByteSequence key =  ByteSequence.from(String.valueOf(registered.getServiceId()).getBytes());
        KV kvClient = client.getKVClient();
        kvClient.delete(key);
    }
}
