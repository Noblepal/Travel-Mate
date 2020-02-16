package com.trichain.omiinad.room;

public interface OnViewSelected {
    public void onViewSelected(String start,String stop);
    public void onViewSelected(String Name, Double latitude, Double longitude);
    public void onViewSelected(String about);

    void onViewSelected(String requestPermissions, String s, String s1);
}
