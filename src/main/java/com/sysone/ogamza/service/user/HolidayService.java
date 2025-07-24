package com.sysone.ogamza.service.user;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HolidayService {
    private static final String SERVICE_KEY = "4o%2Bnb04UAY6awxuhP2O2WHxiGdQt8TRUx3wBeS9bjDt%2F4FYvXShTRwVHTmjCQ2oHGSA9QXlNMoFFo9BzDhYLLQ%3D%3D";
    private static final String ENDPOINT = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    public static Map<Integer, String> getHolidays(int year, int month) {
        Map<Integer, String> holidays = new HashMap<>();

        try {
            String urlStr = ENDPOINT + "?solYear=" + year + "&solMonth=" + String.format("%02d", month) + "&ServiceKey=" + SERVICE_KEY + "&_type=xml";
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
                String locdate = getTagValue("locdate", item); // ex) 20250724
                int day = Integer.parseInt(locdate.substring(6));
                holidays.put(day, dateName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return holidays;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = nList.item(0);
        return nValue == null ? null : nValue.getNodeValue();
    }

    // 테스트용
    public static void main(String[] args) {
        Map<Integer, String> holidays = getHolidays(2025, 8);
        holidays.forEach((day, name) -> System.out.println(day + "일: " + name));
    }
}