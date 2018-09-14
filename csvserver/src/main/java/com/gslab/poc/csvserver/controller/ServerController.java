package com.gslab.poc.csvserver.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.net.InternetDomainName;
import com.gslab.poc.csvserver.model.CatURL;
import com.gslab.poc.csvserver.model.ResponseBase;

@Controller
@RequestMapping(value = "/server")
public class ServerController {

	@Value("${category.url.file.location}")
	private String fileLocation;

	@Value("${app.server}")
	private String isServer;
	

	@Value("${application.server.host}")
	private String serverhost;

	@Value("${application.server.port}")
	private String serverport;

	@Value("${app.name}")
	private String appName;

	@RequestMapping(value = "/alldata", method = RequestMethod.GET)
	public @ResponseBody ResponseBase getSample() {
		System.out.println("fileLocation=" + fileLocation);
		List<CatURL> catURLs = processInputFile(fileLocation);
		return new ResponseBase(catURLs);
	}

	@RequestMapping(value = "/getCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseBase getCategory(@RequestBody CatURL inputCatUrl) throws IOException {
		String url = inputCatUrl.getUrl();
		String category = getCategoryFromMap(url);
		if (!StringUtils.isBlank(category)) {
			return new ResponseBase(new CatURL(category, url, "CACHE_READ", appName));
		} else {
			if (!Boolean.parseBoolean(isServer)) {
				CatURL catURL = getCatUrlFromServer(url);
				if (null != catURL) {
					writeTofile(catURL);
					catURL.setStatus("CACHE_WRITE");
					catURL.setClientId(appName);
					return new ResponseBase(catURL);
				}
			} else {
				return null;
			}
		}
		return null;
	}

	@RequestMapping(value = "/cache/refresh", method = RequestMethod.POST)
	public @ResponseBody ResponseBase cacheRefresh(@RequestBody CatURL inputCatURL) throws Exception {
		Set<String> categories = getAllCategoryFromMap();
		Set<CatURL> responseList = new HashSet<CatURL>();
		Set<String> inputList = new HashSet<String>();
		System.out.println("Categories=" + categories);
		CsvFileWriter.clearCsv(fileLocation);
		for (String category : categories) {
			String[] s1 = category.split("/");
			String s= InternetDomainName.from(s1[0]).topPrivateDomain().toString();
			inputList.add(s);
		}
		for (String input : inputList) {
			List<CatURL> catURLs= this.getAllCatUrlFromServer(input);
			responseList.addAll(catURLs);
		}

		writeTofile(new ArrayList<CatURL>(responseList));
		
		for(CatURL catURL : responseList){
			catURL.setStatus("CACHE_WRITE");
			catURL.setClientId(appName);
		}
		return new ResponseBase(new ArrayList<CatURL>(responseList));

	}

	@RequestMapping(value = "/cache/clear", method = RequestMethod.GET)
	public @ResponseBody ResponseBase cacheClear() throws Exception {
		CsvFileWriter.clearCsv(fileLocation);
		return new ResponseBase();

	}
	@RequestMapping(value = "/getAllCategory", method = RequestMethod.GET)
	public @ResponseBody ResponseBase getCategoriesList(@RequestParam("q") String q) throws IOException {
		System.out.println("**q=" + q);
		List<CatURL> catUrls = getAllUrlsLikeInoutUrls(q);
		return new ResponseBase(catUrls);
	}

	private String getCategoryFromMap(String url) {
		Map<String, String> map = readFileAndGetMap();
		return map.get(url);

	}

	private Set<String> getAllCategoryFromMap() {
		Map<String, String> map = readFileAndGetMap();
		return  map.keySet();

	}

	private Map<String, String> readFileAndGetMap() {
		List<CatURL> catURLs = processInputFile(fileLocation);
		Map<String, String> map = new HashMap<String, String>();
		if (null != catURLs && !catURLs.isEmpty()) {
			for (CatURL catURL : catURLs) {
				map.put(catURL.getUrl(), catURL.getCategoryId());
			}
		}
		return map;
	}

	private void writeTofile(CatURL catURL) throws IOException {
		List<CatURL> catURLs = new ArrayList<CatURL>();
		catURLs.add(catURL);
		CsvFileWriter.writeCsv(fileLocation, catURLs);
	}

