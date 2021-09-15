package uk.gov.hmcts.reform.casehqemulator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.casehqemulator.services.DataService;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/rest/hmcts/resources/sessions")
public class SessionsController {

    private static final String RES_CODE = "response code";
    private static final String DESCRIPTION = "description";
    private DataService dataService;

    @Autowired
    public SessionsController(final DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/")
    public ResponseEntity retrieveSessions(@RequestHeader(required = false) String returnHttpCode,
                                           @RequestHeader(required = false) String returnErrorCode,
                                           @RequestHeader(required = false) String returnDescription) throws IOException {
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final List<Map<String, Object>> sessions = dataService.getSessions();
            final List<Map<String, Object>> response = new ArrayList<>();
            for (final Map res : sessions) {
                response.add(res);
            }
            return ok(response);
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity retrieveSessionByid(@PathVariable String sessionId,
                                              @RequestHeader(required = false) String returnHttpCode,
                                              @RequestHeader(required = false) String returnErrorCode,
                                              @RequestHeader(required = false) String returnDescription) throws IOException {
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            Map<String, Object> session = dataService.getSessionById(sessionId);
            if (session == null || session.size() == 0) {
                session = new HashMap<>();
                session.put("message", "Session doesn't exist...");
            }
            return ok(session);
        }
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> createSession(@RequestBody final Map<String, Object> payload,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.createSession(payload);
            final Map<String, String> response = new HashMap<>();
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }


    @PutMapping("/{sessionIdCaseHQ}")
    public ResponseEntity<Map<String, String>> updateSession(@PathVariable String sessionIdCaseHQ, @RequestBody Map<String, Object> payload,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.updateSession(sessionIdCaseHQ, payload);
            final Map<String, String> response = new HashMap<>();
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @DeleteMapping("/{sessionIDCaseHQ}")
    public ResponseEntity<Map<String, String>> deleteSession(@PathVariable String sessionIDCaseHQ,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.deleteSession(sessionIDCaseHQ);
            final Map<String, String> response = new HashMap<>();
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }
}
