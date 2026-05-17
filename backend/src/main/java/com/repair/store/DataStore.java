package com.repair.store;

import com.repair.entity.RepairOrder;
import com.repair.entity.Review;
import com.repair.entity.User;
import com.repair.entity.Worker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {
    public static final ConcurrentHashMap<String, User> USER_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Worker> WORKER_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, RepairOrder> ORDER_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, CopyOnWriteArrayList<RepairOrder>> ORDER_POOL_BY_AREA = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, CopyOnWriteArrayList<RepairOrder>> ORDER_POOL_BY_TYPE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Review> REVIEW_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, CopyOnWriteArrayList<RepairOrder>> WORKER_ORDERS = new ConcurrentHashMap<>();
}
