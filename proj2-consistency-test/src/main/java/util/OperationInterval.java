package util;
/**
 * Created by Hongji Li on Dec 12 2016.
 * */
public class OperationInterval {
	
	final String operation;
	
	private long startTime;
	
	private long endTime;
	
	private String key;
	
	private String value;
	
	final long clientIndex;
	
	final long commandIndex;
	
	OperationInterval(String name, long clientIndex, long commandIndex) {
		this.operation = name;
		this.clientIndex = clientIndex;
		this.commandIndex = commandIndex;
	}
	
	void setKey(String key) {
		this.key = key;
	}
	
	void setValue(String value) {
		this.value = value;
	}
	
	void setStartTime(long time) {
		this.startTime = time;
	}
	
	void setEndTime(long time) {
		this.endTime = time;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	@Override public int hashCode() {
		int hashCode = operation.hashCode();
		hashCode = hashCode * 31 + (int) clientIndex;
		hashCode = hashCode * 31 + (int) commandIndex;
		return hashCode;
	}

}
