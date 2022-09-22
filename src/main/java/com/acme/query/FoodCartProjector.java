package com.acme.query;

import com.acme.coreapi.FindFoodCartQuery;
import com.acme.coreapi.FoodCartCreatedEvent;
import com.acme.coreapi.ProductDeselectedEvent;
import com.acme.coreapi.ProductSelectedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class FoodCartProjector {

    private final FoodCartViewRepository foodCartViewRepository;

    public FoodCartProjector(FoodCartViewRepository foodCartViewRepository) {
        this.foodCartViewRepository = foodCartViewRepository;
    }

    @EventHandler
    public void on(FoodCartCreatedEvent event) {
        FoodCartView foodCartView = new FoodCartView(event.getFoodCartId(), Collections.emptyMap());

        this.foodCartViewRepository.save(foodCartView);
    }

    @EventSourcingHandler
    public void on(ProductSelectedEvent event){
        foodCartViewRepository.findById(event.getFoodCartId()).ifPresent(
                foodCartView -> foodCartView.addProducts(event.getProductId(), event.getQuantity())
        );
    }
    @EventHandler
    public void on(ProductDeselectedEvent event){
        foodCartViewRepository.findById(event.getFoodCartId()).ifPresent(
                foodCartView -> foodCartView.removeProduct(event.getProductId(), event.getQuantity())
        );
    }

    @QueryHandler
    public FoodCartView handle(FindFoodCartQuery query) {
        return foodCartViewRepository.findById(query.getFoodCartId()).orElse(null);
    }

}
