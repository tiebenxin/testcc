package com.lensim.fingerchat.data.hexmeet;

import android.support.annotation.NonNull;
import com.lensim.fingerchat.data.repository.SPDataRepository;


/**
 * date on 2018/1/5
 * author ll147996
 * describe
 */

public class UserRepository {

    private SPDataRepository<User> spDataRepository;

    private User user;

    private UserRepository() {
        spDataRepository = new SPDataRepository<>();
    }

    public static UserRepository getInstance(){
        return UserRepository.Singleton.INSTANCE;
    }

    private static class Singleton{
        private static final UserRepository INSTANCE = new UserRepository();
    }

    public User getUser() {
        if (user != null) {
            return user;
        } else {
            return spDataRepository.getData(User.class);
        }
    }

    public void setUser(@NonNull User user) {
        this.user = user;
        spDataRepository.saveData(user);
    }
}
