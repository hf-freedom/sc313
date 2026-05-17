package com.repair.task;

import com.repair.entity.RepairOrder;
import com.repair.entity.enums.OrderStatus;
import com.repair.service.OrderService;
import com.repair.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderTimeoutTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(fixedRate = 5000)
    public void scanTimeoutUnacceptedOrders() {
        long now = System.currentTimeMillis();

        DataStore.ORDER_MAP.values().stream()
                .filter(order -> order.getStatus().equals(OrderStatus.PENDING_ACCEPT.getCode()))
                .filter(order -> {
                    Long timeout = order.getTimeoutUnaccepted();
                    if (timeout == null) timeout = 60000L;
                    return (now - order.getCreateTime().getTime()) > timeout;
                })
                .forEach(order -> {
                    System.out.println("订单超时未接单，释放重派: " + order.getOrderNo());
                    orderService.releaseTimeoutOrder(order);
                    orderService.reassignOrder(order);
                });
    }

    @Scheduled(fixedRate = 5000)
    public void scanTimeoutUnvisitedOrders() {
        long now = System.currentTimeMillis();

        DataStore.ORDER_MAP.values().stream()
                .filter(order -> order.getStatus().equals(OrderStatus.ACCEPTED.getCode()))
                .filter(order -> {
                    Long timeout = order.getTimeoutUnvisited();
                    if (timeout == null) timeout = 300000L;
                    Date acceptTime = order.getAcceptTime();
                    if (acceptTime == null) return false;
                    return (now - acceptTime.getTime()) > timeout;
                })
                .forEach(order -> {
                    System.out.println("师傅超时未上门，释放重派: " + order.getOrderNo());
                    orderService.releaseTimeoutOrder(order);
                    orderService.reassignOrder(order);
                });
    }
}
