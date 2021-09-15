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
@RequestMapping("/rest/hmcts/resources/listings")
public class ListingsController {

    private DataService dataService;
    private static final String RES_CODE = "response code";
    private static final String DESCRIPTION = "description";

    @Autowired
    public ListingsController(final DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/create-listing")
    public ResponseEntity<Map<String, String>> requestSchedule(@RequestBody Map<String, Object> request,
                                                               @RequestHeader(required = false) String returnHttpCode,
                                                               @RequestHeader(required = false) String returnErrorCode,
                                                               @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.createListing(request);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @GetMapping("")
    public ResponseEntity retrieveListing(@RequestParam(required = false) String date_of_listing,
                                          @RequestParam(required = false) String hearingType,
                                          @RequestHeader(required = false) String returnHttpCode,
                                          @RequestHeader(required = false) String returnErrorCode,
                                          @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final Map<String, String> requestParams = new HashMap<>();
            if (date_of_listing != null) {
                requestParams.put("hearingDate", date_of_listing);
            }
            if (hearingType != null) {
                requestParams.put("hearingType", hearingType);
            }
            final List<Map<String, Object>> listings = dataService.getListings(requestParams);
            return ok(listings);
        }
    }

    @GetMapping("/{listing_id}")
    public ResponseEntity retrieveListingByid(@PathVariable String listing_id,
                                              @RequestHeader(required = false) String returnHttpCode,
                                              @RequestHeader(required = false) String returnErrorCode,
                                              @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            Map<String, Object> listing = dataService.getListingById(listing_id);
            if (listing == null || listing.size() == 0) {
                listing = new HashMap<>();
                listing.put("message", "Resource doesn't exist...");
            }
            return ok(listing);
        }
    }

    @PutMapping("/{caseListingRequestId}")
    public ResponseEntity<Map<String, String>> updateListing(@PathVariable String caseListingRequestId,
                                                             @RequestBody Map<String, Object> payload,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.updateListing(caseListingRequestId, payload);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<Map<String, String>> deleteListing(@RequestBody Map<String, String> request, @PathVariable String listingId,
                                                             @RequestHeader(required = false) String returnHttpCode,
                                                             @RequestHeader(required = false) String returnErrorCode,
                                                             @RequestHeader(required = false) String returnDescription) throws IOException {
        final Map<String, String> response = new HashMap<>();
        if(returnHttpCode != null && returnErrorCode != null && returnDescription != null){
            response.put("errCode", returnErrorCode);
            response.put("errorDesc", returnDescription);
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(returnHttpCode)));
        } else {
            final String message = dataService.deleteListing(listingId);
            response.put(RES_CODE, "200");
            response.put(DESCRIPTION, message);
            return ok(response);
        }
    }
}
