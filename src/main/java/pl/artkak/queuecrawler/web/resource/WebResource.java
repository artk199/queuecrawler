package pl.artkak.queuecrawler.web.resource;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class WebResource {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Lob
    @Column()
    private byte[] byteData;

    @Lob
    @Column()
    private String textData;

    @Enumerated(EnumType.STRING)
    private WebResourceDownloadingStatus status;

    private int responseStatusCode;

    private String contentType;

    public Object getContent() {
        return hasTextContentType() ? textData : byteData;
    }

    public boolean hasTextContentType(){
        return contentType != null && contentType.startsWith("text");
    }

}
