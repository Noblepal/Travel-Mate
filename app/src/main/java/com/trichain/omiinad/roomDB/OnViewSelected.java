package com.trichain.omiinad.roomDB;

public interface OnViewSelected {
    public void onViewSelected(String start,String stop);
    public void onViewSelected(String Name, Double latitude, Double longitude);
    public void onViewSelected(String about);
}
