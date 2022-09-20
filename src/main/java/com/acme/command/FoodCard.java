package com.acme.command;


import com.acme.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aggregate
public class FoodCard {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoodCard.class);

    @AggregateIdentifier
    private UUID foodCartId;

    private Map<UUID, Integer> selectedProducts;

    private boolean confirmed;

    public FoodCard() {
        super();
    }

    @CommandHandler
    public FoodCard(@NotNull CreateFoodCartCommand command) {
        AggregateLifecycle.apply(new FoodCartCreatedEvent(command.getFoodCartId()));
    }

    @CommandHandler
    public void handle(@NotNull SelectProductCommand command) {
        AggregateLifecycle.apply(new ProductSelectedEvent(foodCartId, command.getProductId(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(@NotNull DeselectProductCommand command) {
        AggregateLifecycle.apply(new ProductDeselectedEvent(foodCartId, command.getProductId(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        AggregateLifecycle.apply(new OrderConfirmedEvent(foodCartId));
    }

    @EventSourcingHandler
    public void on(@NotNull FoodCartCreatedEvent event) {
        this.foodCartId = event.getFoodCartId();
        this.selectedProducts = new HashMap<>();
        this.confirmed = false;
    }

    @EventSourcingHandler
    public void on(@NotNull ProductSelectedEvent event) {
        this.selectedProducts.merge(event.getProductId(), event.getQuantity(), Integer::sum);

    }

    @EventSourcingHandler
    public void on(@NotNull ProductDeselectedEvent event) {
        this.selectedProducts.computeIfPresent(
                event.getProductId(),
                (productId, quantity) -> quantity -= event.getQuantity()
        );
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.confirmed = true;
    }
}
