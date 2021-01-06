package pl.artkak.queuecrawler.web.resource;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebResourceServiceTest {

    private WebResourceService webResourceService;
    private MockWebServer mockWebServer;

    @Mock
    private WebResourceRepository webResourceRepositoryMock;

    private final BlockingQueue<Long> webResourceBlockingQueue = new LinkedBlockingQueue<>();


    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        webResourceService = new WebResourceService(webResourceRepositoryMock, webResourceBlockingQueue);
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
    }

    @Test
    void whenAddingNewWebResource_thenProperStatusShouldBeSetAndShouldBeSavedAndAddedToQueue() {
        //setup
        WebResource webResource = new WebResource();
        webResource.setId(11L);
        webResource.setUrl("http://example.com");

        //when
        webResourceService.saveAndAddWebResourceToQueue(webResource);

        //then
        assertEquals(WebResourceDownloadingStatus.QUEUED, webResource.getStatus());
        assertTrue(webResourceBlockingQueue.contains(webResource.getId()));
        verify(webResourceRepositoryMock, times(1)).save(eq(webResource));
    }

    @Test
    void whenDownloadingWebResource_thenResponseShouldBeSaved() {
        //setup
        String body = "<html><body>SomeContent</html>";
        String contentType = "text/html; charset=utf-8";
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", contentType)
                .setBody(body)
                .throttleBody(16, 1, TimeUnit.SECONDS);
        mockWebServer.enqueue(mockResponse);

        WebResource webResource = new WebResource();
        webResource.setId(11L);
        webResource.setUrl(mockWebServer.url("/").toString());

        //when
        webResourceService.downloadContentForWebResource(webResource);

        //then
        assertEquals(body, webResource.getTextData());
        assertEquals(contentType, webResource.getContentType());
        assertEquals(HttpStatus.OK.value(), webResource.getResponseStatusCode());
        assertEquals(WebResourceDownloadingStatus.DOWNLOADED, webResource.getStatus());
    }
}