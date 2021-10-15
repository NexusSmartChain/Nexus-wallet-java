package com.chain.common.ret;

import java.io.Serializable;


public interface Code<T extends Serializable>{
	/**
	 * enum返回统一代码
	 * @return 枚举代码
	 */
	T code();
}

