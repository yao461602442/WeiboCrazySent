package jy.test;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class GenerateAt {
	
	final static int MAXLEN = 140;
	
	public static String getMethodRequest(String url, Map<String, String> params, Map<String, String> header){
		System.out.println("get request is begin! url =" + url);
		HttpClient hc = new HttpClient();
		try {
			StringBuilder sb = new StringBuilder(url);
			if(params != null){
				for (String param_key : params.keySet()) {
					if(param_key == null || params.get(param_key) == null)
						continue;
					sb.append("&").append(param_key).append("=").append(params.get(param_key));
				}
			}
			GetMethod pm = new GetMethod(sb.toString());
			if(header != null){
				for (String head_key : header.keySet()) {
					if(head_key == null || header.get(head_key) == null)
						continue;
					pm.addRequestHeader(head_key, header.get(head_key));
				}
			}
			pm.getParams().setContentCharset("utf8");
			hc.executeMethod(pm);
			String ret = pm.getResponseBodyAsString();
			System.err.println(ret);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.out.println("get request is end! url =" + url);
		}
		return "";
	}
	
	public static String postMethodRequestWithFile(String url, Map<String, String> params, Map<String, String> header, Map<String, byte[]> itemsMap) throws Exception{
		System.out.println("post request is begin! url =" + url);
		HttpClient hc = new HttpClient();
		
		PostMethod pm = new PostMethod(url);
		if(header != null){
			for (String head_key : header.keySet()) {
				if(head_key == null || header.get(head_key) == null)
					continue;
				pm.addRequestHeader(head_key, header.get(head_key));
			}
		}
		int part_size = 1;
		if(params != null)
			part_size = params.size();
		if(itemsMap != null)
			part_size = part_size + itemsMap.size();
		Part[] parts = new Part[part_size]; 
		int index = 0;
		if(itemsMap != null){
			for (String item_name : itemsMap.keySet()) {
				if(itemsMap.get(item_name) == null)
					continue;
				parts[index] = new FilePart(item_name, new ByteArrayPartSource(item_name, itemsMap.get(item_name)), "multipart/form-data;", "utf-8");
				index ++ ;
			}
		}
		
		if(params != null){
			for (String param_key : params.keySet()) {
				if(param_key == null|| params.get(param_key) == null)
					continue;
				parts[index] = new StringPart(param_key, params.get(param_key), "utf-8");
				index ++ ;
			}
		}
		pm.setRequestEntity(new MultipartRequestEntity(parts, pm.getParams())); 
		pm.getParams().setContentCharset("utf8");
		hc.executeMethod(pm);
		String ret = pm.getResponseBodyAsString();
		System.err.println(ret);
		if(ret.charAt(2) == 'e')
		{
			throw new Exception("TOKEN TIME LIMITED");
		}
		return ret;
		
	}
	
	public static String postMethodRequestWithOutFile(String url, Map<String, String> params, Map<String, String> header)
			throws Exception
	{
		System.out.println("post request is begin! url =" + url);
		HttpClient hc = new HttpClient();

		PostMethod pm = new PostMethod(url);
		if(header != null){
			for (String head_key : header.keySet()) {
				if(head_key == null || header.get(head_key) == null)
					continue;
				pm.addRequestHeader(head_key, header.get(head_key));
			}
		}
		if(params != null){
			for (String param_key : params.keySet()) {
				if(param_key == null || params.get(param_key) == null)
					continue;
				pm.addParameter(param_key, params.get(param_key));
			}
		}
		pm.getParams().setContentCharset("utf8");
		hc.executeMethod(pm);
		
		String ret = pm.getResponseBodyAsString();
		System.err.println(ret);
		if(ret.charAt(2) == 'e')
		{
			throw new Exception("TOKEN TIME LIMITED");
		}
		return ret;
	}
	
	public static byte[] readFromURL(String url) {
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(url);
		// 加入同步避免被防盗链机制屏蔽而取不到内容
		Header h = new Header("referer", "hupan.com");
		getMethod.setRequestHeader(h);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				return new byte[] {};
			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			// 处理内容
			return responseBody;
		} catch (Exception e) {
			System.out.println("读取url失败：url->" + url + ":" + e);
			return new byte[] {};
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
	}
	
	public static void sent(String token, int rand)
	{
		try{
		   Map<String, String> params = new HashMap<String, String>();
		   String status = "@宏佳但从那 ";
		   params.put("access_token", token);
		   params.put("status", status + rand);
		   params.put("lat", "22.631163467669495");
		   params.put("long", "114.04709815979004");		   
		   HttpUtil.postMethodRequestWithOutFile(POST_WEIBO_URL_WITH_IMAGE, params, header);
		   Map<String, byte[]> itemsMap = new HashMap<String, byte[]>();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public static void sentWithFile(String content,  String token, int rand)
	{
		try{
			   Map<String, String> params = new HashMap<String, String>();
			   String status = "@陆德华edward ";
			   params.put("access_token", token);
			   params.put("status", content + rand);
			   params.put("lat", "23.06039585415722"); //维度
			   params.put("long", "113.39111052974704");//经度		   
			   Map<String, byte[]> itemsMap = new HashMap<String, byte[]>();
			   itemsMap.put("pic", readFromURL("http://p13.freep.cn/p.aspx?u=v20_p13_photo_1308251334372216_0.jpg"));
			   HttpUtil.postMethodRequestWithFile(POST_WEIBO_URL_WITH_IMAGE, params, header, itemsMap);
			   Thread.sleep(1000);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	static String []tokenArr={
		//yao.mitcn@qq.com
		"2.00e7A9jC7aMw9E6b5641685cIZpAnC", //yy
		"2.00e7A9jC8HR2OB5829cc8f47FVCCuD", //yy
		"2.00e7A9jCFJgvbCb340dee4dcYtUaFB", //
		//"2.00VwpDvC_cCZYCd244d022330gJLza", //dh
		//"2.00UtRTGDXvWDUB22ab15f1c8_ANJLB", //视频
		
		"2.00UtRTGDXvWDUB22ab15f1c8_ANJLB",//girl0001
		"2.00UtRTGDugCcqD87b8ae16b4AjskZC",//girl0002
		"2.00UtRTGD0FOnBga52c370a46yhx5cD",//girl0003Y
		"2.00UtRTGDjswpsD919ee013ab00kOt8",//girl0004Y
		"2.00UtRTGDfKWhQD033a53256e0hRr9Y",//girl0005Y
};
	
	public static ArrayList<String> nickname_list = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException{
		 
//		int count=1;
//		while(true){
//			sentWithFile("2.00i4QTCC0NwWI8cd10f7fe4f3JS2rB", (int)(Math.random()*10000));
//			count++;
//			System.out.println(count);
//		}
		
		FileReader fr = new FileReader(PEOPLE);
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter("C:\\Users\\Administrator\\Desktop\\洗衣机销售\\weiboList");
		BufferedWriter bw = new BufferedWriter(fw);
		String line = null;
		while((line = br.readLine()) != null)
		{
			nickname_list.add(line);
		}
		System.out.println(nickname_list.size());
		int count = 1;
		fr.close();
		br.close();
//		while(true){
//			for(int i=0; i<nickname_list.size(); ++i)
//			{
//				String content = "打扰了。测试一下，如果被艾特到就回复一下。@"+nickname_list.get(i)+" ";
//				sentWithFile(content, "2.00e7A9jC7aMw9E6b5641685cIZpAnC", count);
//			}
//			count++;
//		}

		
		//sentWithFile("2.00QH1SFENsalnD4a258bb953EWAxGD", 0);
		int  i=0; //轮换token
		int  curWeiboPeopleNumber = 0; //本条微博@的人数
		int  weiboCounter = 1; //微博条数
		int  nickCounter = 0; //已经@的总人数
		int  index = 0; //临时游标
		int  excIndex = 0; //异常变量
		String accessToken="";
		StringBuilder sb = new StringBuilder();
		try{
			while(true)
			{
				accessToken=tokenArr[i];
				sb = new StringBuilder();
				try {
				        Map<String, String> params = new HashMap<String, String>();
				        int curLen =0; //当前微博长度
				        index = nickCounter;
				        int nextNicknameLen = nickname_list.get(index).length(); //下一个昵称的长度
				        while(curLen+nextNicknameLen+1 < MAXLEN-7 && index < nickname_list.size())
				        {//+2是因为@字符与空格字符
				        	sb.append("@"+nickname_list.get(index++));
				        	curLen = sb.length();
				        }
				        sb.append("@宏那 "+weiboCounter);
				        String status = sb.toString();
				        curWeiboPeopleNumber = index-nickCounter;
				        bw.append(status);
				        bw.newLine();
//				        params.put("access_token", accessToken);
//				        params.put("status", status);
//				        params.put("lat", "22.631163467669495");
//				        params.put("long", "114.04709815979004");
//				        Map<String, byte[]> itemsMap = new HashMap<String, byte[]>();
//				        itemsMap.put("pic", readFromURL(IMAGESRC));
//				        HttpUtil.postMethodRequestWithFile(POST_WEIBO_URL_WITH_IMAGE, params, header, itemsMap);
//				        Thread.sleep(1000*10);
				        //====================
//				        excIndex ++;
//				        if(excIndex == 31)
//				        {
//				        	throw new Exception("发了30条微博 ，要更换token了");	
//				        }
				        //=====================
				        System.out.printf("第%d条微博%n当前@了%d人%n已经@了%d人%n当前微博内容:%s%n%n%n%n", weiboCounter, curWeiboPeopleNumber, index, status);
					    nickCounter = index;
					    weiboCounter++;
					    
					    if(nickCounter >= nickname_list.size())
					    {
					    	br.close();
					    	bw.close();
					    	break;
					    }
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("本微博发送失败：：：：：：："+sb.toString());
					System.err.printf("token=%s i=%d%n", tokenArr[i], i);
					i=(i+1)%tokenArr.length; //更换token
					//excIndex =0; //刷新异常
					if(i==0)
					{
						try {
							System.out.printf("===停下来5分钟等待===%n%n");
							Thread.sleep(1000*300);
							
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		} catch(Exception e3)
		{
			e3.printStackTrace();
			System.out.printf("===%n%n出大错啦%n%n===%nindex=%d%n", index);
			System.out.printf("已发微博数:%d%n", weiboCounter);
			System.out.printf("已@人数：%d%n", nickCounter);
		}
		
		
		
//		params.put("status", status + Math.random());
//		itemsMap.put("pic", readFromURL("http://p13.freep.cn/p.aspx?u=v20_p13_photo_1308241708106945_0.jpg"));
//		HttpUtil.postMethodRequestWithFile(POST_WEIBO_URL_WITH_IMAGE, params, header, itemsMap);
//		try {
//			Thread.sleep(1000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		params.put("status", status + Math.random());
//		params.put("url", "http://ww1.sinaimg.cn/mw600/7973a285gw1dqgmdbzjbkj.jpg");
		//HttpUtil.postMethodRequestWithOutFile(POST_WEIBO_URL_WITH_CONTENT_URL, params, header);
	}
	
	/**
	 * 采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
	 */
	public static final String SINA_SOURCE = "3879098224";

	/**
	 * sina api 请求的参数<br>
	 */
	public static Map<String, String> header = new HashMap<String, String>();
	
	static{
		header.put("Accept-Language", "zh-CN,zh;q=0.8");
		header.put("User-Agent", "test sina api");
		header.put("Accept-Charset", "utf-8;q=0.7,*;q=0.3");
	}
	
	/**
	 * 
	 * 发布微博 请求方式 POST
	 * source		false	string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
	 * access_token	false	string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。<br>
	 * status		true	string	要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。<br>
	 * url			false	string	图片的URL地址，必须以http开头。<br>
	 * lat			false	float	纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。<br>
	 * long			false	float	经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。<br>
	 * annotations	false	string	元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，<br>
	 * 									必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。<br>
	 */
	//public static final String POST_WEIBO_URL_WITH_CONTENT_URL = "https://api.weibo.com/2/statuses/upload_url_text.json?";
	
	/**
	 * 
	 * 发布微博 请求方式 POST
	 * source		false	string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
	 * access_token	false	string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。<br>
	 * status		true	string	要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。<br>
	 * lat			false	float	纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。<br>
	 * long			false	float	经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。<br>
	 * annotations	false	string	元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，<br>
	 * 									必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。<br>
	 */
	public static final String POST_WEIBO_URL_WITH_CONTENT = "https://api.weibo.com/2/statuses/update.json?";
	
	/**
	 * 
	 * 发布微博 请求方式 POST
	 * source		false	string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。<br>
	 * access_token	false	string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。<br>
	 * status		true	string	要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。<br>
	 * pic			false	string	图片要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M。。<br>
	 * lat			false	float	纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。<br>
	 * long			false	float	经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。<br>
	 * annotations	false	string	元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，<br>
	 * 									必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。<br>
	 */
	public static final String POST_WEIBO_URL_WITH_IMAGE = "https://api.weibo.com/2/statuses/upload.json?";
	
	public static final String IMAGESRC = "http://p13.freep.cn/p.aspx?u=v20_p13_photo_1308251334372216_0.jpg";
	public static final String PEOPLE = "C:\\Users\\Administrator\\Desktop\\洗衣机销售\\gdufsnickname.txt";
}
