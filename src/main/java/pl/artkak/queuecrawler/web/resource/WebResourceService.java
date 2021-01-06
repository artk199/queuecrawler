package pl.artkak.queuecrawler.web.resource;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
public class WebResourceService {

    private final WebResourceRepository webResourceRepository;
    private final BlockingQueue<Long> webResourceBlockingQueue;

    public WebResourceService(WebResourceRepository webResourceRepository,
                              BlockingQueue<Long> webResourceBlockingQueue) {
        this.webResourceRepository = webResourceRepository;
        this.webResourceBlockingQueue = webResourceBlockingQueue;
    }

    /**
     * Set up {@code WebResourceDownloadingStatus.QUEUED} status and saves web resource.
     * @param webResource - resource to be saved
     */
    public void saveAndAddWebResourceToQueue(WebResource webResource) {
        webResource.setStatus(WebResourceDownloadingStatus.QUEUED);
        webResourceRepository.save(webResource);
        webResourceBlockingQueue.add(webResource.getId());
    }

    /**
     * Prepare & send http request for given webResource, then saves result.
     * @param webResource - resource to be downloaded
     */
    @Transactional
    public void downloadContentForWebResource(WebResource webResource) {
        log.info("Downloading web resource with id {} and url {}", webResource.getId(), webResource.getUrl());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(webResource.getUrl())
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            String contentType = String.valueOf(body.contentType());
            webResource.setStatus(WebResourceDownloadingStatus.DOWNLOADED);
            webResource.setResponseStatusCode(response.code());
            webResource.setContentType(contentType);
            if (webResource.hasTextContentType()) {
                webResource.setTextData(body.string());
            } else {
                webResource.setByteData(body.bytes());
            }
        } catch (IOException e) {
            webResource.setStatus(WebResourceDownloadingStatus.ERROR_OCCURRED);
            log.error("Error during request for web resource: {}", webResource.getId(), e);
        }
        webResourceRepository.save(webResource);
    }

    /**
     * Search for web resource with given id.
     * @param id - id of web resource
     * @return found web resource object
     */
    public Optional<WebResource> findWebResourceById(Long id) {
        return webResourceRepository.findById(id);
    }

    /**
     * If content is not empty then searches for {@code WebResource} with downloaded text content with given text.
     * Otherwise returns all {@code WebResource} saved objects.
     * @param content - text to be searched for
     * @return list of found web resources
     */
    public Collection<WebResource> findAllWebResourcesWithContentLike(String content) {
        if (StringUtils.hasText(content)) {
            return webResourceRepository.findByTextDataContaining(content);
        } else {
            return webResourceRepository.findAll();
        }
    }

}
