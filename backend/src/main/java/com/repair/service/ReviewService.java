package com.repair.service;

import com.repair.entity.RepairOrder;
import com.repair.entity.Review;
import com.repair.entity.Worker;
import com.repair.entity.enums.OrderStatus;
import com.repair.store.DataStore;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    public Review createReview(String orderId, String userId, Integer rating, String comment) {
        RepairOrder order = DataStore.ORDER_MAP.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("只能评价自己的订单");
        }

        if (!order.getStatus().equals(OrderStatus.COMPLETED.getCode())) {
            throw new RuntimeException("订单未完成，无法评价");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }

        String reviewId = UUID.randomUUID().toString().replace("-", "");
        Review review = new Review();
        review.setId(reviewId);
        review.setOrderId(orderId);
        review.setUserId(userId);
        review.setWorkerId(order.getWorkerId());
        review.setRating(rating);
        review.setComment(comment);
        review.setCreateTime(new Date());

        DataStore.REVIEW_MAP.put(reviewId, review);

        updateWorkerRating(order.getWorkerId(), rating);

        return review;
    }

    private void updateWorkerRating(String workerId, Integer newRating) {
        Worker worker = DataStore.WORKER_MAP.get(workerId);
        if (worker != null) {
            List<Review> workerReviews = DataStore.REVIEW_MAP.values().stream()
                    .filter(r -> r.getWorkerId().equals(workerId))
                    .collect(Collectors.toList());

            double totalRating = workerReviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();

            double avgRating = totalRating / workerReviews.size();
            avgRating = Math.round(avgRating * 10.0) / 10.0;
            worker.setRating(avgRating);

            if (newRating <= 2) {
                worker.setServiceScore(Math.max(0, worker.getServiceScore() - 5));
            } else if (newRating >= 4) {
                worker.setServiceScore(Math.min(100, worker.getServiceScore() + 2));
            }
        }
    }

    public List<Review> getWorkerReviews(String workerId) {
        return DataStore.REVIEW_MAP.values().stream()
                .filter(r -> r.getWorkerId().equals(workerId))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
}
