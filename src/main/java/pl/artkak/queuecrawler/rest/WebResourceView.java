package pl.artkak.queuecrawler.rest;

import lombok.*;
import pl.artkak.queuecrawler.web.resource.WebResource;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class WebResourceView {

    private Long id;

    @NotBlank(message = "URL is required")
    private String url;


    public static WebResourceView of(WebResource webResource) {
        return builder()
                .id(webResource.getId())
                .url(webResource.getUrl())
                .build();
    }

}
