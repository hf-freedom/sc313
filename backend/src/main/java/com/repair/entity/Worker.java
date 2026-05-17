package com.repair.entity;

import java.util.Date;
import java.util.List;

public class Worker {
    private String id;
    private String name;
    private String phone;
    private String area;
    private Double latitude;
    private Double longitude;
    private List<String> skills;
    private Integer currentLoad;
    private Integer maxLoad;
    private Double rating;
    private Integer orderCount;
    private Integer serviceScore;
    private Date createTime;
    private Integer status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public Integer getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Integer currentLoad) { this.currentLoad = currentLoad; }
    public Integer getMaxLoad() { return maxLoad; }
    public void setMaxLoad(Integer maxLoad) { this.maxLoad = maxLoad; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    public Integer getServiceScore() { return serviceScore; }
    public void setServiceScore(Integer serviceScore) { this.serviceScore = serviceScore; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
