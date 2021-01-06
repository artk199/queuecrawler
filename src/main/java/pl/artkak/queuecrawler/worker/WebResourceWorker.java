package pl.artkak.queuecrawler.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.artkak.queuecrawler.web.resource.WebResource;
import pl.artkak.queuecrawler.web.resource.WebResourceRepository;
import pl.artkak.queuecrawler.web.resource.WebResourceService;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class WebResourceWorker implements Runnable {

    private volatile boolean cancelled;

    private final WebResourceRepository webResourceRepository;
    private final BlockingQueue<Long> webResourceBlockingQueue;
    private final WebResourceService webResourceService;

    public WebResourceWorker(WebResourceRepository webResourceRepository,
                             BlockingQueue<Long> webResourceBlockingQueue,
                             WebResourceService webResourceService) {
        this.webResourceRepository = webResourceRepository;
        this.webResourceBlockingQueue = webResourceBlockingQueue;
        this.webResourceService = webResourceService;
    }

    @Override
    public void run() {
        log.info("Starting listening for web resources to download...");
        while (!cancelled) {
            try {
                Long value = webResourceBlockingQueue.take();
                Optional<WebResource> webResourceOptional = webResourceRepository.findById(value);
                if (webResourceOptional.isEmpty()) {
                    log.warn("WebResource with id {} could not be found. Probably deleted during waiting in queue.", value);
                } else {
                    webResourceService.downloadContentForWebResource(webResourceOptional.get());
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException occurred while waiting for next web resource", e);
            } catch (Exception e) {
                log.error("Unknown error: ", e);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
