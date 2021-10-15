package com.chain.common.ret;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class JsonResult<T> implements Serializable {
	private static final long serialVersionUID = 6838141783293262136L;

	/**
	 * 放入常量池，直接用，简单返回推荐，不用new
	 **/
	public static final String STRING_OK=codeByEnum(JsonResultEnum.OK);
	public static final String STRING_FAIL=codeByEnum(JsonResultEnum.FAIL);
	public static final String STRING_FORBIDDEN=codeByEnum(JsonResultEnum.FORBIDDEN);
	public static final String STRING_NOT_LOGIN=codeByEnum(JsonResultEnum.NOT_LOGIN);
	public static final String STRING_BAD_REQUEST=codeByEnum(JsonResultEnum.BAD_REQUEST);
	public static final String STRING_NOT_FOUNT=codeByEnum(JsonResultEnum.NOT_FOUND);


	/**
	 * 状态码
	 **/
	protected String code;
	/**
	 * 返回信息
	 **/
	protected String msg;
	/**
	 * 数据
	 **/
	protected T data;
	public static final JsonResult SUCCESS = new JsonResult();

	public JsonResult() {
		this((T) null);
	}

	public JsonResult(T data) {
		this.code = "0";
		this.msg = "success";
		this.data = data;
	}
	public JsonResult<T> msg(String message){
		this.msg=message;
		return this;
	}

	public JsonResult<T> data(T data){
		this.data=data;
		return this;
	}
	public JsonResult<T> code(String code){
		this.code= code;
		return this;
	}

	public JsonResult(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public JsonResult(String code, String resutMsg, T data) {
		this.code = code;
		this.msg = resutMsg;
		this.data = data;
	}

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "JsonResult [code=" + this.code + ", msg=" + this.msg + ", data=" + JSONObject.toJSON(this.data) + "]";
	}

	public static <T> JsonResult<T> result(T data, String code, String msg) {
		return new JsonResult<>(code, msg, data);
	}
	public static <T> JsonResult<T> success(T data) {
		return new JsonResult<>(JsonResultEnum.OK.code(), JsonResultEnum.OK.getMessage(), data);
	}

	private JsonResult<T> code(JsonResultEnum result){
		this.code= result.code();
		this.msg= result.getMessage();
		return this;
	}
	public JsonResult<T> notLogin(){
		return code(JsonResultEnum.NOT_LOGIN);
	}
	public JsonResult<T> forbidden(){
		return code(JsonResultEnum.FORBIDDEN);
	}
	public JsonResult<T> ok(){
		return code(JsonResultEnum.OK);
	}
	public JsonResult<T> fail(){
		return code(JsonResultEnum.FAIL);
	}
	public JsonResult<T> badRequest(){
		return code(JsonResultEnum.BAD_REQUEST);
	}

	public JsonResult<T> notFound(){
		return code(JsonResultEnum.NOT_FOUND);
	}

	public static String code(String code,String msg){
		return String.format("{\"code\":%s,\"msg\":\"%s\"}", code, msg);
	}
	private static String codeByEnum(JsonResultEnum result){
		return code( result.code(),result.getMessage() );
	}
	public static String notLogin(String message){
		return code(JsonResultEnum.NOT_LOGIN.code(),message);
	}
	public static String forbidden(String message){
		return code(JsonResultEnum.FORBIDDEN.code(),message);
	}
	public static String ok(String message){
		return code(JsonResultEnum.OK.code(),message);
	}
	public static String fail(String message){
		return code(JsonResultEnum.FAIL.code(),message);
	}

	public static <T> JsonResult<T> newJsonResult(){
		return new JsonResult<>();
	}
	public static <T> JsonResult<Page<T>> newPageJsonResult(){
		return new JsonResult<>();
	}
	/**
	 * 返回结构:{code:200,data:{key:{}}}
	 * @param <T> 值对应的类型
	 * @return 返回结构:{code:200,data:{key:{}}}
	 */
	public static <T extends Serializable> KeyValResult<T> newKvResult(){
		return new KeyValResult<>();
	}
	public static MapJsonResult newMapJsonResult(){
		return new MapJsonResult();
	}
	public static ObjectJsonResult newObjectJsonResult(){
		return new ObjectJsonResult();
	}
	public static StringJsonResult newStringJsonResult(){
		return new StringJsonResult();
	}

	public static class MapJsonResult extends JsonResult<Map<String,Object>>{
		private static final long serialVersionUID=950225467116187917L;

		public MapJsonResult put(String key, Object value){
			if(data==null) data=new LinkedHashMap(16);
			data.put(key, value);
			return this;
		}

		@Override
		public MapJsonResult notLogin(){
			super.notLogin();
			return this;
		}

		@Override
		public MapJsonResult forbidden(){
			super.forbidden();
			return this;
		}

		@Override
		public MapJsonResult ok(){
			super.ok();
			return this;
		}

		@Override
		public MapJsonResult fail(){
			super.fail();
			return this;
		}

		@Override
		public MapJsonResult badRequest(){
			super.badRequest();
			return this;
		}

		@Override
		public MapJsonResult notFound(){
			super.notFound();
			return this;
		}
		@Override
		public MapJsonResult msg(String message){
			this.msg=message;
			return this;
		}
	}
	public static class KeyValResult<V extends Serializable> extends JsonResult<StrKeyVal<V>>{
		private static final long serialVersionUID=950225467116187917L;

		public KeyValResult<V> data(String key,V val){
			data=new StrKeyVal<>(key,val);
			return this;
		}
		public KeyValResult<V> data(V val){
			data=new StrKeyVal<>("key",val);
			return this;
		}
		@Override
		public KeyValResult<V> notLogin(){
			super.notLogin();
			return this;
		}

		@Override
		public KeyValResult<V> forbidden(){
			super.forbidden();
			return this;
		}

		@Override
		public KeyValResult<V> ok(){
			super.ok();
			return this;
		}

		@Override
		public KeyValResult<V> fail(){
			super.fail();
			return this;
		}

		@Override
		public KeyValResult<V> badRequest(){
			super.badRequest();
			return this;
		}

		@Override
		public KeyValResult<V> notFound(){
			super.notFound();
			return this;
		}
		@Override
		public KeyValResult<V> msg(String message){
			this.msg=message;
			return this;
		}
	}
	public static class ObjectJsonResult extends JsonResult<Object>{
		private static final long serialVersionUID=-7994606521154445848L;
	}
	public static class StringJsonResult extends JsonResult<String>{
		private static final long serialVersionUID=5573434663615772332L;
	}

}
