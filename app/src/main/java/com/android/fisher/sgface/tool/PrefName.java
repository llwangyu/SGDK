package com.android.fisher.sgface.tool;

public class PrefName {
	public static final String DEFAULT_SERVER_URL = "http://211.98.121.169:57099";
	//public static final String DEFAULT_SERVER_URL = "http://10.208.72.171:8080/SgjhMap";

    public static final String VER_SERVER_URL = "/face/ApiGetAppVersion.json";//查看升级
	public static final String LOGIN_SERVER_URL = "/face/ApiLogin.json";//用户登录
	public static final String UPLOAD_SIGN_SERVER_URL = "/face/ApiuploadTbImg";//上传图片
	public static final String FINAL_SIGN_SERVER_URL = "/face/ApiSendFaceDetect.json";//上传图片


    //系统常量
	public static final String APK_NAME = "lsc.apk";
	public static final String FILE_DIR_NAME = "sgface";

    public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String POST_FILE = "file";
	public static final String POST_FILE1 = "file1";
	public static final String POST_FILE2= "file2";
	public static final String POST_PHOTO = "filephoto";
	public static final String POST_MORE_FILE = "morefile";
	public static final String POST_SIGN = "postSign";
	public static final String POST_LONG_TIMEOUT = "postLongTimeout";
    public static final String GET_WITHOUTRESULT = "GETWITHOUTRESULT";
    public static final String PREF_BOOL_HAS_SHOW_HELP = "hasShowHelp";
	//百度的信息
	public static final String BAIDU_AI_URL = "https://aip.baidubce.com/oauth/2.0/token";
	public static final String BAIDU_API_KEY = "GRV3WypspIm1sBdshzfYcHrw";
	public static final String BAIDU_SECRECT_KEY = "KE8sQbxbHwmuPG8sUFNE6jKfnCw3SHDv";
	public static final String BAIDU_GRANT_TYPE = "client_credentials";
	public static final String BAIDU_FACE_URL = "https://aip.baidubce.com/rest/2.0/face/v2/match";
	public static final String BAIDU_FACE_CONTENTTYPE = "application/x-www-form-urlencoded";




}