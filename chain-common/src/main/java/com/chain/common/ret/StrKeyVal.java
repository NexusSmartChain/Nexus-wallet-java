package com.chain.common.ret;

import java.io.Serializable;


public class StrKeyVal<V extends Serializable> extends KeyVal<String,V> implements Serializable{
	private static final long serialVersionUID=6313698375299969865L;

	public StrKeyVal(){}
	public StrKeyVal(String s, V v){
		super(s, v);
	}
}
