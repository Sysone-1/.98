package com.sysone.ogamza.service.user;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * 공공데이터포털의 한국 공휴일 API를 활용해 특정 연도와 월의 휴일 목록을 가져오는 서비스 클래스입니다.
 *
 * 반환 형태는 <일(int), 공휴일명(String)> 형태의 Map입니다.
 *
 * 사용 API: https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo
 * 예: getHolidays(2025, 8) → {15=광복절}
 *
 * @author 서샘이
 * @since 2025-07-27
 */
public class HolidayService {

    /** 공공데이터 포털에서 발급받은 인증키 */
    private static final String SERVICE_KEY = "4o%2Bnb04UAY6awxuhP2O2WHxiGdQt8TRUx3wBeS9bjDt%2F4FYvXShTRwVHTmjCQ2oHGSA9QXlNMoFFo9BzDhYLLQ%3D%3D";

    /** 공휴일 API의 엔드포인트 URL */
    private static final String ENDPOINT = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    /**
     * 특정 연도와 월의 공휴일 데이터를 조회하여 Map으로 반환합니다.
     *
     * @param year 연도 (예: 2025)
     * @param month 월 (예: 8)
     * @return <날짜, 공휴일명> 형태의 Map
     */
    public static Map<Integer, String> getHolidays(int year, int month) {
        Map<Integer, String> holidays = new HashMap<>();

        try {
            String urlStr = ENDPOINT + "?solYear=" + year +
                    "&solMonth=" + String.format("%02d", month) +
                    "&ServiceKey=" + SERVICE_KEY +
                    "&_type=xml";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

            InputStream responseStream = conn.getInputStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(responseStream);

            NodeList itemList = doc.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String dateName = getTagValue("dateName", item);
                String locdate = getTagValue("locdate", item); // 예: 20250815
                int day = Integer.parseInt(locdate.substring(6));
                holidays.put(day, dateName);
            }

        } catch (Exception e) {
            System.err.println("[공휴일 API 조회 실패] " + e.getMessage());
        }

        return holidays;
    }

    /**
     * 특정 태그명에 대한 값을 XML 요소에서 추출합니다.
     */
    private static String getTagValue(String tag, Element element) {
        NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = nList.item(0);
        return nValue == null ? null : nValue.getNodeValue();
    }

    /**
     * 테스트용 main 메서드
     */
    public static void main(String[] args) {
        Map<Integer, String> holidays = getHolidays(2025, 8);
        holidays.forEach((day, name) -> System.out.println(day + "일: " + name));
    }
}