	private void writeTofile(List<CatURL> catURLs1) throws IOException {
		List<CatURL> catURLs = new ArrayList<CatURL>();
		catURLs.addAll(catURLs1);
		CsvFileWriter.writeCsv(fileLocation, catURLs);
	}

	

	private List<CatURL> getAllUrlsLikeInoutUrls(String url) {
		Map<String, String> map = readFileAndGetMap();
		List<CatURL> responseList = new ArrayList<CatURL>();
		List<String> resultKeys = new ArrayList<String>();
		for (String key : map.keySet()) {
			if (key.contains(url)) {
				resultKeys.add(key);
			}
		}

		if (null != resultKeys && !resultKeys.isEmpty()) {
			for (String resultKey : resultKeys) {
				responseList.add(new CatURL(map.get(resultKey), resultKey));
			}
		}
		return responseList;
	}

	private List<CatURL> processInputFile(String inputFilePath) {
		List<CatURL> inputList = new ArrayList<CatURL>();
		try {
			File inputF = new File(inputFilePath);
			InputStream inputFS = new FileInputStream(inputF);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			throw new RuntimeException();
		}
		return inputList;
	}

	private Function<String, CatURL> mapToItem = (line) -> {
		String[] p = line.split(",");// a CSV has comma separated lines
		CatURL item = new CatURL();
		item.setUrl(p[0]);
		if (p.length > 1) {
			String str = "";
			for (int i = 1; i < p.length; i++) {
				if (p[i] != null && p[i].trim().length() > 0)
					if (StringUtils.isBlank(str)) {
						str = p[i];
					} else {
						str = str + "," + p[i];
					}
			}
			item.setCategoryId(str);

		}
		return item;
	};

	public static void main(String[] args) throws Exception {
		System.out.println("Hello");
		/*ServerController controller = new ServerController();
		controller.fileLocation="/opt/gslab/poc/client1/URLCategoryClient1.csv";
		controller.serverhost="localhost";
		controller.serverport="7070";
		controller.appName="client1";

		ResponseBase catURL = controller.getCategory(new CatURL(null, "zedge.net/"));
		System.out.println("*** CatUrl=" + catURL);
*/
		System.out.println(InternetDomainName.from("mandar.com").topPrivateDomain().toString());
	}

	public CatURL getCatUrlFromServer(String inputUrl) throws RuntimeException {

		final Map<String, String> requestParameters = new HashMap<String, String>();
		final String apiUrl = getCompleteApiUrl("getCatgeory");

		String url;
		try {
			url = CommonHttpUtil.appendRequestParameters(apiUrl, requestParameters);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		final String baseResponse = CommonHttpUtil.httpPost(url, new CatURL(null, inputUrl, null, appName));
		CatURL catURL = CommonHttpUtil.validateAndGetResponse(baseResponse, CatURL.class);
		return catURL;
	}

	private String getCompleteApiUrl(final String apiUrl) {
		final StringBuilder builder = new StringBuilder();
		// http://localhost:7070/csvserver/server/getCategory?url=google.com/a/
		builder.append("http://").append(serverhost).append(":").append(serverport).append("/csvserver").append("/server/getCategory");
		return builder.toString();
	}
	private String getAllCompleteApiUrl(final String apiUrl) {
		final StringBuilder builder = new StringBuilder();
		// http://localhost:7070/csvserver/server/getCategory?url=google.com/a/
		builder.append("http://").append(serverhost).append(":").append(serverport).append("/csvserver").append("/server/getAllCategory");
		return builder.toString();
	}
	public List<CatURL> getAllCatUrlFromServer(String inputUrl) throws RuntimeException {

		final Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put("q", inputUrl);
		final String apiUrl = getAllCompleteApiUrl("getCatgeory");

		String url;
		try {
			url = CommonHttpUtil.appendRequestParameters(apiUrl, requestParameters);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		final String baseResponse = CommonHttpUtil.httpGet(url);
		List<CatURL> catURLs = CommonHttpUtil.validateAndGetResponseList(baseResponse, CatURL.class);
		return catURLs;
	}

}
