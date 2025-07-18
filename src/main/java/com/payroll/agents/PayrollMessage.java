package com.payroll.agents;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages that robots pass to each other
 */
public class PayrollMessage {
    private String employeeId;
    private String fromAgent;
    private String toAgent;
    private Map<String, Object> data;
    private long timestamp;

    public PayrollMessage(String employeeId, String fromAgent) {
        this.employeeId = employeeId;
        this.fromAgent = fromAgent;
        this.data = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    // Add data to the message
    public PayrollMessage addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    // Get data from the message
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) this.data.get(key);
    }

    // Getters and setters
    public String getEmployeeId() { return employeeId; }
    public String getFromAgent() { return fromAgent; }
    public String getToAgent() { return toAgent; }
    public void setToAgent(String toAgent) { this.toAgent = toAgent; }
    public Map<String, Object> getData() { return data; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Message[from=%s, to=%s, employee=%s, data=%s]",
                fromAgent, toAgent, employeeId, data.keySet());
    }
}