package com.around.practice;

import com.around.practice.dto.Cart;
import com.around.practice.dto.CartItem;
import com.around.practice.dto.Item;
import com.around.practice.repository.CartRepository;
import com.around.practice.repository.ItemRepository;
import com.around.practice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collection;
import java.util.Collections;


import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class InventoryServiceUnitTest {
    InventoryService inventoryService;

    @MockBean private ItemRepository itemRepository;
    @MockBean private CartRepository cartRepository;

    @BeforeEach
    void setUp(){
        //테스트 데이터 정의
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        //협력자와 상호작용 정의
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new InventoryService(itemRepository, cartRepository);
    }
    //탑 레벨 방식이라고도 부르는 패러다임을 사용
    //먼저 리액터 기반 함수를 최상위에서 호출하고 바로 다음에 as를 이어서 호출
    @Test
    void addItemToEmptyCartShouldProduceOneCartItem(){
        inventoryService.addItemToCart("My Cart", "item1")
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);
                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));
                    return true;
                })
                .verifyComplete();
    }

    //탑 레벨과는 다른 방식으로 작성한 테스트 코드
    //이 방식은 단순히 바깥에 명시적으로 드러난 행이 아니라 메소드의 인자까지 뒤져봐야 무엇이 테스트되는지를 알 수 있으므로 별로 좋은 방식이 아님
    @Test
    void alternativeWayToTest(){
        StepVerifier.create(
                inventoryService.addItemToCart("My Cart", "item1"))
                .expectNextMatches(cart -> {
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);
                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));
                    return true;
                })
                .verifyComplete();
    }

}
