package com.sysone.ogamza.view;

import javafx.scene.layout.BorderPane;

public class MainLayout extends BorderPane {

    public MainLayout(){
        setLeft(new SideBar());
        setTop(new Header());
    }
}
