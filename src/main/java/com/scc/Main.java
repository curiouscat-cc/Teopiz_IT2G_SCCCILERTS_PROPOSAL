package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.ui.Authentication;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting SCC-CI Lab & Equipment Resource Tracking System...");

        DatabaseInitializer.initialize();

        Authentication.start();
    }
}