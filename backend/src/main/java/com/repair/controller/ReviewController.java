package com.repair.controller;

import com.repair.common.Result;
import com.repair.entity.Review;
import com.repair.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Result<Review> createReview(@RequestBody Map<String, Object> body) {
        try {
            String orderId = (String) body.get("orderId");
            String userId = (String) body.get("userId");
            Integer rating = (Integer) body.get("rating");
            String comment = (String) body.get("comment");
            Review review = reviewService.createReview(orderId, userId, rating, comment);
            return Result.success(review);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}")
    public Result<List<Review>> getWorkerReviews(@PathVariable String workerId) {
        try {
            List<Review> reviews = reviewService.getWorkerReviews(workerId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
