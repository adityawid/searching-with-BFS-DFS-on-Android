package com.devjurnal.bfs;

import android.content.Context;
import android.graphics.Point;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devjurnal on 4/15/18.
 */

public class Vertex {
    private int data;
    private boolean visited;
    private List<Vertex> children;
    public TextView tv;

    public Vertex(int data, Context c, Point p) {
        this.data = data;
        this.visited = false;
        this.children = new ArrayList<Vertex>();

        this.tv = new TextView(c);
        this.tv.setText(String.valueOf(this.data));
        this.tv.setX(p.x);
        this.tv.setY(p.y);
        this.tv.setTextColor(0xff000000);
        this.tv.setTextSize(24);
        this.tv.setBackgroundColor(0xff888888);
        this.tv.setPadding(5, 5, 5, 5);
    }

    public List<Vertex> getChildren() {
        return this.children;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
        this.tv.setText(String.valueOf(this.data));
    }

    public void select() {
        this.tv.setBackgroundColor(0xffa4c0e4);
    }

    public void unselect() {
        this.tv.setBackgroundColor(0xffffffff);
    }

    public void addChild(Vertex v) {
        this.children.add(v);
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
        if (visited)
            this.tv.setBackgroundColor(0xff888888);
        else
            this.tv.setBackgroundColor(0xffffffff);
    }

    public boolean getVisited() {
        return this.visited;
    }
}
