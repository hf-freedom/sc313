package com.repair.service;

import com.repair.entity.Worker;
import com.repair.store.DataStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    public List<Worker> getAllWorkers() {
        return new ArrayList<>(DataStore.WORKER_MAP.values());
    }

    public Worker getWorkerById(String workerId) {
        return DataStore.WORKER_MAP.get(workerId);
    }

    public List<Worker> getLowRatedWorkers(double threshold) {
        return DataStore.WORKER_MAP.values().stream()
                .filter(w -> w.getStatus() == 1)
                .filter(w -> w.getRating() < threshold)
                .sorted((a, b) -> Double.compare(a.getRating(), b.getRating()))
                .collect(Collectors.toList());
    }
}
