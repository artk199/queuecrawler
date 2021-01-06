package pl.artkak.queuecrawler.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WebResourceNotFoundException extends RuntimeException {

    public WebResourceNotFoundException(Long id) {
        super("Not found web resource with id: " + id);
    }

}
