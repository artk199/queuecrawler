package pl.artkak.queuecrawler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.artkak.queuecrawler.web.resource.WebResourceRepository;
import pl.artkak.queuecrawler.worker.WebResourceWorker;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Called on application startup.
 *
 * <ul>
 * <li> starts webResourceWorker</li>
 * <li> adds all abandoned tasks from database to queue </li>
 * </ul>
 *
 */
@Component
@Slf4j
class ApplicationBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    private final WebResourceWorker webResourceWorker;
    private final WebResourceRepository webResourceRepository;
    private final BlockingQueue<Long> webResourceBlockingQueue;

    ApplicationBootstrap(WebResourceWorker webResourceWorker,
                         WebResourceRepository webResourceRepository,
                         BlockingQueue<Long> webResourceBlockingQueue) {
        this.webResourceWorker = webResourceWorker;
        this.webResourceRepository = webResourceRepository;
        this.webResourceBlockingQueue = webResourceBlockingQueue;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Start worker
        log.info("Starting web resource worker with class: {}", webResourceWorker.getClass().getSimpleName());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(webResourceWorker);

        // Add to queue all resources with proper status
        Collection<Long> webResourcesIds = webResourceRepository.findAllQueuedIds();
        log.info("Adding {} web resources waiting to be downloaded.", webResourcesIds.size());
        webResourceBlockingQueue.addAll(webResourcesIds);

        log.info("Initialization complete.");
    }

}