package com.repair.service;

import com.repair.dto.OrderCreateDTO;
import com.repair.entity.RepairOrder;
import com.repair.entity.User;
import com.repair.entity.Worker;
import com.repair.entity.enums.OrderStatus;
import com.repair.store.DataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Value("${repair.order.timeout-unaccepted:60000}")
    private Long timeoutUnaccepted;

    @Value("${repair.order.timeout-unvisited:300000}")
    private Long timeoutUnvisited;

    @Value("${repair.order.cancel-penalty-before-accept:0}")
    private Integer cancelPenaltyBeforeAccept;

    @Value("${repair.order.cancel-penalty-after-accept:10}")
    private Integer cancelPenaltyAfterAccept;

    @Value("${repair.order.cancel-penalty-after-visited:20}")
    private Integer cancelPenaltyAfterVisited;

    public RepairOrder createOrder(OrderCreateDTO dto) {
        User user = DataStore.USER_MAP.get(dto.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        RepairOrder order = new RepairOrder();
        String orderId = UUID.randomUUID().toString().replace("-", "");
        order.setId(orderId);
        order.setOrderNo("RO" + System.currentTimeMillis());
        order.setUserId(user.getId());
        order.setUserName(user.getName());
        order.setUserPhone(user.getPhone());
        order.setType(dto.getType());
        order.setTitle(dto.getTitle());
        order.setDescription(dto.getDescription());
        order.setArea(dto.getArea() != null ? dto.getArea() : user.getArea());
        order.setAddress(dto.getAddress() != null ? dto.getAddress() : user.getAddress());
        order.setLatitude(dto.getLatitude());
        order.setLongitude(dto.getLongitude());
        order.setUrgent(dto.getUrgent() != null ? dto.getUrgent() : false);
        order.setStatus(OrderStatus.PENDING_ACCEPT.getCode());
        order.setStatusDesc(OrderStatus.PENDING_ACCEPT.getDesc());
        order.setCreateTime(new Date());
        order.setTimeoutUnaccepted(timeoutUnaccepted);
        order.setTimeoutUnvisited(timeoutUnvisited);

        DataStore.ORDER_MAP.put(orderId, order);
        addToOrderPool(order);

        if (order.getUrgent()) {
            pushToHighRatedWorkers(order);
        }

        return order;
    }

    private void addToOrderPool(RepairOrder order) {
        String area = order.getArea();
        String type = order.getType();

        DataStore.ORDER_POOL_BY_AREA.computeIfAbsent(area, k -> new CopyOnWriteArrayList<>()).add(order);
        DataStore.ORDER_POOL_BY_TYPE.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(order);
    }

    private void pushToHighRatedWorkers(RepairOrder order) {
        List<Worker> highRatedWorkers = DataStore.WORKER_MAP.values().stream()
                .filter(w -> w.getStatus() == 1)
                .filter(w -> w.getRating() >= 4.5)
                .filter(w -> w.getSkills().contains(order.getType()))
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(5)
                .collect(Collectors.toList());

        List<String> pushedWorkerIds = highRatedWorkers.stream()
                .map(Worker::getId)
                .collect(Collectors.toList());
        order.setPushedWorkerIds(pushedWorkerIds);

        System.out.println("紧急订单 " + order.getOrderNo() + " 已推送给 " + highRatedWorkers.size() + " 位高评分师傅: " + pushedWorkerIds);
    }

    public List<RepairOrder> getAvailableOrders(String workerId) {
        Worker worker = DataStore.WORKER_MAP.get(workerId);
        if (worker == null) {
            throw new RuntimeException("师傅不存在");
        }

        List<RepairOrder> areaOrders = DataStore.ORDER_POOL_BY_AREA.getOrDefault(worker.getArea(), new CopyOnWriteArrayList<>());
        List<String> skills = worker.getSkills();

        return areaOrders.stream()
                .filter(order -> order.getStatus().equals(OrderStatus.PENDING_ACCEPT.getCode()))
                .filter(order -> skills.contains(order.getType()))
                .sorted((a, b) -> {
                    if (a.getUrgent() && !b.getUrgent()) return -1;
                    if (!a.getUrgent() && b.getUrgent()) return 1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                })
                .collect(Collectors.toList());
    }

    public synchronized RepairOrder acceptOrder(String orderId, String workerId) {
        RepairOrder order = DataStore.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(OrderStatus.PENDING_ACCEPT.getCode())) {
            throw new RuntimeException("订单状态不允许接单，当前状态: " + order.getStatusDesc());
        }

        Worker worker = DataStore.WORKER_MAP.get(workerId);
        if (worker == null) {
            throw new RuntimeException("师傅不存在");
        }

        if (worker.getStatus() != 1) {
            throw new RuntimeException("师傅当前状态不可接单");
        }

        if (worker.getCurrentLoad() >= worker.getMaxLoad()) {
            throw new RuntimeException("师傅当前接单已达上限，无法接单");
        }

        if (!worker.getSkills().contains(order.getType())) {
            throw new RuntimeException("师傅不具备该维修类型技能");
        }

        double distance = calculateDistance(
                worker.getLatitude(), worker.getLongitude(),
                order.getLatitude(), order.getLongitude()
        );
        if (distance > 20.0) {
            throw new RuntimeException("订单距离过远（超过20公里），无法接单");
        }

        order.setStatus(OrderStatus.ACCEPTED.getCode());
        order.setStatusDesc(OrderStatus.ACCEPTED.getDesc());
        order.setWorkerId(worker.getId());
        order.setWorkerName(worker.getName());
        order.setWorkerPhone(worker.getPhone());
        order.setAcceptTime(new Date());

        worker.setCurrentLoad(worker.getCurrentLoad() + 1);

        removeFromOrderPool(order);

        DataStore.WORKER_ORDERS.computeIfAbsent(workerId, k -> new CopyOnWriteArrayList<>()).add(order);

        return order;
    }

    private void removeFromOrderPool(RepairOrder order) {
        CopyOnWriteArrayList<RepairOrder> areaList = DataStore.ORDER_POOL_BY_AREA.get(order.getArea());
        if (areaList != null) {
            areaList.removeIf(o -> o.getId().equals(order.getId()));
        }

        CopyOnWriteArrayList<RepairOrder> typeList = DataStore.ORDER_POOL_BY_TYPE.get(order.getType());
        if (typeList != null) {
            typeList.removeIf(o -> o.getId().equals(order.getId()));
        }
    }

    public RepairOrder confirmVisit(String orderId, String workerId) {
        RepairOrder order = DataStore.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(OrderStatus.ACCEPTED.getCode())) {
            throw new RuntimeException("订单状态不允许确认上门，当前状态: " + order.getStatusDesc());
        }

        if (!order.getWorkerId().equals(workerId)) {
            throw new RuntimeException("该订单不属于当前师傅");
        }

        order.setStatus(OrderStatus.VISITED.getCode());
        order.setStatusDesc(OrderStatus.VISITED.getDesc());
        order.setVisitTime(new Date());

        return order;
    }

    public RepairOrder completeOrder(String orderId, String workerId) {
        RepairOrder order = DataStore.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(OrderStatus.VISITED.getCode())) {
            throw new RuntimeException("订单状态不允许完成，当前状态: " + order.getStatusDesc());
        }

        if (!order.getWorkerId().equals(workerId)) {
            throw new RuntimeException("该订单不属于当前师傅");
        }

        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setStatusDesc(OrderStatus.COMPLETED.getDesc());
        order.setCompleteTime(new Date());

        Worker worker = DataStore.WORKER_MAP.get(workerId);
        if (worker != null) {
            worker.setCurrentLoad(Math.max(0, worker.getCurrentLoad() - 1));
            worker.setOrderCount(worker.getOrderCount() + 1);
        }

        return order;
    }

    public RepairOrder cancelOrder(String orderId, String userId) {
        RepairOrder order = DataStore.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("只能取消自己的订单");
        }

        if (order.getStatus().equals(OrderStatus.COMPLETED.getCode()) ||
                order.getStatus().equals(OrderStatus.CANCELLED.getCode()) ||
                order.getStatus().equals(OrderStatus.TIMEOUT_RELEASE.getCode())) {
            throw new RuntimeException("当前订单状态不允许取消");
        }

        Integer penalty = 0;
        if (order.getStatus().equals(OrderStatus.PENDING_ACCEPT.getCode())) {
            penalty = cancelPenaltyBeforeAccept;
            removeFromOrderPool(order);
        } else if (order.getStatus().equals(OrderStatus.ACCEPTED.getCode())) {
            penalty = cancelPenaltyAfterAccept;
        } else if (order.getStatus().equals(OrderStatus.VISITED.getCode())) {
            penalty = cancelPenaltyAfterVisited;
        }

        if (order.getWorkerId() != null) {
            Worker worker = DataStore.WORKER_MAP.get(order.getWorkerId());
            if (worker != null) {
                worker.setServiceScore(Math.max(0, worker.getServiceScore() - penalty));
                worker.setCurrentLoad(Math.max(0, worker.getCurrentLoad() - 1));
            }
        }

        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setStatusDesc(OrderStatus.CANCELLED.getDesc() + (penalty > 0 ? "（扣" + penalty + "服务分）" : ""));
        order.setCancelTime(new Date());

        return order;
    }

    public List<RepairOrder> getWorkerOrders(String workerId) {
        return DataStore.WORKER_ORDERS.getOrDefault(workerId, new CopyOnWriteArrayList<>())
                .stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }

    public List<RepairOrder> getUserOrders(String userId) {
        return DataStore.ORDER_MAP.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }

    public RepairOrder getOrderDetail(String orderId) {
        return DataStore.ORDER_MAP.get(orderId);
    }

    public void releaseTimeoutOrder(RepairOrder order) {
        if (order.getWorkerId() != null) {
            Worker worker = DataStore.WORKER_MAP.get(order.getWorkerId());
            if (worker != null) {
                worker.setCurrentLoad(Math.max(0, worker.getCurrentLoad() - 1));
            }
        }

        order.setStatus(OrderStatus.TIMEOUT_RELEASE.getCode());
        order.setStatusDesc(OrderStatus.TIMEOUT_RELEASE.getDesc());

        removeFromOrderPool(order);
    }

    public void reassignOrder(RepairOrder order) {
        order.setStatus(OrderStatus.PENDING_ACCEPT.getCode());
        order.setStatusDesc(OrderStatus.PENDING_ACCEPT.getDesc());
        order.setWorkerId(null);
        order.setWorkerName(null);
        order.setWorkerPhone(null);
        order.setAcceptTime(null);
        order.setCreateTime(new Date());

        addToOrderPool(order);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) {
            return 0;
        }
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lon1) - Math.toRadians(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        return Math.round(s * 10000d) / 10000d;
    }

    public List<RepairOrder> getAllOrders() {
        return new ArrayList<>(DataStore.ORDER_MAP.values());
    }
}
