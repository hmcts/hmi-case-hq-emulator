package uk.gov.hmcts.reform.casehqemulator.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/utils")
public class UtilsController {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String REMOTE_ADDR = "REMOTE_ADDR";
    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private static final String X_AZURE_CLIENT_IP = "X-Azure-ClientIP";
    public static final String X_AZURE_SOCKET_IP = "X-Azure-SocketIP";

    @GetMapping("/retrieve-client-ip")
    public ResponseEntity retrieveHearing(HttpServletRequest servletRequest) throws IOException {
        final Map<String, String> clientDetails = new HashMap<>();
        /*clientDetails.put("forwardedFor", servletRequest.getHeader(X_FORWARDED_FOR));
        clientDetails.put("forwardedHost", servletRequest.getHeader(X_FORWARDED_HOST));
        clientDetails.put("azureClientIP", servletRequest.getHeader(X_AZURE_CLIENT_IP));
        clientDetails.put("socketIp", servletRequest.getHeader(X_AZURE_SOCKET_IP));
        clientDetails.put("remoteAddr", servletRequest.getHeader(REMOTE_ADDR));
        clientDetails.put("httpClientIp", servletRequest.getHeader(HTTP_CLIENT_IP));*/
        final Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements())  {
            final String key = headerNames.nextElement();
            clientDetails.put(key, servletRequest.getHeader(key));
        }
        return ok(clientDetails);
    }
}
