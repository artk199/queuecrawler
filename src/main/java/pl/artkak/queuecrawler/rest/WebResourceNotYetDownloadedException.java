package pl.artkak.queuecrawler.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WebResourceNotYetDownloadedException extends RuntimeException {

    public WebResourceNotYetDownloadedException(Long id) {
        super("Web resource not yet downloaded: " + id);
    }

}
