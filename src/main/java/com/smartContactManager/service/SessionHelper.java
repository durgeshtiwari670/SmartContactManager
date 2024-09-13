package com.smartContactManager.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

    public void removeMassageFromSession() {
        try {

            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                    .getSession();
            session.removeAttribute("massage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
