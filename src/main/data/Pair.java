package main.data;

public class Pair {
private Integer Key;
private Integer Value;
public Integer getKey() {
	return Key;
}
public void setKey(Integer key) {
	Key = key;
}
public Integer getValue() {
	return Value;
}
public void setValue(Integer value) {
	Value = value;
}

public Pair(Integer Key,Integer Value){
	this.Key=Key;
	this.Value=Value;
}
}
