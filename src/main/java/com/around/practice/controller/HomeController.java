package com.around.practice.controller;

import com.around.practice.dto.Cart;
import com.around.practice.dto.CartItem;
import com.around.practice.dto.Item;
import com.around.practice.repository.CartRepository;
import com.around.practice.repository.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

    private ItemRepository itemRepository;
    private CartRepository cartRepository;

    public HomeController(ItemRepository itemRepository, // <2>
                          CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }
    // end::1[]

    // tag::2[]
    @GetMapping
    Mono<Rendering> home() { // <1>
        return Mono.just(Rendering.view("home.html") // <2>
                .modelAttribute("items", //
                        this.itemRepository.findAll().doOnNext(System.out::println)) // <3>
                .modelAttribute("cart", //
                        this.cartRepository.findById("My Cart") // <4>
                                .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }
    // end::2[]

    // tag::3[]
    @PostMapping("/add/{id}") // <1>
    Mono<String> addToCart(@PathVariable String id) { // <2>
        return this.cartRepository.findById("My Cart") //
                .log("foundCart")
                .defaultIfEmpty(new Cart("My Cart")) // <3>
                .log("emptyCart")
                .flatMap(cart -> cart.getCartItems().stream() // <4>
                        .filter(cartItem -> cartItem.getItem() //
                                .getId().equals(id)) //
                        .findAny() //
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart).log("newCartItem");
                        }) //
                        .orElseGet(() -> { // <5>
                            return this.itemRepository.findById(id) //
                                    .log("fetchedItem")
                                    .map(item -> new CartItem(item)) //
                                    .log("cartITem")
                                    .map(cartItem -> {
                                        cart.getCartItems().add(cartItem);
                                        return cart;
                                    }).log("addedCartItem");
                        }))
                .log("cartWithAnotherItem")
                .flatMap(cart -> this.cartRepository.save(cart))
                .log("savedCart")// <6>
                .thenReturn("redirect:/"); // <7>
    }
    // end::3[]

    @PostMapping
    Mono<String> createItem(@ModelAttribute Item newItem) {
        return this.itemRepository.save(newItem) //
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    Mono<String> deleteItem(@PathVariable String id) {
        return this.itemRepository.deleteById(id) //
                .thenReturn("redirect:/");
    }
}
