package com.harsha.controller;

import com.harsha.domain.OrderType;
import com.harsha.model.Coin;
import com.harsha.model.Order;
import com.harsha.model.User;
import com.harsha.request.CreateOrderRequest;
import com.harsha.service.CoinService;
import com.harsha.service.OrderService;
import com.harsha.service.UserService;
import com.harsha.service.WalletService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
        @RequestHeader("Authorization") String jwt,
        @RequestBody CreateOrderRequest req) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findCoinById(req.getCoinId());

        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long orderId) throws Exception{
        User user = userService.findUserProfileByJwt(jwtToken);

        Order order = orderService.getOrderById(orderId);
        if(Objects.equals(order.getUser().getId(), user.getId())){
            return ResponseEntity.ok(order);
        }
        else{
            throw new Exception("You don't have access");
        }
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_Symbol) throws Exception {

       Long userId = userService.findUserProfileByJwt(jwt).getId();

       List<Order> userOrders = orderService.getAllOrdersofUser(userId, order_type, asset_Symbol);
       return ResponseEntity.ok(userOrders);

    }


}
