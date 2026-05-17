package com.repair.controller;

import com.repair.common.Result;
import com.repair.entity.Worker;
import com.repair.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workers")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @GetMapping
    public Result<List<Worker>> getAllWorkers() {
        return Result.success(workerService.getAllWorkers());
    }

    @GetMapping("/{workerId}")
    public Result<Worker> getWorkerById(@PathVariable String workerId) {
        Worker worker = workerService.getWorkerById(workerId);
        if (worker == null) {
            return Result.error("师傅不存在");
        }
        return Result.success(worker);
    }

    @GetMapping("/low-rated")
    public Result<List<Worker>> getLowRatedWorkers(@RequestParam(defaultValue = "4.0") double threshold) {
        return Result.success(workerService.getLowRatedWorkers(threshold));
    }
}
