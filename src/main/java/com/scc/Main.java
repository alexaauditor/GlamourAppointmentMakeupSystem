
package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.initializeDatabase();

        Scanner sc = new Scanner(System.in);
        UserDao userDao = new UserDao();

        try {
            Authentication authentication =
                    new Authentication(sc, userDao);

            authentication.start();

        } finally {
            sc.close();
        }
    }
}
