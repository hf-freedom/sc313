package com.repair.controller;

import com.repair.common.Result;
import com.repair.dto.OrderCreateDTO;
import com.repair.entity.RepairOrder;
import com.repair.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<RepairOrder> createOrder(@RequestBody OrderCreateDTO dto) {
        try {
            RepairOrder order = orderService.createOrder(dto);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/available/{workerId}")
    public Result<List<RepairOrder>> getAvailableOrders(@PathVariable String workerId) {
        try {
            List<RepairOrder> orders = orderService.getAvailableOrders(workerId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/accept")
    public Result<RepairOrder> acceptOrder(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        try {
            String workerId = body.get("workerId");
            RepairOrder order = orderService.acceptOrder(orderId, workerId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/visit")
    public Result<RepairOrder> confirmVisit(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        try {
            String workerId = body.get("workerId");
            RepairOrder order = orderService.confirmVisit(orderId, workerId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/complete")
    public Result<RepairOrder> completeOrder(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        try {
            String workerId = body.get("workerId");
            RepairOrder order = orderService.completeOrder(orderId, workerId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/cancel")
    public Result<RepairOrder> cancelOrder(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        try {
            String userId = body.get("userId");
            RepairOrder order = orderService.cancelOrder(orderId, userId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}")
    public Result<List<RepairOrder>> getWorkerOrders(@PathVariable String workerId) {
        try {
            List<RepairOrder> orders = orderService.getWorkerOrders(workerId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<List<RepairOrder>> getUserOrders(@PathVariable String userId) {
        try {
            List<RepairOrder> orders = orderService.getUserOrders(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public Result<RepairOrder> getOrderDetail(@PathVariable String orderId) {
        RepairOrder order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping
    public Result<List<RepairOrder>> getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }

    @PostMapping("/{orderId}/release")
    public Result<RepairOrder> releaseOrder(@PathVariable String orderId) {
        try {
            RepairOrder order = orderService.getOrderDetail(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (order.getStatus() != 1) {
                return Result.error("只有已接单状态的订单才能释放");
            }
            orderService.releaseTimeoutOrder(order);
            orderService.reassignOrder(order);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/reassign")
    public Result<RepairOrder> reassignOrder(@PathVariable String orderId) {
        try {
            RepairOrder order = orderService.getOrderDetail(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (order.getStatus() != 0 && order.getStatus() != 5) {
                return Result.error("只有待接单或超时释放状态的订单才能重派");
            }
            orderService.reassignOrder(order);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
