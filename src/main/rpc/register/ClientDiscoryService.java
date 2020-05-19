package register;

import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Util;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 *
 *
 * @description:
 * @author: zhangtb
 * @Date: 2020-05-13 15:08
 */
@Service
@Slf4j
public class ClientDiscoryService implements Runnable{

    public List<String> endPoints;

    public void watch(){
        while(true){
            Args cmd = new Args();
            CountDownLatch latch = new CountDownLatch(cmd.maxEvents);
            ByteSequence key = ByteSequence.from(cmd.key, StandardCharsets.UTF_8);
            Collection<URI> endpoints = Util.toURIs(cmd.endpoints);

            Watch.Listener listener = Watch.listener(response -> {
                log.info("Watching for key={}", cmd.key);
                for (WatchEvent event : response.getEvents()) {
                    log.info("type={}, key={}, value={}", event.getEventType(),
                            Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(StandardCharsets.UTF_8)).orElse(""),
                            Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(StandardCharsets.UTF_8))
                                    .orElse(""));
                    endPoints = Splitter.on(",").trimResults().splitToList(event.getKeyValue().getValue().toString());
                }
                latch.countDown();
            });

            try (Client client = Client.builder().endpoints(endpoints).build();
                 Watch watch = client.getWatchClient();
                 Watch.Watcher watcher = watch.watch(key, listener)) {
                latch.await();
            } catch (Exception e) {
                log.error("Watching Error {}", e);
                System.exit(1);
            }
        }
    }

    @Override
    public void run() {
        watch();
    }

    public static class Args {
        @Parameter(required = true, names = { "127.0.0.1:2379"}, description = "the etcd endpoints")
        private List<String> endpoints = new ArrayList<>();

        @Parameter(required = true, names = { "serverhost" }, description = "the key to watch")
        private String key;

        @Parameter(names = { "-m", "--max-events" }, description = "the maximum number of events to receive")
        private Integer maxEvents = Integer.MAX_VALUE;
    }


    public void watchStart(){
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
}
