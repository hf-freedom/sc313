package com.repair.task;

import com.repair.entity.Worker;
import com.repair.store.DataStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkerRatingTask {

    @Scheduled(fixedRate = 60000)
    public void scanLowRatedWorkers() {
        List<Worker> lowRatedWorkers = DataStore.WORKER_MAP.values().stream()
                .filter(w -> w.getStatus() == 1)
                .filter(w -> w.getRating() < 4.0 || w.getServiceScore() < 80)
                .collect(Collectors.toList());

        if (!lowRatedWorkers.isEmpty()) {
            System.out.println("扫描到低评分/低服务分师傅: " + lowRatedWorkers.size() + " 位");
            lowRatedWorkers.forEach(w -> {
                System.out.printf("  师傅: %s, 评分: %.1f, 服务分: %d%n",
                        w.getName(), w.getRating(), w.getServiceScore());
                if (w.getServiceScore() < 60) {
                    w.setStatus(0);
                    System.out.println("    已暂停接单资格");
                }
            });
        }
    }
}
