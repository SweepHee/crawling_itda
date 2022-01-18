package security.security.Global;


import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Component
public class GlobalFunction {


    /*
    * 쿼리스트링에서 원하는 쿼리 제거하고 문자열로 리턴
    * Ex) page=1&color=red&age=16에서 page만 지우고싶을때 사용
    * */

    public String splitQueryString(HttpServletRequest request, String exclude) {

        String queryString = request.getQueryString();

        Map<String, String> result = new HashMap<>();

        System.out.println(queryString);

        if (queryString != null)
        {

            for (String param: queryString.split("&")) {

                String[] pair = param.split("=");

                if (exclude.equals(pair[0])) {
                    continue;
                }

                if (pair.length>1 && !pair[0].equals("=") && !pair[1].equals("=")) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }

            }
        }

        return MapToQueryString(result);

    }


    public String MapToQueryString(Map<String, String> map) {

        StringBuilder str = new StringBuilder();

        if (!map.isEmpty())
        {
            for (Map.Entry<String, String> elem: map.entrySet()) {

                if (!str.toString().equals("")) {
                    str.append("&");
                }
                str.append(elem.getKey()).append("=").append(elem.getValue());
            }

            if (Character.toString(str.charAt(0)).equals("=")) {
                str.deleteCharAt(0);
            }
        }


        return str.toString();

    }


}
