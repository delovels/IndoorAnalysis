package com.bupt.indoorPosition.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class InspectDisplay implements Serializable {
	private int floor;
	private String description;
	private int count;

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
