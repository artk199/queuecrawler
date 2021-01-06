package pl.artkak.queuecrawler.rest;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.artkak.queuecrawler.web.resource.WebResource;
import pl.artkak.queuecrawler.web.resource.WebResourceDownloadingStatus;
import pl.artkak.queuecrawler.web.resource.WebResourceService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("web-resource")
public class WebResourceController {

    private final WebResourceService webResourceService;

    public WebResourceController(WebResourceService webResourceService) {
        this.webResourceService = webResourceService;
    }

    @GetMapping()
    public Collection<WebResourceView> listAllWebResources(@RequestParam(required = false) String content) {
        Collection<WebResource> webResources = webResourceService.findAllWebResourcesWithContentLike(content);
        return webResources.stream().map(WebResourceView::of).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WebResourceView getWebResource(@PathVariable Long id) {
        WebResource webResource = webResourceService.findWebResourceById(id)
                .orElseThrow(() -> new WebResourceNotFoundException(id));
        return WebResourceView.of(webResource);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        WebResource webResource = webResourceService.findWebResourceById(id)
                .orElseThrow(() -> new WebResourceNotFoundException(id));
        if(webResource.getStatus() != WebResourceDownloadingStatus.DOWNLOADED){
            throw new WebResourceNotYetDownloadedException(id);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", webResource.getContentType());
        return new ResponseEntity<>(webResource.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping()
    public WebResourceView createWebResource(@Valid @RequestBody WebResourceView webResourceView) {
        WebResource webResource = convertToEntity(webResourceView);
        webResourceService.saveAndAddWebResourceToQueue(webResource);
        return WebResourceView.of(webResource);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        body.put("errors", errors);
        return body;
    }

    private WebResource convertToEntity(WebResourceView webResourceView) {
        WebResource post = new WebResource();
        post.setUrl(webResourceView.getUrl());
        return post;
    }

}
