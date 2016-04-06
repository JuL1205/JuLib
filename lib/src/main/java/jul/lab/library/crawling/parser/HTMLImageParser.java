package jul.lab.library.crawling.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jul.lab.library.concurrent.AsyncJob;
import jul.lab.library.crawling.ImageUrlList;
import jul.lab.library.crawling.ParsedPageList;

/**
 * 전달받은 page의 모든 하위 page의 이미지까지 크롤링한다.
 */
public class HTMLImageParser extends AsyncJob<List<String>> {
    public static String PATTERN = "<img(.*)src\\s{0,}=\\s{0,}\"[^\\s]*\"";
    public static Pattern mImageTagPattern = Pattern.compile(PATTERN);

    public static String PATTERN2 = "src\\s{0,}=\\s{0,}\"[^\\s]*\"";
    public static Pattern mImageSrcPattern = Pattern.compile(PATTERN2);


    public static String PATTERN3 = "href\\s{0,}=\\s{0,}\"[^\\s]*\"";
    public static Pattern mLinkPattern = Pattern.compile(PATTERN3);

    private String mPage;

    private ImageUrlList mImageUrlList;
    private ParsedPageList mParsedPageList;

    public String mDomain;
    public HTMLImageParser(String domain, String page, ImageUrlList target, ParsedPageList parsedPageList){
        mDomain = domain;
        mPage = page;
        mImageUrlList = target;
        mParsedPageList = parsedPageList;
    }

    @Override
    protected List<String> run() throws InterruptedException {
        List<String> imgUrlList = new ArrayList<>();

        try {
            URL obj = new URL(mDomain+mPage);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setReadTimeout(3 * 1000);

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
                        imgUrlList.add(url);
//                        mImageUrlList.atomicAdd(url);
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
                    if(linkPage.startsWith("/") && !linkPage.contains("?")){
                        if(!mParsedPageList.wasParsed(linkPage)){   //아직 파싱작업을 안한 페이지라면 새작업 시작.
                            new HTMLImageParser(mDomain, linkPage, mImageUrlList, mParsedPageList).execute();
                            mParsedPageList.atomicAdd(linkPage);
                        }
                    }
                }
            }
            br.close();

//            Log.w("mImagePathList size = " + mImageUrlList.size());
//            Log.w("mParsedPageList size = "+mParsedPageList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return imgUrlList;
    }

    @Override
    protected void doneOnMainThread(Object finalResult) {
        mImageUrlList.atomicAdd((List<String>) finalResult);
    }
}
