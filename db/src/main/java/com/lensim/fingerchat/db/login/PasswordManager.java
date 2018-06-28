package com.lensim.fingerchat.db.login;


import com.lensim.fingerchat.db.GreenDaoManager;
import com.lensim.fingerchat.db.greendao.PasswordDao;
import java.util.List;


/**
 * Created by ll147996 on 2018/1/19.
 *
 */

public class PasswordManager {

    private static final PasswordManager passwordRepository = new PasswordManager();
    private volatile Password password;
    private PasswordDao passwordDao;

    private PasswordManager() {
        passwordDao = GreenDaoManager.getInstance().getSession().getPasswordDao();
    }


    public static PasswordManager getInstance() {
        return passwordRepository;
    }


    public Password getPassword() {
        if (password == null) {
            password = queryPassword();
        }
        return password;
    }

    public void setPassword(String pw, String secretkey) {
        addPassword(pw, secretkey);
    }

    public void clearPassword() {
        if (password != null) {
            passwordDao.delete(password);
            password = null;
        }
    }


    private synchronized void addPassword(String pw, String secretkey) {
        //查询所有
        if (passwordDao.loadAll() != null && passwordDao.loadAll().size() > 0) {
            passwordDao.deleteAll();
        }
            Password password = new Password();
            password.setPassword(pw);
            password.setSecretkey(secretkey);
            password.setTime(System.currentTimeMillis());
            this.password = password;
            passwordDao.insert(password);

    }


    private Password queryPassword() {
        List<Password> userList = passwordDao.queryBuilder()
//                .where(PasswordDao.Properties.Id.notEq(1))
            .limit(1)
            .build()
            .list();
        if (userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }
}

