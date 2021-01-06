package pl.artkak.queuecrawler.worker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class WorkerConfiguration {

    @Bean
    public BlockingQueue<Long> webResourceBlockingQueue(){
        return new LinkedBlockingQueue<>();
    }

}
