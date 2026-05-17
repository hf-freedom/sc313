package com.repair.entity;

import java.util.Date;

public class RepairOrder {
    private String id;
    private String orderNo;
    private String userId;
    private String userName;
    private String userPhone;
    private String type;
    private String title;
    private String description;
    private String area;
    private String address;
    private Double latitude;
    private Double longitude;
    private Boolean urgent;
    private Integer status;
    private String statusDesc;
    private String workerId;
    private String workerName;
    private String workerPhone;
    private Date createTime;
    private Date acceptTime;
    private Date visitTime;
    private Date completeTime;
    private Date cancelTime;
    private Long timeoutUnaccepted;
    private Long timeoutUnvisited;
    private java.util.List<String> pushedWorkerIds;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Boolean getUrgent() { return urgent; }
    public void setUrgent(Boolean urgent) { this.urgent = urgent; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusDesc() { return statusDesc; }
    public void setStatusDesc(String statusDesc) { this.statusDesc = statusDesc; }
    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public String getWorkerPhone() { return workerPhone; }
    public void setWorkerPhone(String workerPhone) { this.workerPhone = workerPhone; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getAcceptTime() { return acceptTime; }
    public void setAcceptTime(Date acceptTime) { this.acceptTime = acceptTime; }
    public Date getVisitTime() { return visitTime; }
    public void setVisitTime(Date visitTime) { this.visitTime = visitTime; }
    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }
    public Date getCancelTime() { return cancelTime; }
    public void setCancelTime(Date cancelTime) { this.cancelTime = cancelTime; }
    public Long getTimeoutUnaccepted() { return timeoutUnaccepted; }
    public void setTimeoutUnaccepted(Long timeoutUnaccepted) { this.timeoutUnaccepted = timeoutUnaccepted; }
    public Long getTimeoutUnvisited() { return timeoutUnvisited; }
    public void setTimeoutUnvisited(Long timeoutUnvisited) { this.timeoutUnvisited = timeoutUnvisited; }
    public java.util.List<String> getPushedWorkerIds() { return pushedWorkerIds; }
    public void setPushedWorkerIds(java.util.List<String> pushedWorkerIds) { this.pushedWorkerIds = pushedWorkerIds; }
}
