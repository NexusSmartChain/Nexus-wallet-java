package com.chain.common.ret;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class KeyVal<K extends Serializable,V extends Serializable> implements Serializable{
	private static final long serialVersionUID=6313698375299969865L;
	private K key;
	private V val;

	public KeyVal(){}

	public KeyVal(K k, V v){
		this.key=k;
		this.val=v;
	}

	public K getKey(){
		return key;
	}

	public void setKey(K key){
		this.key=key;
	}

	public V getVal(){
		return val;
	}

	public void setVal(V val){
		this.val=val;
	}

	@Override
	public boolean equals(Object o){
		if(this==o) return true;

		if(o==null || getClass()!=o.getClass()) return false;

		KeyVal<?, ?> keyValue=(KeyVal<?, ?>) o;

		return new EqualsBuilder().append(key, keyValue.key).append(val, keyValue.val).isEquals();
	}

	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(key).append(val).toHashCode();
	}
}
