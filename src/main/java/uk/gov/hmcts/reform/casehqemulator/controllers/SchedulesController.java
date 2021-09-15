package uk.gov.hmcts.reform.casehqemulator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.casehqemulator.services.DataService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/schedules")
public class SchedulesController {

    private static final String RES_CODE = "response code";
    private static final String DESCRIPTION = "description";

    private DataService dataService;

    @Autowired
    public SchedulesController(final DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/retrieve-schedule")
    public ResponseEntity retrieveSchedule(@RequestParam(required = false) String hearing_date,
                                                                      @RequestParam(required = false) String hearing_venue_id,
                                                                      @RequestParam(required = false) String hearing_room_id,
                                                                      @RequestParam(required = false) String hearing_session_id_casehq,
                                                                      @RequestParam(required = false) String hearing_case_id_hmcts,
                                                                      @RequestParam(required = false) String hearing_id_casehq,
                                                                      @RequestHeader(required = false) String returnHttpCode,
                                                                      @RequestHeader(required = false) String returnErrorCode,
                                                                      @RequestHeader(required = false) String returnDescription) throws IOException {
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final Map<String, String> requestParams = new HashMap<>();
            if (hearing_date != null) {
                requestParams.put("hearingDate", hearing_date);
            }
            if (hearing_venue_id != null) {
                requestParams.put("hearingVenueId", hearing_venue_id);
            }
            if (hearing_room_id != null) {
                requestParams.put("hearingRoomId", hearing_room_id);
            }
            if (hearing_session_id_casehq != null) {
                requestParams.put("hearingSessionIdCaseHQ", hearing_session_id_casehq);
            }
            if (hearing_case_id_hmcts != null) {
                requestParams.put("hearingCaseIdHMCTS", hearing_case_id_hmcts);
            }
            if (hearing_id_casehq != null) {
                requestParams.put("hearingIdCaseHQ", hearing_id_casehq);
            }

            final List<Map<String, String>> schedules = dataService.getSchedules(requestParams);
            return ok(schedules);
        }
    }

    @PostMapping("/create-schedule")
    public ResponseEntity<Map<String, String>> requestSchedule(@RequestBody Map<String, String> request,
                                                               @RequestHeader(required = false) String returnHttpCode,
                                                               @RequestHeader(required = false) String returnErrorCode,
                                                               @RequestHeader(required = false) String returnDescription) throws IOException {
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.createSchedule(request);
            final Map<String, String> response = new HashMap<>();
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @PutMapping("/update-schedule/{transactionIdCaseHQ}")
    public ResponseEntity<Map<String, String>> updateSchedule(@PathVariable String transactionIdCaseHQ, @RequestBody final Map<String, String> payload,
                                                              @RequestHeader(required = false) String returnHttpCode,
                                                              @RequestHeader(required = false) String returnErrorCode,
                                                              @RequestHeader(required = false) String returnDescription) throws IOException {
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.updateSchedule(transactionIdCaseHQ, payload);
            final Map<String, String> response = new HashMap<>();
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @DeleteMapping("/delete-schedule/{transactionIdCaseHQ}")
    public ResponseEntity<Map<String, String>> deleteSchedule(@PathVariable String transactionIdCaseHQ,
                                                              @RequestHeader(required = false) String returnHttpCode,
                                                              @RequestHeader(required = false) String returnErrorCode,
                                                              @RequestHeader(required = false) String returnDescription) {
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.deleteSchedule(transactionIdCaseHQ);
            final Map<String, String> response = new HashMap<>();
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

}
