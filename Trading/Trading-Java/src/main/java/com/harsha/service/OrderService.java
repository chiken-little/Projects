package com.harsha.service;

import com.harsha.domain.OrderType;
import com.harsha.model.Coin;
import com.harsha.model.Order;
import com.harsha.model.OrderItem;
import com.harsha.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrdersofUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;


}
