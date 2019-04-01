package com.example.a98.transportdemo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.amap.api.location.AMapLocation;

/**
 * 用于记录一条轨迹，包括起点、终点、轨迹中间点、距离、耗时、平均速度、时间
 * 
 * @author ligen
 * 
 */
public class PathRecord {
	private AMapLocation mStartPoint;
	private AMapLocation mEndPoint;
	private List<AMapLocation> mPathLinePoints = new ArrayList<AMapLocation>();
	private String mDistance;
	private String mDuration;
	private String mAveragespeed;
	private String mDate;
	private int mId = 0;

	public PathRecord() {

	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public AMapLocation getStartpoint() {
		return mStartPoint;
	}

	public void setStartpoint(AMapLocation startpoint) {
		this.mStartPoint = startpoint;
	}

	public AMapLocation getEndpoint() {
		return mEndPoint;
	}

	public void setEndpoint(AMapLocation endpoint) {
		this.mEndPoint = endpoint;
	}

	public List<AMapLocation> getPathline() {
		return mPathLinePoints;
	}

	public void setPathline(List<AMapLocation> pathline) {
		this.mPathLinePoints = pathline;
	}

	public String getDistance() {
		return mDistance;
	}

	public void setDistance(String distance) {
		this.mDistance = distance;
	}

	public String getDuration() {
		return mDuration;
	}

	public void setDuration(String duration) {
		this.mDuration = duration;
	}

	public String getAveragespeed() {
		return mAveragespeed;
	}

	public void setAveragespeed(String averagespeed) {
		this.mAveragespeed = averagespeed;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}

	public void addpoint(AMapLocation point) {
		mPathLinePoints.add(point);
	}

	private String getArea(String dis){
		long distance=Long.parseLong(dis);
		long area= (long) (Math.pow(distance,2 )/4);
		return Long.toString(area);
	}
	@Override
	public String toString() {
		StringBuilder record = new StringBuilder();
//		record.append("recordSize:" + getPathline().size() + ", ");
		record.append("距离: " + getDistance() + "米, ");
		record.append("时间: " + getDuration() + "秒, ");
		record.append("面积: " + getArea(getDistance()) + "平方");

		return record.toString();
	}
}
