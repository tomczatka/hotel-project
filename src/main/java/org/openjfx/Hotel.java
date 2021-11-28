package org.openjfx;

public class Hotel {
    int portNumber;
    int rooms1;
    int rooms2;
    int rooms3;

    public Hotel(int portNumber, int rooms1, int rooms2, int rooms3) {
        this.portNumber = portNumber;
        this.rooms1 = rooms1;
        this.rooms2 = rooms2;
        this.rooms3 = rooms3;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getRooms1() {
        return rooms1;
    }

    public void setRooms1(int rooms1) {
        this.rooms1 = rooms1;
    }

    public int getRooms2() {
        return rooms2;
    }

    public void setRooms2(int rooms2) {
        this.rooms2 = rooms2;
    }

    public int getRooms3() {
        return rooms3;
    }

    public void setRooms3(int rooms3) {
        this.rooms3 = rooms3;
    }
}
