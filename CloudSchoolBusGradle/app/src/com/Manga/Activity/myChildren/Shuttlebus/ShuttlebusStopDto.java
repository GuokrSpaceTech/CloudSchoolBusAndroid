package com.Manga.Activity.myChildren.Shuttlebus;

import java.util.List;

public class ShuttlebusStopDto {
    private int     lineid;
    private String  linename;
    private List<ShuttlebusStopItemDto> stop;

    public int getLineid() {
        return lineid;
    }

    public void setLineid(int lineid) {
        this.lineid = lineid;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public List<ShuttlebusStopItemDto> getStop() {
        return stop;
    }

    public void setStop(List<ShuttlebusStopItemDto> stop) {
        this.stop = stop;
    }
}
