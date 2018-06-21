package com.beetech.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.beetech.service.MeterService;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.xianhua.tempmonitor.po.MeterData;
import com.xianhua.tempmonitor.po.MeterDataDaily;

/**
 * 用于httpclient查询接口，向莱士的meter_t,meter_data两表提供数据
 * 
 * @param
 * @author lfh
 * @return
 */
@SuppressWarnings("deprecation")
public class MeterUtils extends BaseHttpUtils {
	public static String GET_TOKEN_URL = "http://120.55.102.113/userInfo/getToken";
	public static String GET_METER_DATA2 = "http://120.55.102.113/userInfo/getRemainById";
	public static String GET_METER_DATA3 = "http://120.55.102.113/userInfo/getAllRemainByLevel1";
	public static String GET_METER_USER = "http://120.55.102.113/userInfo/getBaseInfoById";
	public static String GET_METER_DAY = "http://120.55.102.113/userInfo/getDayData";

	/**
	 * 所有接口使用的前置步骤： 获取token
	 * 
	 * @return String token
	 */
	public static String getToken() {
		HttpPost post = null;
		try {
			post = new HttpPost(GET_TOKEN_URL);
			@SuppressWarnings("resource")
			HttpClient httpClient = new DefaultHttpClient();
			JsonObject object = new JsonObject();
			object.addProperty("userName", "lsdahua");
			object.addProperty("pw", "ADA6F2B5B7BA3F1531E4C1E291CBE7B0");
			// 构建消息实体
			StringEntity entity = new StringEntity(object.toString(), Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity1 = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retStr = EntityUtils.toString(entity1, Charsets.UTF_8);
				EntityUtils.consume(entity1);
				JSONObject retJson = JSONObject.fromObject(retStr);
				@SuppressWarnings("unused")
				int code = retJson.getInt("code");
				String token = retJson.getString("data");
				MeterService.setTOKEN(token);
				return token;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// {"meter_id":"12345678,431,234","read_time":"2017-09","token":"65ac6e3a-3d56-4bc1-81b6-460a82ba072c"}
	// {"code":1,"dataJs":[{"before_time":"2017-11-01","read_time":"2017-12-01","cha":1.94,"read_value":9085.74,"meter_address":"12345678",
	//"before_value":9083.8},{"before_time":"2017-11-01","read_time":"2017-12-01","cha":2.94,"read_value":9086.74,"meter_address":"431","before_value":9083.8}]],"meter_id":"12345678,431,234","read_time":"2017-12","token":"7fa3ac01-0b09-4ee0-b75d-3c0df1aad736"}
	/**
	 * 根据接口9查询一个或多个用户一个月每天的用电量。
	 * 
	 * @return 返回一个 List<MeterDataDaily> 对象，往后遍历它然后取出所有MeterDataDaily，
	 */
	public static List<MeterDataDaily> getMeterDataByDay(String token, String meterId, String readTime) {
		HttpPost post = null;
		try {
			post = new HttpPost(GET_METER_DAY);
			@SuppressWarnings("resource")
			HttpClient httpClient = new DefaultHttpClient();
			JsonObject object = new JsonObject();
			object.addProperty("meter_id", meterId);
			object.addProperty("token", token);
			object.addProperty("read_time", readTime);
			// 构建消息实体
			StringEntity entity = new StringEntity(object.toString(), Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity1 = response.getEntity();
			List<MeterDataDaily> meterDaily = Lists.newArrayList();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retStr = EntityUtils.toString(entity1, Charsets.UTF_8);
				EntityUtils.consume(entity1);
				JSONObject retJson = JSONObject.fromObject(retStr);
				for (Object res : retJson.getJSONArray("dataJs")) {
					JSONObject obj = (JSONObject) res;
					Object beforeTime = obj.get("before_time");
					Object beforeValue = obj.get("before_value");
					Object rTime = obj.get("read_time");
					Object readValue = obj.get("read_value");
					Object cha = obj.get("cha");
					String readValueStr = readValue+"";
					String beforeValueStr = beforeValue+"";
					Float readValueF = Float.parseFloat(readValueStr);
					Float beforeValueF = Float.parseFloat(beforeValueStr);
					Float chaF = Float.parseFloat(cha+"");
//					Double meterDayValue = Double.parseDouble((String)readValueStr)-Double.parseDouble((String)beforeValueStr);
//					Double meterDayValue = (Double)readValue-(Double)beforeValue;
//					read_time=2018-4-01
					Date readT = DateUtils.parseStringToDate((String)rTime, DateUtils.C_YYYY_MM_DD);
					Date beforeT = DateUtils.parseStringToDate((String)beforeTime, DateUtils.C_YYYY_MM_DD);
					MeterDataDaily mdd = new MeterDataDaily();
					mdd.setBeforeTime(beforeT);
					mdd.setBeforeValue(beforeValueF);
					mdd.setReadValue(readValueF);
					mdd.setReadTime(readT);
					mdd.setCha(chaF);
					mdd.setMeterAddress(Integer.parseInt(meterId));
					meterDaily.add(mdd);
				}
				return meterDaily;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 使用接口三 获取meterData电表值
	 * 
	 * @param token
	 * @return 所有电表的数据
	 */
	public static List<MeterData> getMeterData(String token, String meterId) {
		HttpPost post = null;
		try {
			post = new HttpPost(GET_METER_DATA3);
			@SuppressWarnings("resource")
			HttpClient httpClient = new DefaultHttpClient();
			JsonObject object = new JsonObject();
			object.addProperty("token", token);
			object.addProperty("level1", meterId);
			// 构建消息实体
			StringEntity entity = new StringEntity(object.toString(), Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity1 = response.getEntity();
			List<MeterData> meterDataList = Lists.newArrayList();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retStr = EntityUtils.toString(entity1, Charsets.UTF_8);
				EntityUtils.consume(entity1);
				JSONObject retJson = JSONObject.fromObject(retStr);
				for (Object res : retJson.getJSONArray("data")) {
					MeterData meterData = new MeterData();
					JSONObject obj = (JSONObject) res;
					Object meter_id = obj.get("meter_id");
					Object read_time = obj.get("read_time");
					Object total_e = obj.get("total_e");
					try {
						if (!(meter_id instanceof JSONNull) && null != meter_id && "null" != meter_id) {
							Integer meterId1 = Integer.parseInt((String) meter_id);// 电表的ID
							meterData.setMeterId(meterId1);
						}
						// JSONNull的处理
						if (!(read_time instanceof JSONNull) && null != read_time && "null" != read_time) {
							String read_time1 = (String) read_time;// 数据时间
							meterData.setReadTime(DateUtils.parseStringToDate(read_time1, "yyyy-MM-dd HH:mm:ss"));
						}
						if (!(total_e instanceof JSONNull) && null != total_e && "null" != total_e) {
							Float total_e1 = Float.parseFloat((String) total_e); // 电表已使用量
							meterData.setMeterUsed(total_e1);
						}
						// 若无法以限定格式解析json，则跳过下一个
					} catch (NumberFormatException e) {
						continue;
					}
					meterDataList.add(meterData);
				}

				return meterDataList;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用接口2获取指定meter_id的电表数据。
	 * 
	 * @param token
	 *            获取到的token
	 * @param meterId
	 *            需要传入指定电表号
	 * @return List<MeterData> meterDataList
	 */
	public static List<MeterData> getMeterData2(String token, Integer meterId) {
		HttpPost post = null;
		try {
			post = new HttpPost(GET_METER_DATA2);
			@SuppressWarnings("resource")
			HttpClient httpClient = new DefaultHttpClient();
			JsonObject object = new JsonObject();
			object.addProperty("token", token);
			object.addProperty("meter_id", meterId + "");
			// 构建消息实体
			StringEntity entity = new StringEntity(object.toString(), Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity1 = response.getEntity();
			List<MeterData> meterDataList = Lists.newArrayList();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retStr = EntityUtils.toString(entity1, Charsets.UTF_8);
				EntityUtils.consume(entity1);
				JSONObject retJson = JSONObject.fromObject(retStr);
				MeterData meterData = new MeterData();
				Object meter_id = retJson.get("meter_id");
				Object read_time = retJson.get("read_time");
				Object total_e = retJson.get("total_e");
				try {
					if (!(meter_id instanceof JSONNull) && null != meter_id && "null" != meter_id) {
						Integer meterId1 = Integer.parseInt((String) meter_id);// 电表的ID
						meterData.setMeterId(meterId1);
					}
					// JSONNull的处理
					if (!(read_time instanceof JSONNull) && null != read_time && "null" != read_time) {
						String read_time1 = (String) read_time;// 数据时间
						meterData.setReadTime(DateUtils.parseStringToDate(read_time1, "yyyy-MM-dd HH:mm:ss"));
					}
					if (!(total_e instanceof JSONNull) && null != total_e && "null" != total_e) {
						Float total_e1 = Float.parseFloat((String) total_e); // 电表已使用量
						meterData.setMeterUsed(total_e1);
					}
					// 若无法以限定格式解析json，则跳过下一个
				} catch (NumberFormatException e) {
					return null;
				}
				meterDataList.add(meterData);
				return meterDataList;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取电表用户
	 * 
	 * @return 电表用户名 String userName
	 */
	public static String getMeterUser(String token, Integer meterId) {
		// "user_name":" 9号库"
		// data = {"meter_id":"17110124","token":"8362c5ee-1a28-4bdd-a4df-53bc663d66a0"}
		HttpPost post = null;
		try {
			post = new HttpPost(GET_METER_USER);
			@SuppressWarnings("resource")
			HttpClient httpClient = new DefaultHttpClient();
			JsonObject object = new JsonObject();
			object.addProperty("token", token);
			object.addProperty("meter_id", meterId + "");
			// 构建消息实体
			StringEntity entity = new StringEntity(object.toString(), Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity1 = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retStr = EntityUtils.toString(entity1, Charsets.UTF_8);
				EntityUtils.consume(entity1);
				JSONObject retJson = JSONObject.fromObject(retStr);
				Object data = retJson.get("data");
				JSONObject retJson2 = JSONObject.fromObject(data);
				String userName = (String) retJson2.get("user_name");
				return userName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
//		String token = MeterUtils.getToken();
		// System.out.println("MeterService.TOKEN --> "+MeterService.TOKEN);
//		System.out.println("token in MeterUtils --> " + token);
//		String str = DateUtils.parseDateToString(new Date(), DateUtils.C_YYYY_MM);
//		Object str = "2018-04-01";
//		System.out.println((String)str);
//		Date readT = DateUtils.parseStringToDate((String)str, DateUtils.C_YYYY_MM_DD);
//		Date readT2 = DateUtils.parseStringToDate((String)str);
//		System.out.println("readT2 而且它是个date");
//		System.out.println(readT2);
		
		Date date =new Date();
		date.setTime(System.currentTimeMillis()-86400000);
		String dateStr = DateUtils.formatDateTime(date, 1);
		System.out.println(dateStr);
		
	}
}
