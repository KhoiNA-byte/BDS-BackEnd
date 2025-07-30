package com.blooddonation.blood_donation_support_system.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String message = "";
        if (throwable != null) {
            message = throwable.getMessage();
        } else {
            // Provide default messages based on status code
            int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
            switch (statusCode) {
                case 404:
                    message = "Resource not found: " + path;
                    break;
                case 403:
                    message = "Access denied";
                    break;
                case 400:
                    message = "Bad request";
                    break;
                default:
                    message = "An error occurred";
                    break;
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status != null ? Integer.parseInt(status.toString()) : 500);
        body.put("error", "Error occurred");
        body.put("message", message);
        body.put("path", path);

        HttpStatus httpStatus = HttpStatus.resolve(
            status != null ? Integer.parseInt(status.toString()) : 500
        );

        return new ResponseEntity<>(body, httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}