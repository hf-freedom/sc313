package com.repair.service;

import com.repair.entity.User;
import com.repair.store.DataStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    public List<User> getAllUsers() {
        return new ArrayList<>(DataStore.USER_MAP.values());
    }

    public User getUserById(String userId) {
        return DataStore.USER_MAP.get(userId);
    }
}
