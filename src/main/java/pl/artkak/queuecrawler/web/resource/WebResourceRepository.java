package pl.artkak.queuecrawler.web.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface WebResourceRepository extends JpaRepository<WebResource, Long> {

    @Query("select r.id from WebResource r where r.status = 'QUEUED'")
    Collection<Long> findAllQueuedIds();

    Collection<WebResource> findByTextDataContaining(String content);

}
