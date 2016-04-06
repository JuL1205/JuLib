package jul.lib.test.crawling.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jul.lab.library.concurrent.AsyncJob;
import jul.lab.library.log.Log;
import jul.lib.test.crawling.ImagePathList;
import jul.lib.test.crawling.ParsedPageList;

/**
 * 전달받은 page의 모든 하위 page의 이미지까지 크롤링한다.
 * Created by owner on 2016. 4. 5..
 */
public class HTMLImageParser extends AsyncJob {
    public static String PATTERN = "<img(.*)src\\s{0,}=\\s{0,}\"[^\\s]*\"";
    public static Pattern mImageTagPattern = Pattern.compile(PATTERN);

    public static String PATTERN2 = "src\\s{0,}=\\s{0,}\"[^\\s]*\"";
    public static Pattern mImageSrcPattern = Pattern.compile(PATTERN2);


    public static String PATTERN3 = "href\\s{0,}=\\s{0,}\"[^\\s]*\"";
    public static Pattern mLinkPattern = Pattern.compile(PATTERN3);

    protected final String DOMAIN = "http://www.gettyimagesgallery.com";

    private String mPage;

    private ImagePathList mImagePathList;
    private ParsedPageList mParsedPageList;
    public HTMLImageParser(String page, ImagePathList target, ParsedPageList parsedPageList){
        mPage = page;
        mImagePathList = target;
        mParsedPageList = parsedPageList;
    }

    @Override
    protected Object run() throws InterruptedException {
        try {
            URL obj = new URL(DOMAIN+mPage);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
//            StringBuffer response = new StringBuffer();
            while((line = br.readLine()) != null) {

                //img 탐색
                Matcher tagMatcher = mImageTagPattern.matcher(line);
                if(tagMatcher.find()){
                    Matcher srcMatcher = mImageSrcPattern.matcher(tagMatcher.group());
                    if(srcMatcher.find()){
                        String find = srcMatcher.group();
                        String url = find.replace("src","").replace(" ","").replace("=","").replace("\"", "");
                        mImagePathList.atomicAdd(url);
                    }
//                    int count = tagMatcher.groupCount();
//                    for(int i = 0 ; i < count ; i++){
//                        Matcher srcMatcher = mImageSrcPattern.matcher(tagMatcher.group(i));
//                        if(srcMatcher.find()){
//                            String find = srcMatcher.group();
//                            String url = find.replace("src","").replace(" ","").replace("=","").replace("\"", "");
//                            mImagePathList.atomicAdd(url);
//                        }
//                    }
                }


                //link 페이지 탐색
                Matcher linkMatcher = mLinkPattern.matcher(line);
                if(linkMatcher.find()){
                    String linkPage = linkMatcher.group().replace("href","").replace(" ","").replace("=","").replace("\"","");
                    if(linkPage.startsWith("/")){
                        if(!mParsedPageList.wasParsed(linkPage)){   //아직 파싱작업을 안한 페이지라면 새작업 시작.
                            new HTMLImageParser(linkPage, mImagePathList, mParsedPageList).execute();
                            mParsedPageList.atomicAdd(linkPage);
                        }
                    }
                }
            }
            br.close();

            Log.w("mImagePathList size = " + mImagePathList.size());
            Log.w("mParsedPageList size = "+mParsedPageList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void doneOnMainThread(Object finalResult) {
    }
}
