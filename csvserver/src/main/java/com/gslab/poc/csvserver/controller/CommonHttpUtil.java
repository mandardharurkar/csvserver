package com.gslab.poc.csvserver.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.cisco.iep.logging.util.LogKeyUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gslab.poc.csvserver.model.BeanBase;
import com.gslab.poc.csvserver.model.Status;

public class CommonHttpUtil {
	private static final Logger log = Logger.getLogger(CommonHttpUtil.class);
 	public static final String IEP_SESSION_ID = "iepsessionid";
 	public static final String IEP_LOG_KEY = "iep-log-key";


	public  static Gson gson = new GsonBuilder().create();
	private static  CacheConfig cacheConfig=CacheConfig.custom().build();
	private static RequestConfig requestConfig= RequestConfig.custom()
			.setConnectTimeout(30000).setSocketTimeout(30000)
			.build();
	private static HttpCacheContext context = HttpCacheContext.create();
	private static CloseableHttpClient cachingClient = CachingHttpClients.custom()
			.setCacheConfig(cacheConfig)
			.setDefaultRequestConfig(requestConfig)
			.build();

	public static void resetHttpCacheClient(){
		if(cachingClient!=null){
			System.out.println("Reseting the http Cache context !!");
			if(log.isDebugEnabled()) {
                log.debug("Reseting the http Cache context !!");
            }
			synchronized (cachingClient) {
				cachingClient = CachingHttpClients.custom()
						.setCacheConfig(cacheConfig)
						.setDefaultRequestConfig(requestConfig)
						.build();
			}
		}
	}

