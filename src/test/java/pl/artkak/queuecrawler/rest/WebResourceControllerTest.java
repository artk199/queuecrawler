package pl.artkak.queuecrawler.rest;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.artkak.queuecrawler.web.resource.WebResource;
import pl.artkak.queuecrawler.web.resource.WebResourceService;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
class WebResourceControllerTest {

    @MockBean
    private WebResourceService webResourceService;

    @Autowired
    WebResourceController webResourceController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenPostRequestWithURL_thenCorrectResponse() throws Exception {
        String user = "{\"url\": \"http://example.com\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/web-resource")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenPostRequestWithEmptyURL_thenCorrectResponse() throws Exception {
        String user = "{\"url\": \"\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/web-resource")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]", Is.is("URL is required")));
    }

    @Test
    public void whenGetWebResourceThatDoesNotExists_then404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/web-resource/111333"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void whenGetWebResourceThatDoesExists_thenCorrectResponse() throws Exception {
        long id = 11L;
        WebResource webResource = new WebResource();
        webResource.setId(id);
        webResource.setUrl("http://example.com");
        Mockito.when(webResourceService.findWebResourceById(id)).thenReturn(Optional.of(webResource));
        mockMvc.perform(MockMvcRequestBuilders.get("/web-resource/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url", Is.is(webResource.getUrl())));
    }

}