package com.acme.gui;

import com.acme.coreapi.CreateFoodCartCommand;
import com.acme.coreapi.DeselectProductCommand;
import com.acme.coreapi.FindFoodCartQuery;
import com.acme.coreapi.SelectProductCommand;
import com.acme.query.FoodCartView;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/foodCard")
@RestController
public class FoodOrderingController {

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    public FoodOrderingController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping("/create")
    public CompletableFuture<UUID> createFoodCard() {
        return commandGateway.send(new CreateFoodCartCommand(UUID.randomUUID()));
    }

    @PostMapping("/{foodCartId}/select/{productId}/quantity/{quantity}")
    public void selecProduct(@PathVariable("foodCartId") String foodCartId,
                             @PathVariable("productId") String productId,
                             @PathVariable("quantity") Integer quantity) {

        this.commandGateway.send(new SelectProductCommand(UUID.fromString(foodCartId), UUID.fromString(productId), quantity));
    }

    @PostMapping("/{foodCartId}/deselect/{productId}/quantity/{quantity}")
    public void deselecProduct(@PathVariable("foodCartId") String foodCartId,
                               @PathVariable("productId") String productId,
                               @PathVariable("quantity") Integer quantity) {
        this.commandGateway.send(new DeselectProductCommand(UUID.fromString(foodCartId), UUID.fromString(productId), quantity));
    }

    @GetMapping("/{foodCartId}")
    public CompletableFuture<FoodCartView> findFoodCart(@PathVariable("foodCartId") String foodCartId) {
        return queryGateway.query(
                new FindFoodCartQuery(UUID.fromString(foodCartId)), ResponseTypes.instanceOf(FoodCartView.class)
        );
    }
}
