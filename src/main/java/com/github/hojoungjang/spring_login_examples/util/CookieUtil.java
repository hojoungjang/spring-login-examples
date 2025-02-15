package com.github.hojoungjang.spring_login_examples.util;

import java.util.Base64;

import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void addCookie(
        HttpServletResponse response,
        String name,
        String value,
        int maxAge
    ) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");            // cookie 가 포함될 경로 설정
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);  // client-side access 금지
        response.addCookie(cookie);

        // ResponseCookie cookie2 = ResponseCookie.from(name, value)
        //     .path("/")
        //     .maxAge(maxAge)
        //     .httpOnly(true)
        //     .sameSite("Strict")
        //     .build();

        // response.addHeader("Set-Cookie", cookie2.toString());
    }

    public static void deleteCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String name
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
            SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue()))
        );
    }
}
