package com.around.practice;

import com.around.practice.dto.Item;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemUnitTest {


    @Test
    void itemBasicsShouldWork(){
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99); //

        //AssertJ를 사용한 값 일치 테스트
        assertThat(sampleItem.getId()).isEqualTo("item1");
        assertThat(sampleItem.getName()).isEqualTo("TV tray");
        assertThat(sampleItem.getDescription()).isEqualTo("Alf TV tray");
        assertThat(sampleItem.getPrice()).isEqualTo(19.99);

        assertThat(sampleItem.toString()).isEqualTo(
                "Item{id='item1', name='TV tray', price=19.99, description='Alf TV tray'}");

        Item sampleItem2 = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        assertThat(sampleItem).isEqualTo(sampleItem2);

    }

}