	public static String toJSONString(final Object obj) {
        //final String jsonString = gson.toJson(obj);
        final ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(obj);
        }
        catch (final IOException e) {
            log.error("method : toJSONString(obj), Failed to convert object to string : " + obj, e);
        }
		return jsonString;
	}

	public static String httpGet(final String apiUrl) throws RuntimeException {
		try {
			if(log.isDebugEnabled()) {
                log.debug("Requesting api url: " + apiUrl);
            }
			final Request httpGet = Request.Get(apiUrl);
			httpGet.addHeader(IEP_SESSION_ID, LogKeyUtil.getIepSessionId());
			//httpGet.addHeader(IEP_USER_ID, LogKeyUtil.getIepUserId());
			httpGet.addHeader(IEP_LOG_KEY, LogKeyUtil.getLogKey());

			final String response = httpGet.execute().returnContent().asString();

			if(log.isDebugEnabled()) {
                log.debug("Response for apiUrl : " + response);
            }
			return response;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String cachedHttpGet(final String apiUrl) throws RuntimeException {
		try {
			if(log.isDebugEnabled()) {
                log.debug("Requesting cached api url: " + apiUrl);
            }
			final HttpGet httpget = new HttpGet(apiUrl);
			httpget.addHeader(IEP_SESSION_ID, LogKeyUtil.getIepSessionId());
			//httpget.addHeader(IEP_USER_ID, LogKeyUtil.getIepUserId());
			httpget.addHeader(IEP_LOG_KEY, LogKeyUtil.getLogKey());

			final CloseableHttpResponse responseObj = cachingClient.execute(httpget, context);

			final String response=getResponse(responseObj.getEntity().getContent());
			if(log.isDebugEnabled()) {
                log.debug("Response ::"+response);
            }
			if(log.isDebugEnabled()) {
                log.debug("Cache Status  -->"+context.getCacheResponseStatus());
            }
			if(log.isDebugEnabled()) {
                log.debug("Response for cached api Url : " + response);
            }
			//CACHE_MISS :: when the entire cache is refreshed
			//CACHE_VALIDATED :: when cache expires ,it hits the api and populates the data in cache
			//CACHE_HIT :: when data is retrieved from cache.
			if(log.isDebugEnabled()) {
                log.debug("Cache Status  -->"+context.getCacheResponseStatus());
            }
			return response;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String getResponse(final InputStream response ) throws IOException{
        final BufferedReader rd = new BufferedReader(new InputStreamReader(response));
		final StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();

	}




	public static String httpPut(final String apiUrl, final Object input) throws RuntimeException{
		try {
			final String searchInputJson=toJSONString(input);
			if(log.isDebugEnabled()) {
                log.debug("Request for apiUrl : " + apiUrl);
            }

			final Request httpPut = Request.Put(apiUrl);
			httpPut.addHeader(IEP_SESSION_ID, LogKeyUtil.getIepSessionId());
			//httpPut.addHeader(IEP_USER_ID, LogKeyUtil.getIepUserId());
			httpPut.addHeader(IEP_LOG_KEY, LogKeyUtil.getLogKey());

			final String response= httpPut.bodyString(searchInputJson, ContentType.APPLICATION_JSON).execute().returnContent().asString();
			if(log.isDebugEnabled()) {
                log.debug("Response for apiUrl : " + response);
            }
			return response;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String httpPost(final String apiUrl, final Object input) throws RuntimeException{
		try {
			final String searchInputJson=toJSONString(input);
			if(log.isDebugEnabled()) {
                log.debug("Request for apiUrl : " + apiUrl);
            }

			final Request httpPost = Request.Post(apiUrl);
			httpPost.addHeader(IEP_SESSION_ID, LogKeyUtil.getIepSessionId());
			//httpPost.addHeader(IEP_USER_ID, LogKeyUtil.getIepUserId());
			httpPost.addHeader(IEP_LOG_KEY, LogKeyUtil.getLogKey());

			final String response= httpPost.bodyString(searchInputJson, ContentType.APPLICATION_JSON).execute().returnContent().asString();
			if(log.isDebugEnabled()) {
                log.debug("Response for apiUrl : " + response);
            }
			return response;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	public static String httpDelete(final String apiUrl) throws RuntimeException{
		try {
			final Request httpDelete = Request.Delete(apiUrl);
			httpDelete.addHeader(IEP_SESSION_ID, LogKeyUtil.getIepSessionId());
			//httpDelete.addHeader(IEP_USER_ID, LogKeyUtil.getIepUserId());
			httpDelete.addHeader(IEP_LOG_KEY, LogKeyUtil.getLogKey());

			final String response= httpDelete.execute().returnContent().asString();
			if(log.isDebugEnabled()) {
                log.debug("Response for apiUrl : " + response);
            }
			return response;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String httpDelete(final String apiUrl, final Object input) throws RuntimeException{
		try {
			final String searchInputJson=toJSONString(input);
			if(log.isDebugEnabled()) {
                log.debug("Request for apiUrl : " + apiUrl);
            }

			final Request httpDelete = Request.Delete(apiUrl);
			httpDelete.addHeader(IEP_SESSION_ID, LogKeyUtil.getIepSessionId());
			//httpDelete.addHeader(IEP_USER_ID, LogKeyUtil.getIepUserId());
			httpDelete.addHeader(IEP_LOG_KEY, LogKeyUtil.getLogKey());

			final String response = httpDelete.bodyString(searchInputJson, ContentType.APPLICATION_JSON).execute().returnContent().asString();
			if(log.isDebugEnabled()) {
                log.debug("Response for apiUrl : " + response);
            }
			return response;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String appendRequestParameters(final String apiUrl,final Map<String, String> requestParameters) throws UnsupportedEncodingException {
		final StringBuffer buffer = new StringBuffer(apiUrl);
		if( requestParameters != null && requestParameters.entrySet() != null
				&& !requestParameters.entrySet().isEmpty())  {
			for (final Entry<String, String> requestParam : requestParameters.entrySet()) {
				if (StringUtils.isNotBlank(requestParam.getValue()) &&
						StringUtils.isNotBlank(requestParam.getKey())) {
					buffer.append("&").append(StringUtils.trim(requestParam.getKey())).
					append("=").append(StringUtils.trim(encodeURL(requestParam.getValue())));
				}
			}
		} else {
			String apiUrlWithReqParams = buffer.toString();
			return apiUrlWithReqParams;
		}
		String apiUrlWithReqParams = buffer.toString();
		apiUrlWithReqParams = apiUrlWithReqParams.replaceFirst("&", "?");
		return apiUrlWithReqParams;
	}

	public static String encodeURL(final String url) throws UnsupportedEncodingException{
		String encodedUrl=URLEncoder.encode(url, "UTF-8");
		encodedUrl=encodedUrl.replaceAll("\\+", "%20");
		return encodedUrl;
	}
	public static void validateStatus(final Status status) throws RuntimeException{
		if( status != null ) {
			if (log.isDebugEnabled()) {
                ;
            } log.debug("status : " + status.getStatusCode());
			if (status.getStatusCode() != 200) {
				log.error(status.getErrorMessage());
				throw new RuntimeException(status.getErrorMessage());
			}
		} else {
			throw new RuntimeException("No Status Found...!");
		}
	}



	@SuppressWarnings("unchecked")
	public static <T> T validateAndGetResponse(final String baseResponse, final Class<T> type) throws RuntimeException {
		return (T) getObjFromPayload(baseResponse, type);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> validateAndGetResponseList(final String baseResponse, final Class<? extends BeanBase> type) throws RuntimeException {
		return (List<T>) getObjFromPayloadList(baseResponse, type, type);
	}

	public static <T> List<T> getObjFromPayloadList(final String jsonInput, final Class<? extends BeanBase> clazz,
			T type) throws RuntimeException {
		final ObjectMapper mapper = new ObjectMapper();
		List<T> outputList = null;
	    try {

	    	final JsonNode mainNode = mapper.readTree(jsonInput);

	    	if (StringUtils.contains(jsonInput, "status")) {
		    	final JsonNode statusNode = mainNode.path("status");
		    	if (!statusNode.isMissingNode()) {
		    		final Status status =  mapper.readValue(statusNode, mapper.getTypeFactory().constructType(Status.class));
		    		CommonHttpUtil.validateStatus(status);
		    	} else {
		    		if(log.isDebugEnabled()) {
                        log.debug("No status found in response...!");
                    }
		    		throw new RuntimeException("No status found in response...!");
		    	}
	    	} else {
	    		throw new RuntimeException("No status found in response...!");
	    	}

	    	if (StringUtils.contains(jsonInput, "payloadList")){
		    	final JsonNode payloadList = mainNode.path("payloadList");
		    	if (!payloadList.isMissingNode()) {
		    		outputList =  mapper.readValue(payloadList, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
		    	}
				if(log.isDebugEnabled()) {
                    log.debug("Received ResponseBase:" + outputList);
                }
	    	} else {
	    		if(log.isDebugEnabled()) {
                    log.debug("No payloadList found in response...!");
                }
	    		type = null;
	    	}
		} catch (final Exception e) {
			log.error("Exception", e);
			throw new RuntimeException(e);
		}
		return outputList;
	}


	public static <T> T getObjFromPayload(final String jsonInput, T type) throws RuntimeException {
		final ObjectMapper mapper = new ObjectMapper();
	    try {
	    	final JsonNode mainNode = mapper.readTree(jsonInput);

	    	if (StringUtils.contains(jsonInput, "status")) {
		    	final JsonNode statusNode = mainNode.path("status");
		    	if (!statusNode.isMissingNode()) {
		    		final Status status =  mapper.readValue(statusNode, mapper.getTypeFactory().constructType(Status.class));
		    		CommonHttpUtil.validateStatus(status);
		    	} else {
		    		if(log.isDebugEnabled()) {
                        log.debug("No status found in response...!");
                    }
		    		throw new RuntimeException("No status found in response...!");
		    	}
	    	} else {
	    		throw new RuntimeException("No status found in response...!");
	    	}

	    	if (StringUtils.contains(jsonInput, "payload")){
		    	final JsonNode payloadList = mainNode.path("payload");
		    	if (!payloadList.isMissingNode()) {
		    		type =  mapper.readValue(payloadList, mapper.getTypeFactory().constructType((Type)type));
		    	}
				if(log.isDebugEnabled()) {
                    log.debug("Received ResponseBase:" + type);
                }
	    	} else {
	    		if(log.isDebugEnabled()) {
                    log.debug("No payload found in response...!");
                }
	    		type = null;
	    	}

		} catch (final Exception e) {
			log.error("Exception", e);
			throw new RuntimeException(e);
		}
		return type;
	}

	public static void main(final String[] args) throws UnsupportedEncodingException {
		System.out.println(encodeURL("Type 1 in Diabetes in Adults"));
	}
}
