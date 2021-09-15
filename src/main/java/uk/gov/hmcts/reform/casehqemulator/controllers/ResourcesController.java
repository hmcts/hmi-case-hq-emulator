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
@RequestMapping("/resources")
public class ResourcesController {


    private DataService dataService;
    private static final String RES_CODE = "response code";
    private static final String DESCRIPTION = "description";

    @Autowired
    public ResourcesController(final DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/retrieve-resources")
    public ResponseEntity retrieveResources(@RequestHeader(required = false) String returnHttpCode,
                                            @RequestHeader(required = false) String returnErrorCode,
                                            @RequestHeader(required = false) String returnDescription) throws IOException {
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            final Map<String, String> response = new HashMap<>();
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final List<Map<String, Map>> resources = dataService.getResources();
            final List<Map<String, String>> response = new ArrayList<>();
            for (Map res : resources) {
                final Set<String> keySet = res.keySet();
                for (String key : keySet) {
                    response.add((Map) res.get(key));
                }

            }
            return ok(response);
        }
    }

    @GetMapping("/retrieve-resources/{resourceId}")
    public ResponseEntity<Map<String, String>> retrieveResourceByid(@PathVariable String resourceId,
                                                                    @RequestHeader(required = false) String returnHttpCode,
                                                                    @RequestHeader(required = false) String returnErrorCode,
                                                                    @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            Map<String, String> resource = dataService.getResourceById(resourceId);
            if (resource == null || resource.size() == 0) {
                resource = new HashMap<>();
                resource.put("message", "Resource doesn't exist...");
            }
            return ok(resource);
        }
    }

    @PostMapping("/create-resource")
    public ResponseEntity<Map<String, String>> requestResource(@RequestBody final Map<String, String> payload,
                                                               @RequestHeader(required = false) String returnHttpCode,
                                                               @RequestHeader(required = false) String returnErrorCode,
                                                               @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.createResource(payload);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @PutMapping("/update-resource/{resourceId}")
    public ResponseEntity<Map<String, String>> updateResource(@PathVariable String resourceId,
                                                              @RequestBody Map<String, String> payload,
                                                              @RequestHeader(required = false) String returnHttpCode,
                                                              @RequestHeader(required = false) String returnErrorCode,
                                                              @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.updateResource(resourceId, payload);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }


    @DeleteMapping("/delete-resource/{resourceId}")
    public ResponseEntity<Map<String, String>> deleteResource(@PathVariable String resourceId,
                                                              @RequestHeader(required = false) String returnHttpCode,
                                                              @RequestHeader(required = false) String returnErrorCode,
                                                              @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.deleteResource(resourceId);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    // elinks methods
    @GetMapping("/retrieve-peope/{peopleId}")
    public ResponseEntity<Map<String, Object>> retrievePeopleByid(@PathVariable String peopleId,
                                                                    @RequestHeader(required = false) String returnHttpCode,
                                                                    @RequestHeader(required = false) String returnErrorCode,
                                                                    @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            Map<String, Object> resource = dataService.getPeopleById(peopleId);
            if (resource == null || resource.size() == 0) {
                resource = new HashMap<>();
                resource.put("message", "Resource doesn't exist...");
            }
            return ok(resource);
        }
    }

    @GetMapping("/retrieve-people")
    public ResponseEntity retrievePeopleByParams(@RequestParam(required = false) String updated_since,
                                                                      @RequestParam(required = false) String per_page,
                                                                      @RequestParam(required = false) String page,
                                                                      @RequestHeader(required = false) String returnHttpCode,
                                                                      @RequestHeader(required = false) String returnErrorCode,
                                                                      @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            List<Map<String, Object>> resource = dataService.getPeopleByParams(updated_since, per_page, page);
            return ok(resource);
        }
    }



    @PostMapping("/create-people")
    public ResponseEntity<Map<String, Object>> requestPeople(@RequestBody final Map<String, Object> payload,
                                                               @RequestHeader(required = false) String returnHttpCode,
                                                               @RequestHeader(required = false) String returnErrorCode,
                                                               @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, Object> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.createPeopleRecord(payload);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }
}
