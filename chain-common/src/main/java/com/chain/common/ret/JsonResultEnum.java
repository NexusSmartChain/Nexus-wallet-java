package com.chain.common.ret;


public enum JsonResultEnum implements Code<String>{
	/** 200 成功,默认值 */NONE( "200", "" ),
	/** 200 成功 */OK( "200", "success" ),
	/** 201 请求失败 */FAIL( "500", "error" ),
	/** 400 参数错误 */BAD_REQUEST( "400", "参数错误" ),
	/** 401 没有登录 */NOT_LOGIN( "401", "没有登录" ),
	/** 403 没有权限 */FORBIDDEN( "403", "没有权限" ),
	/** 404 没有找到 */NOT_FOUND( "404", "没有找到" ),
	/** 401 在别处登录 */ LOG_IN_ELSEWHERE("401","账号在别处登录");
	;
	private String code;
	private String message;

	JsonResultEnum(String code, String name ){
		this.code=code;
		this.message=name;
	}

	/**
	 * enum返回统一代码
	 * @return 枚举代码
	 */
	@Override
	public String code(){
		return code;
	}

	public String getCode(){
		return code;
	}

	public String getMessage(){
		return message;
	}
}