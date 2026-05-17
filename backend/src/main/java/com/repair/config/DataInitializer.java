package com.repair.config;

import com.repair.entity.User;
import com.repair.entity.Worker;
import com.repair.store.DataStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        initUsers();
        initWorkers();
        System.out.println("数据初始化完成！");
    }

    private void initUsers() {
        User user1 = new User();
        user1.setId("u001");
        user1.setName("张三");
        user1.setPhone("13800138001");
        user1.setAddress("朝阳区望京SOHO");
        user1.setArea("朝阳区");
        user1.setCreateTime(new Date());
        DataStore.USER_MAP.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId("u002");
        user2.setName("李四");
        user2.setPhone("13800138002");
        user2.setAddress("海淀区中关村");
        user2.setArea("海淀区");
        user2.setCreateTime(new Date());
        DataStore.USER_MAP.put(user2.getId(), user2);

        User user3 = new User();
        user3.setId("u003");
        user3.setName("王五");
        user3.setPhone("13800138003");
        user3.setAddress("西城区金融街");
        user3.setArea("西城区");
        user3.setCreateTime(new Date());
        DataStore.USER_MAP.put(user3.getId(), user3);
    }

    private void initWorkers() {
        Worker worker1 = new Worker();
        worker1.setId("w001");
        worker1.setName("李师傅");
        worker1.setPhone("13900139001");
        worker1.setArea("朝阳区");
        worker1.setLatitude(39.9892);
        worker1.setLongitude(116.4714);
        worker1.setSkills(Arrays.asList("空调维修", "冰箱维修", "洗衣机维修"));
        worker1.setCurrentLoad(0);
        worker1.setMaxLoad(3);
        worker1.setRating(4.8);
        worker1.setOrderCount(128);
        worker1.setServiceScore(100);
        worker1.setCreateTime(new Date());
        worker1.setStatus(1);
        DataStore.WORKER_MAP.put(worker1.getId(), worker1);

        Worker worker2 = new Worker();
        worker2.setId("w002");
        worker2.setName("王师傅");
        worker2.setPhone("13900139002");
        worker2.setArea("朝阳区");
        worker2.setLatitude(39.9921);
        worker2.setLongitude(116.4680);
        worker2.setSkills(Arrays.asList("水电维修", "灯具安装", "管道疏通"));
        worker2.setCurrentLoad(0);
        worker2.setMaxLoad(3);
        worker2.setRating(4.6);
        worker2.setOrderCount(96);
        worker2.setServiceScore(95);
        worker2.setCreateTime(new Date());
        worker2.setStatus(1);
        DataStore.WORKER_MAP.put(worker2.getId(), worker2);

        Worker worker3 = new Worker();
        worker3.setId("w003");
        worker3.setName("张师傅");
        worker3.setPhone("13900139003");
        worker3.setArea("海淀区");
        worker3.setLatitude(39.9847);
        worker3.setLongitude(116.3046);
        worker3.setSkills(Arrays.asList("空调维修", "水电维修", "家电维修"));
        worker3.setCurrentLoad(0);
        worker3.setMaxLoad(3);
        worker3.setRating(4.9);
        worker3.setOrderCount(256);
        worker3.setServiceScore(100);
        worker3.setCreateTime(new Date());
        worker3.setStatus(1);
        DataStore.WORKER_MAP.put(worker3.getId(), worker3);

        Worker worker4 = new Worker();
        worker4.setId("w004");
        worker4.setName("赵师傅");
        worker4.setPhone("13900139004");
        worker4.setArea("海淀区");
        worker4.setLatitude(39.9912);
        worker4.setLongitude(116.3125);
        worker4.setSkills(Arrays.asList("家具安装", "门窗维修", "锁具服务"));
        worker4.setCurrentLoad(0);
        worker4.setMaxLoad(3);
        worker4.setRating(4.3);
        worker4.setOrderCount(67);
        worker4.setServiceScore(88);
        worker4.setCreateTime(new Date());
        worker4.setStatus(1);
        DataStore.WORKER_MAP.put(worker4.getId(), worker4);

        Worker worker5 = new Worker();
        worker5.setId("w005");
        worker5.setName("刘师傅");
        worker5.setPhone("13900139005");
        worker5.setArea("西城区");
        worker5.setLatitude(39.9128);
        worker5.setLongitude(116.3634);
        worker5.setSkills(Arrays.asList("空调维修", "冰箱维修", "洗衣机维修", "水电维修"));
        worker5.setCurrentLoad(0);
        worker5.setMaxLoad(3);
        worker5.setRating(4.7);
        worker5.setOrderCount(189);
        worker5.setServiceScore(98);
        worker5.setCreateTime(new Date());
        worker5.setStatus(1);
        DataStore.WORKER_MAP.put(worker5.getId(), worker5);
    }
}
