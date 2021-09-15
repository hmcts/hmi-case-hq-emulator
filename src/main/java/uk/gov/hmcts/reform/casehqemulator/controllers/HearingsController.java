package uk.gov.hmcts.reform.casehqemulator.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.casehqemulator.services.DataService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/hearings")
public class HearingsController {
    private static final Logger log = LoggerFactory.getLogger(HearingsController.class);
    private static final String RES_CODE = "response code";
    private static final String DESCRIPTION = "description";

    private DataService dataService;

    @Autowired
    public HearingsController(final DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/retrieve-hearing/{hearingIdCaseHQ}")
    public ResponseEntity retrieveHearing(@PathVariable(required = true) String hearingIdCaseHQ,
                                          @RequestHeader(required = false) String returnHttpCode,
                                          @RequestHeader(required = false) String returnErrorCode,
                                          @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final Map<String, Object> hearings = (Map<String, Object>) dataService.getHearingById(hearingIdCaseHQ);
            return ok(hearings);
        }
    }

    @GetMapping("/retrieve-hearing")
    public ResponseEntity retrieveHearing(@RequestParam(required = false) String hearingIdCaseHQ,
            @RequestParam(required = false) String hearingDate, @RequestParam(required = false) String hearingType,
            @RequestHeader(required = false) String returnHttpCode,
            @RequestHeader(required = false) String returnErrorCode,
            @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final Map<String, String> requestParams = new HashMap<>();
            if (hearingIdCaseHQ != null) {
                requestParams.put("hearingIdCaseHQ", hearingIdCaseHQ);
            }
            if (hearingDate != null) {
                requestParams.put("hearingDate", hearingDate);
            }
            if (hearingType != null) {
                requestParams.put("hearingType", hearingType);
            }
            final List<Map<String, String>> hearings = dataService.getHearings(requestParams);
            return ok(hearings);
        }
    }

    @PostMapping("/create-hearing")
    public ResponseEntity<Map<String, Object>> requestHearing(@RequestBody Map<String, Object> request,
            @RequestHeader(required = false) String returnHttpCode,
            @RequestHeader(required = false) String returnErrorCode,
            @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.createHearing(request);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @PostMapping("/direct-hearing/sessions/{sessionIdCaseHQ}")
    public ResponseEntity<Map<String, String>>  requestDirectHearing(@PathVariable String sessionIdCaseHQ, @RequestBody String request,
            @RequestHeader(required = false) String returnHttpCode,
            @RequestHeader(required = false) String returnErrorCode,
            @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        log.info("Direct Hearing for session {}", sessionIdCaseHQ);
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            // dataService.createHearing(request);
            response.put(RES_CODE, String.valueOf(HttpStatus.ACCEPTED.value()));
            response.put(DESCRIPTION, "The request was received successfully");
            return ResponseEntity.accepted().build();
        }
    }

    @PutMapping("/update-hearing/{hearingIdCaseHQ}")
    public ResponseEntity<Map<String, Object>> updateHearing(@PathVariable String hearingIdCaseHQ,
            @RequestBody Map payload, @RequestHeader(required = false) String returnHttpCode,
            @RequestHeader(required = false) String returnErrorCode,
            @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.updateHearing(hearingIdCaseHQ, payload);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, "Hearing updated successfully");
            return ok(response);
        }
    }

    @DeleteMapping("/delete-hearing")
    public ResponseEntity<Map<String, String>> deleteHearing(@RequestBody Map<String, String> request,
            @RequestHeader(required = false) String returnHttpCode,
            @RequestHeader(required = false) String returnErrorCode,
            @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.deleteHearing(request);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }


    ///Video Hearing Code
    @GetMapping("/retrieve-video-hearing/{hearingIdCaseHQ}")
    public ResponseEntity retrieveVideoHearing(@PathVariable(required = true) String hearingIdCaseHQ,
                                          @RequestHeader(required = false) String returnHttpCode,
                                          @RequestHeader(required = false) String returnErrorCode,
                                          @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final Map<String, Object> hearing = (Map<String, Object>) dataService.getVideoHearingById(hearingIdCaseHQ);
            return ok(hearing);
        }
    }

    @GetMapping("/retrieve-video-hearing")
    public ResponseEntity retrieveVideoHearingByUsername(@RequestHeader(required = false) String returnHttpCode,
                                          @RequestHeader(required = false) String returnErrorCode,
                                          @RequestHeader(required = false) String returnDescription,
                                          @RequestParam(required = false) String username) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final List<Map<String, Object>> hearings = dataService.getVideoHearings(username);
            return ok(hearings);
        }
    }

    @PostMapping("/create-video-hearing")
    public ResponseEntity requestVideoHearing(@RequestBody Map<String, Object> request,
                                                              @RequestHeader(required = false) String returnHttpCode,
                                                              @RequestHeader(required = false) String returnErrorCode,
                                                              @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            if (request != null && request.size() > 0) {
                final String message = dataService.createVideoHearing(request);
                return new ResponseEntity<>(request, HttpStatus.CREATED);
            } else {
                final Map<String, String> errRes = new HashMap<>();
                errRes.put("errCode", "400");
                errRes.put("errorDesc", "Invalid request body.");
                return new ResponseEntity<>(errRes, HttpStatus.valueOf(400));
            }
        }
    }

    @PutMapping("/update-video-hearing/{hearingIdCaseHQ}")
    public ResponseEntity<Map<String, Object>> updateVideoHearing(@PathVariable String hearingIdCaseHQ,
                                                             @RequestBody Map<String, Object> payload,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> errorResponse = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            errorResponse.put("errCode", returnErrorCode);
            errorResponse.put("errorDesc", returnDescription);
            return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final Map<String, Object> response = dataService.updateVideoHearing(hearingIdCaseHQ, payload);
            return ok(response);
        }
    }

    @DeleteMapping("/delete-video-hearing/{hearingIdCaseHQ}")
    public ResponseEntity<Map<String, String>> deleteVideoHearing(@PathVariable(required = true) String hearingIdCaseHQ,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if (returnHttpCode != null && returnErrorCode != null && returnDescription != null) {
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.deleteVideoHearing(hearingIdCaseHQ);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
