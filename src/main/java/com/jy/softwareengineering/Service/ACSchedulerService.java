package com.jy.softwareengineering.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ACSchedulerService {
//    线程池，因为要求一次只能服务三个，所以设置3个线程
    private final ScheduledThreadPoolExecutor taskScheduler = new ScheduledThreadPoolExecutor(3);
//    这里是等待队列，用于存储等待执行的任务
    private final Queue<ACService.RoomTempTask> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Map<Integer, ACService.RoomTempTask> allTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 每30秒触发时间片轮转
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::rotateTasks, 30, 30, TimeUnit.SECONDS);
    }
//将任务添加到任务队列，队列满了就将任务加入等待队列
    public synchronized void addTask(ACService.RoomTempTask task) {
        allTasks.put(task.getRoomId(), task);
        if (taskScheduler.getActiveCount() < 3) {
            startTask(task);
        } else {
            waitingQueue.add(task);
        }
    }

    private void startTask(ACService.RoomTempTask task) {
        task.resume(); // 恢复任务标志
        long delay = task.getDelayInSeconds(); // 根据风速决定调度间隔
        ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(
                task, 0, delay, TimeUnit.SECONDS
        );
        task.setFuture(future);
    }

//  这里是RR的核心
    private synchronized void rotateTasks() {
        //获取当前运行中的任务
        List<ACService.RoomTempTask> runningTasks = allTasks.values().stream()
                .filter(task -> !task.isDone())
                .collect(Collectors.toList());

        //暂停任务，加入等待队列
        for (ACService.RoomTempTask task : runningTasks) {
            task.pause();
            waitingQueue.add(task);
        }

        //重新从等待队列中选择优先调度的任务：按风速降序
        List<ACService.RoomTempTask> sortedTasks = new ArrayList<>();
        for (int priority = 2; priority >= 0; priority--) {
            for (ACService.RoomTempTask task : waitingQueue) {
                if (task.getPriority() == priority && !task.isDone()) {
                    sortedTasks.add(task);
                    if (sortedTasks.size() >= 3) break;
                }
            }
            if (sortedTasks.size() >= 3) break;
        }

        //启动选中的任务并从等待队列中移除
        for (ACService.RoomTempTask task : sortedTasks) {
            waitingQueue.remove(task);
            startTask(task);
        }
    }

//移除任务
    public synchronized void removeTask(int roomId) {
        ACService.RoomTempTask task = allTasks.remove(roomId);
        if (task != null) {
            task.cancel();
            if (!waitingQueue.isEmpty()) {
                startTask(waitingQueue.poll());
            }
        }
    }
}
