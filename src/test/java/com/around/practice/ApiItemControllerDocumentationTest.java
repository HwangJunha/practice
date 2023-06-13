package com.around.practice;

import com.around.practice.controller.api.ApiItemController;
import com.around.practice.dto.Item;
import com.around.practice.repository.ItemRepository;
import com.around.practice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;


@WebFluxTest(controllers = ApiItemController.class)
@AutoConfigureRestDocs
public class ApiItemControllerDocumentationTest {
    @Autowired private WebTestClient webTestClient;

    @MockBean
    InventoryService inventoryService;

    @MockBean
    ItemRepository itemRepository;

    @Test
    public void finingAllItems(){
        Mockito.when(itemRepository.findAll()).thenReturn(Flux.just(new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("findAll",preprocessResponse(prettyPrint())));

    }

    @Test
    public void postNewItems(){
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(Mono.just(new Item("1", "Alf alarm clock", "nothing important", 19.99)));

        this.webTestClient.post().uri("/api/items")
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(WebTestClientRestDocumentation.document("post-new-item", preprocessResponse(prettyPrint())));

    }
}
