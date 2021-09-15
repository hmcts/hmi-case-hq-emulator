package uk.gov.hmcts.reform.casehqemulator.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DataService {

    private static final String LISTING_REQUEST_ID = "listingRequestId";
    private static final String HEARING_ID_CASE_HQ = "hearingIdCaseHQ";
    private static final String RSC_UPDATE_SUCCESSFULL = "The request was received successfully.";
    private static final String SCHED_UPDATE_SUCCESSFULL = "The request was received successfully.";
    private static final String HRNG_UPDATE_SUCCESSFULL = "The request was received successfully.";

    @Value("${casehq.emulator.repository.path.resources}")
    private String resources_dir;

    @Value("${casehq.emulator.repository.path.schedules}")
    private String schedules_dir;

    @Value("${casehq.emulator.repository.path.hearings}")
    private String hearings_dir;

    @Value("${casehq.emulator.repository.path.listings}")
    private String listings_dir;

    @Value("${casehq.emulator.repository.path.sessions}")
    private String sessions_dir;

    private ObjectMapper objectMapper;

    @Autowired
    public DataService(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String deleteListing(final String listingId) {
        final File f = new File(listings_dir);
        String message = "The request was received successfully.";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            message = lookUpAndDeleteListingFile(listingId, files);
        }
        return message;
    }

    public String createListing(final Map<String, Object> payload) throws IOException {
        final String fileName = createFileNameForListingRecord(payload, listings_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String listingJson = objectMapper.writeValueAsString(payload);
            FileUtils.writeStringToFile(new File(fileName), listingJson, true);
            return "The request was received successfully.";
        }
        return "The request was received successfully.";
    }

    public List<Map<String, String>> getSchedules(final Map<String, String> requestParams) throws IOException {
        final File f = new File(schedules_dir);
        final List<Map<String, String>> schedules = new ArrayList<>();
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.endsWith(".json")) {
                    final Map<String, String> schedule = objectMapper.readValue(
                        new File(schedules_dir + fileName),
                        Map.class
                    );
                    if (requestParams.size() > 0) {
                        filterSchedules(requestParams, schedules, schedule);
                    } else {
                        schedules.add(schedule);
                    }
                }
            }
        }
        return schedules;
    }

    public String createSchedule(final Map<String, String> request) throws IOException {
        final String fileName = createFileNameForScheduleRecord(request, schedules_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            final String schedulesJson = objectMapper.writeValueAsString(request);
            FileUtils.writeStringToFile(new File(fileName), schedulesJson, true);
            return "The request was received successfully.";
        }
        return "The request was received successfully.";
    }

    public String updateSchedule(final String transactionIdCaseHQ, final Map<String, String> payload) throws IOException {
        final File f = new File(schedules_dir);
        String message = "The request was received successfully.";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                message = updateScheduleRecord(transactionIdCaseHQ, payload, file.getName());
                if (SCHED_UPDATE_SUCCESSFULL.equals(message))
                    break;
            }
        }
        return message;
    }

    public String deleteSchedule(final String transactionIdCaseHQ) {
        final File f = new File(schedules_dir);
        String message = "The request was received successfully.";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            message = lookUpAndDeleteScheduleFile(transactionIdCaseHQ, files);
        }
        return message;
    }

    public List<Map<String, String>> getHearings(final Map<String, String> requestParams) throws IOException {
        final List<Map<String, String>> hearings = new ArrayList<>();
        File heaaringsDirectory = new File(hearings_dir);
        if (heaaringsDirectory.exists()) {
            List<File> files = new ArrayList<File>(FileUtils.listFiles(
                heaaringsDirectory,
                new WildcardFileFilter("*.json"),
                TrueFileFilter.TRUE
            ));
            for (File file : files) {
                final String fileName = file.getName();
                if (fileName.endsWith(".json")) {
                    Map<String, String> data = objectMapper.readValue(file, Map.class);
                    if (requestParams.size() > 0) {
                        filterHearings(requestParams, hearings, data);
                    } else {
                        hearings.add(data);
                    }
                }
            }
        }
        return hearings;
    }

    public Object getVideoHearingById(final String hearingIdCaseHQ) throws IOException {
        Map<String, Object> videoHearing = null;
        File heaaringsDirectory = new File(hearings_dir);
        if (heaaringsDirectory.exists()) {
            List<File> files = new ArrayList<File>(FileUtils.listFiles(
                heaaringsDirectory,
                new WildcardFileFilter("*.json"),
                TrueFileFilter.TRUE
            ));
            for (File file : files) {
                final String fileName = file.getName();
                if (fileName.contains("vh_"+ hearingIdCaseHQ)) {
                    videoHearing = objectMapper.readValue(file, Map.class);
                    break;
                }
            }
        }
        return videoHearing;
    }

    public List<Map<String, Object>> getVideoHearings(final String username) throws IOException {
        final List<Map<String, Object>> hearings = new ArrayList<>();
        File heaaringsDirectory = new File(hearings_dir);
        if (heaaringsDirectory.exists()) {
            List<File> files = new ArrayList<File>(FileUtils.listFiles(
                heaaringsDirectory,
                new WildcardFileFilter("*.json"),
                TrueFileFilter.TRUE
            ));
            for (File file : files) {
                final String fileName = file.getName();
                if (fileName.contains("vh_")) {
                    Map<String, Object> data = objectMapper.readValue(file, Map.class);
                    if (username != null && !username.isBlank()) {
                        filterVideoHearings(username, hearings, data);
                    } else {
                        hearings.add(data);
                    }
                }
            }
        }
        return hearings;
    }

    public Map<String, Object> getHearingById(final String id) throws IOException {
        final List<Map<String, String>> hearings = new ArrayList<>();
        File heaaringsDirectory = new File(hearings_dir);
        Map<String, Object> hearingMap = null;
        if (heaaringsDirectory.exists()) {
            final File[] files = heaaringsDirectory.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.contains(id)) {
                    final Map<String, Object> hearing = objectMapper.readValue(
                        new File(hearings_dir + fileName),
                        Map.class
                    );
                    hearingMap = /*(Map<String, Object>)*/ hearing/*.values().toArray()[0]*/;
                    break;
                }
            }
        }
        return hearingMap;
    }

    public List<Map<String, Object>> getListings(final Map<String, String> requestParams) throws IOException {
        final List<Map<String, Object>> listings = new ArrayList<>();
        File listingsDirectory = new File(listings_dir);
        if (listingsDirectory.exists()) {
            List<File> files = new ArrayList<File>(FileUtils.listFiles(
                listingsDirectory,
                new WildcardFileFilter("*.json"),
                TrueFileFilter.TRUE
            ));
            for (File file : files) {
                final String fileName = file.getName();
                if (fileName.endsWith(".json")) {
                    Map<String, Object> data = objectMapper.readValue(file, Map.class);
                    if (requestParams.size() > 0) {
                        filterListings(requestParams, listings, data);
                    } else {
                        listings.add(data);
                    }
                }
            }
        }
        return listings;
    }

    public List<Map<String, Map>> getResources() throws IOException {
        final File resourcesDirectory = new File(resources_dir);
        final List<Map<String, Map>> resources = new ArrayList<>();
        if (resourcesDirectory.exists()) {
            final List<File> files = new ArrayList<File>(FileUtils.listFiles(
                resourcesDirectory,
                new WildcardFileFilter("*.json"),
                TrueFileFilter.TRUE
            ));
            for (File file : files) {
                Map<String, Map> data = objectMapper.readValue(file, Map.class);
                resources.add(data);
            }
        }
        return resources;
    }

    public List<Map<String, Object>> getSessions() throws IOException {
        final File resourcesDirectory = new File(sessions_dir);
        final List<Map<String, Object>> sessions = new ArrayList<>();
        if (resourcesDirectory.exists()) {
            final List<File> files = new ArrayList<File>(FileUtils.listFiles(
                resourcesDirectory,
                new WildcardFileFilter("*.json"),
                TrueFileFilter.TRUE
            ));
            for (File file : files) {
                Map<String, Object> data = objectMapper.readValue(file, Map.class);
                sessions.add(data);
            }
        }
        return sessions;
    }

    public String createHearing(final Map<String, Object> request) throws IOException {
        final String fileName = createFileNameForHearingRecord(request, hearings_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String hearingJson = objectMapper.writeValueAsString(request);
            FileUtils.writeStringToFile(new File(fileName), hearingJson, true);
            return "The request was received successfully.";
        }
        return "The request was received successfully.";
    }

    public String createVideoHearing(final Map<String, Object> request) throws IOException {
        final String fileName = createFileNameForVideoHearingRecord(request, hearings_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String hearingJson = objectMapper.writeValueAsString(request);
            FileUtils.writeStringToFile(new File(fileName), hearingJson, true);
            return "The request was received successfully.";
        }
        return "The request was received successfully.";
    }

    public String updateHearing(final String caseId, final Map<String, String> payload) throws IOException {
        final File f = new File(hearings_dir);
        String message = "Hearing does not exist";
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (File file : files) {
                message = updateHearingRecord(caseId, payload, file.getName());
                if (HRNG_UPDATE_SUCCESSFULL.equals(message)) break;
            }
        }
        return message;
    }

    public Map<String, Object> updateVideoHearing(final String caseId, final Map<String, Object> payload) throws IOException {
        final File f = new File(hearings_dir);
        Map<String, Object> response = null;
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (File file : files) {
                response = updateVideoHearingRecord(caseId, payload, file.getName());
                if (HRNG_UPDATE_SUCCESSFULL.equals(response)) break;
            }
        }
        return response;
    }

    public String updateSession(final String sessionId, final Map<String, Object> payload) throws IOException {
        final File f = new File(sessions_dir);
        String message = "Session does not exist";
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (File file : files) {
                message = updateSessionRecord(sessionId, payload, file.getName());
                if (HRNG_UPDATE_SUCCESSFULL.equals(message)) break;
            }
        }
        return message;
    }

    public String deleteHearing(final Map<String, String> request) throws IOException {
        final File f = new File(hearings_dir);
        String message = "The request was received successfully.";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            message = lookUpAndDeleteFile(request, files);
        }
        return message;
    }


    public Map<String, String> getResourceById(final String resourceId) throws IOException {
        final File f = new File(resources_dir);
        Map<String, String> resourceMap = null;
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.equals(resourceId + ".json")) {
                    final Map<String, Map<String, String>> resource = objectMapper.readValue(
                        new File(resources_dir + fileName),
                        Map.class
                    );
                    resourceMap = (Map<String, String>) resource.values().toArray()[0];
                    break;
                }
            }
        }
        return resourceMap;
    }

    public String createPeople(final Map<String, String> payload) throws IOException {
        final String fileName = createFileNameForResourceRecord(payload, resources_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String resourceJson = objectMapper.writeValueAsString(payload);
            FileUtils.writeStringToFile(new File(fileName), resourceJson, true);
            return "The request was received successfully.";
        }
        return "Resource already exists";
    }

    public Map<String, Object> getPeopleById(String peopleId) throws IOException {
        final File f = new File(resources_dir);
        Map<String, Object> resourceMap = null;
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.equals("elinks_"+peopleId + ".json")) {
                    final Map<String, Object> resource = objectMapper.readValue(
                        new File(resources_dir + fileName),
                        Map.class
                    );
                    resourceMap = resource;
                    break;
                }
            }
        }
        return resourceMap;
    }

    public List<Map<String, Object>> getPeopleByParams(String updated_since, String per_page, String page) throws IOException {
        final File f = new File(resources_dir);
        List<Map<String, Object>> resourceMap = new ArrayList<>();
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.contains("elinks_")) {
                    final Map<String, Object> resource = objectMapper.readValue(
                        new File(resources_dir + fileName),
                        Map.class
                    );
                    final Map<String, String> requestParams = new HashMap<>();
                    if (updated_since != null) {
                        requestParams.put("updated_since", updated_since);
                    }
                    if (per_page != null) {
                        requestParams.put("per_page", per_page);
                    }
                    if (page != null) {
                        requestParams.put("page", page);
                    }
                    filterPeopleRecords(resourceMap, resource, requestParams);
                }
            }
        }
        return resourceMap;
    }

    private void filterPeopleRecords(List<Map<String, Object>> resourceMap, Map<String, Object> resource, Map<String, String> requestParams) {
        boolean found = false;
        final Set<String> keys = requestParams.keySet();
        for (String key: keys) {
            if (requestParams.get(key) != null && resource.get(key) != null && resource.get(key).equals(requestParams.get(key))) {
                found = true;
            } else {
                found = false;
                break;
            }
        }
        if (found) {
            resourceMap.add(resource);
        }
    }

    public Map getListingById(final String listing_id) throws IOException {
        final File f = new File(listings_dir);
        Map<String, Object> listingMap = null;
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.equals(listing_id + ".json")) {
                    final Map<String, Map<String, Object>> listing = objectMapper.readValue(
                        new File(listings_dir + fileName),
                        Map.class
                    );
                    listingMap = (Map<String, Object>) listing.values().toArray()[0];
                    break;
                }
            }
        }
        return listingMap;
    }

    public String createResource(final Map<String, String> payload) throws IOException {
        final String fileName = createFileNameForResourceRecord(payload, resources_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String resourceJson = objectMapper.writeValueAsString(payload);
            FileUtils.writeStringToFile(new File(fileName), resourceJson, true);
            return "The request was received successfully.";
        }
        return "Resource already exists";
    }

    public String createPeopleRecord(final Map<String, Object> payload) throws IOException {
        final String fileName = createFileNameForPeopleRecord(payload, resources_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String resourceJson = objectMapper.writeValueAsString(payload);
            FileUtils.writeStringToFile(new File(fileName), resourceJson, true);
            return "The request was received successfully.";
        }
        return "Resource already exists";
    }

    public String createSession(final Map<String, Object> payload) throws IOException {
        final String fileName = createFileNameForSessionRecord(payload, sessions_dir);
        final File f = new File(fileName);
        if (!f.isDirectory() && !f.exists()) {
            String resourceJson = objectMapper.writeValueAsString(payload);
            FileUtils.writeStringToFile(new File(fileName), resourceJson, true);
            return "The request was received successfully.";
        }
        return "Session already exists";
    }

    public String updateResource(final String resourceId, final Map<String, String> payload) throws IOException {
        final File f = new File(resources_dir);
        String message = "Resource does not exist";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            for (File file : files) {
                message = updateResourceRecord(resourceId, payload, file.getName());
                if (RSC_UPDATE_SUCCESSFULL.equals(message))
                    break;
            }
        }
        return message;
    }

    public String updateListing(final String caseListingRequestId, final Map<String, Object> payload) throws IOException {
        final File f = new File(listings_dir);
        String message = "Listing does not exist";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                message = updateListingRecord(caseListingRequestId, payload, file.getName());
                if (RSC_UPDATE_SUCCESSFULL.equals(message))
                    break;
            }
        }
        return message;
    }

    public String deleteResource(final String resourceId) throws IOException {
        final File f = new File(resources_dir);
        String message = "Resource does not exist";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            message = lookUpAndDeleteResourceFile(resourceId, files);
        }
        return message;
    }

    public String deleteSession(final String sessionId) throws IOException {
        final File f = new File(sessions_dir);
        String message = "Session does not exist";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            message = lookUpAndDeleteSessionFile(sessionId, files);
        }
        return message;
    }

    public String deleteVideoHearing(final String hearingIdCaseHQ) throws IOException {
        final File f = new File(hearings_dir);
        String message = "The request was received successfully.";
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            message = lookUpAndDeleteVideoHearingFile(hearingIdCaseHQ, files);
        }
        return message;
    }

    private String createFileNameForResourceRecord(final Map<String, String> payload, String rsc_dir) {
        String fileName = null;
        final StringBuffer fileNameBuffer = new StringBuffer(rsc_dir);
        fileName = fileNameBuffer.append(payload.get("userIdCaseHQ")).append(".json").toString();
        return fileName;
    }

    private String createFileNameForPeopleRecord(final Map<String, Object> payload, String rsc_dir) {
        String fileName = null;
        final StringBuffer fileNameBuffer = new StringBuffer(rsc_dir);
        fileName = fileNameBuffer.append("elinks_").append(payload.get("id")).append(".json").toString();
        return fileName;
    }

    private String lookUpAndDeleteFile(Map<String, String> request, File[] files) throws IOException {
        String message = "The request was received successfully.";
        for (final File file : files) {
            final String fileName = file.getName();
            if (fileName.contains(request.get(HEARING_ID_CASE_HQ))) {
                final Map<String, String> hearing = objectMapper.readValue(
                    new File(hearings_dir + fileName),
                    Map.class
                );
                if (hearing.get(LISTING_REQUEST_ID).equals(request.get(LISTING_REQUEST_ID))) {
                    FileUtils.deleteQuietly(file);
                    message = "The request was received successfully.";
                    break;
                }
            }
        }
        return message;
    }

    private String lookUpAndDeleteVideoHearingFile(String id, File[] files) throws IOException {
        String message = "The request was received successfully.";
        for (final File file : files) {
            final String fileName = file.getName();
            if (fileName.contains("vh_"+id)) {
                final Map<String, String> hearing = objectMapper.readValue(
                    new File(hearings_dir + fileName),
                    Map.class
                );
                FileUtils.deleteQuietly(file);
                message = "The request was received successfully.";
                break;
            }
        }
        return message;
    }

    private String lookUpAndDeleteResourceFile(final String resourceId, File[] files) throws IOException {
        String message = "Resource does not exist";
        for (final File file : files) {
            final String fileName = file.getName();
            if (fileName.contains(resourceId)) {
                file.delete();
                message = "The request was received successfully.";
                break;
            }
        }
        return message;
    }

    private String lookUpAndDeleteSessionFile(final String sessionId, File[] files) throws IOException {
        String message = "Session does not exist";
        for (final File file : files) {
            final String fileName = file.getName();
            if (fileName.contains(sessionId)) {
                file.delete();
                message = "The request was received successfully.";
                break;
            }
        }
        return message;
    }

    private String lookUpAndDeleteScheduleFile(final String transactionIdCaseHQ, final File[] files) {
        String message = "Schedule does not exist.";
        for (final File file : files) {
            final String fileName = file.getName();
            if (fileName.contains(transactionIdCaseHQ)) {
                file.delete();
                message = "The request was received successfully.";
                break;
            }
        }
        return message;
    }

    private String createFileNameForHearingRecord(final Map<String, Object> request, final String jsonDir) {
        final StringBuffer fileNameBuffer = new StringBuffer(jsonDir);
        final Map<String, Object> _case = (Map<String, Object>) ((Map<String, Object>) request.get("hearingRequest")).get("_case");
        final String fileName = fileNameBuffer.append(_case.get("caseIdHMCTS"))
            .append("_").append(_case.get("caseSubType")).append("_")
            .append(_case.get("caseRegistered")).append(".json")
            .toString();
        return fileName;
    }

    private String createFileNameForVideoHearingRecord(final Map<String, Object> request, final String jsonDir) {
        final StringBuffer fileNameBuffer = new StringBuffer(jsonDir);
        final String id = UUID.randomUUID().toString();
        request.put("id", id);
        final String fileName = fileNameBuffer.append("vh_")
            .append(id)
            .append(".json")
            .toString();
        return fileName;
    }

    private String createFileNameForScheduleRecord(Map<String, String> request, String schedules_dir) {
        final StringBuffer fileNameBuffer = new StringBuffer(schedules_dir);
        final String fileName = fileNameBuffer.append(request.get("transactionIdCaseHQ"))
            .append("_").append(request.get("hearingDate"))
            .append(".json")
            .toString();
        return fileName;
    }

    private String updateScheduleRecord(final String transactionIdCaseHQ, final Map<String, String> payload, final String fileName)
        throws IOException {
        if (fileName.contains(transactionIdCaseHQ)) {
            final Map<String, String> schedule = objectMapper.readValue(new File(schedules_dir + fileName), Map.class);
            final Set<String> keys = schedule.keySet();
            for (final String key : keys) {
                schedule.put(key, payload.get(key));
            }
            objectMapper.writeValue(Paths.get(schedules_dir + fileName).toFile(), schedule);
            return SCHED_UPDATE_SUCCESSFULL;
        }
        return "Schedule does not exist";
    }

    private String updateResourceRecord(String resourceId, Map<String, String> payload, String fileName) throws IOException {
        if (fileName.contains(resourceId)) {
            final Map<String, Map<String, String>> resource = objectMapper.readValue(
                new File(resources_dir + fileName),
                Map.class
            );
            final Map<String, String> resourceMap = resource.get(resourceId);
            final Set<String> keys = resourceMap.keySet();
            for (final String key : keys) {
                resourceMap.put(key, payload.get(key));
            }
            objectMapper.writeValue(Paths.get(resources_dir + fileName).toFile(), resource);
            return RSC_UPDATE_SUCCESSFULL;
        }
        return "Resource doesn't exist";
    }

    private String updateListingRecord(String listingId, Map<String, Object> payload, String fileName) throws IOException {
        if (fileName.contains(listingId)) {
            final Map<String, Map<String, Object>> listing = objectMapper.readValue(
                new File(listings_dir + fileName),
                Map.class
            );
            final Map<String, Object> listingMapCase = (Map) listing.get("_Case");
            final Map<String, Object> listingMapEntity = (Map) listing.get("Entity");
            final Map<String, Object> listingMapSubtype = (Map) listing.get("Subtype");
            final Map<String, Object> listingMapListing = (Map) listing.get("Listing");

            final Set<String> caseKeys = listingMapCase.keySet();
            for (final String key : caseKeys) {
                listingMapCase.put(key, ((Map) payload.get("_Case")).get(key));
            }

            final Set<String> entityKeys = listingMapEntity.keySet();
            for (final String key : entityKeys) {
                listingMapEntity.put(key, ((Map) payload.get("Entity")).get(key));
            }

            final Set<String> subtypeKeys = listingMapSubtype.keySet();
            for (final String key : subtypeKeys) {
                listingMapSubtype.put(key, ((Map) payload.get("Subtype")).get(key));
            }

            final Set<String> listingKeys = listingMapListing.keySet();
            for (final String key : listingKeys) {
                listingMapListing.put(key, ((Map) payload.get("Listing")).get(key));
            }
            objectMapper.writeValue(Paths.get(listings_dir + fileName).toFile(), listing);
            return RSC_UPDATE_SUCCESSFULL;
        }
        return "Listing doesn't exist";
    }


    private void filterSchedules(final Map<String, String> requestParams, final List<Map<String, String>> schedules,
                                 final Map<String, String> schedule) {
        final Set<String> paramKeys = requestParams.keySet();
        boolean allKeysMatch = false;
        for (final String paramKey : paramKeys) {
            if (requestParams.get(paramKey).equals(schedule.get(paramKey))) {
                allKeysMatch = true;
            } else {
                allKeysMatch = false;
                break;
            }
        }
        if (allKeysMatch) {
            schedules.add(schedule);
        }
    }

    private String updateHearingRecord(String caseId, Map<String, String> payload, String fileName) throws IOException {
        if (fileName.contains("vh_" +caseId)) {
            final Map<String, String> hearing = objectMapper.readValue(new File(hearings_dir + fileName), Map.class);
            final Set<String> keys = hearing.keySet();
            for (final String key : keys) {
                hearing.put(key, payload.get(key));
            }
            objectMapper.writeValue(Paths.get(hearings_dir + fileName).toFile(), hearing);
            return HRNG_UPDATE_SUCCESSFULL;
        }
        return "Hearing does not exist";
    }

    private Map<String, Object> updateVideoHearingRecord(String caseId, Map<String, Object> payload, String fileName) throws IOException {
        if (fileName.contains("vh_" +caseId)) {
            final Map<String, Object> videoHearing = objectMapper.readValue(new File(hearings_dir + fileName), Map.class);
            final Set<String> keys = payload.keySet();
            for (final String key : keys) {
                videoHearing.put(key, payload.get(key));
            }
            objectMapper.writeValue(Paths.get(hearings_dir + fileName).toFile(), videoHearing);
            return videoHearing;
        }
        return null;
    }

    private String updateSessionRecord(final String sessionId, final Map<String, Object> payload, final String fileName) throws IOException {
        if (fileName.contains(sessionId)) {
            final Map<String, Object> session = objectMapper.readValue(new File(sessions_dir + fileName), Map.class);
            final Set<String> keys = session.keySet();
            for (final String key : keys) {
                session.put(key, payload.get(key));
            }
            objectMapper.writeValue(Paths.get(sessions_dir + fileName).toFile(), session);
            return HRNG_UPDATE_SUCCESSFULL;
        }
        return "Session does not exist";
    }

    private void filterHearings(final Map<String, String> requestParams, final List<Map<String, String>> hearings, final Map<String, String> data) {
        final Set<String> paramKeys = requestParams.keySet();
        boolean allKeysMatch = false;
        for (final String paramKey : paramKeys) {
            if (requestParams.get(paramKey).equals(data.get(paramKey))) {
                allKeysMatch = true;
            } else {
                allKeysMatch = false;
                break;
            }
        }
        if (allKeysMatch) {
            hearings.add(data);
        }
    }

    private void filterVideoHearings(final String username, final List<Map<String, Object>> hearings, final Map<String, Object> data) {
        boolean allKeysMatch = false;
        final List<Map<String, String>> participants = (List<Map<String, String>>)data.get("participants");
        if (participants != null) {
            for (final Map<String, String> participant : participants) {
                if (participant.get("username").equals(username)) {
                    allKeysMatch = true;
                } else {
                    allKeysMatch = false;
                    break;
                }
            }
            if (allKeysMatch) {
                hearings.add(data);
            }
        }
    }

    private void filterListings(final Map<String, String> requestParams, final List<Map<String, Object>> listings, final Map<String, Object> data) {
        final Set<String> paramKeys = requestParams.keySet();
        boolean allKeysMatch = false;
        for (final String paramKey : paramKeys) {
            if (requestParams.get(paramKey).equals(data.get(paramKey))) {
                allKeysMatch = true;
            } else {
                allKeysMatch = false;
                break;
            }
        }
        if (allKeysMatch) {
            listings.add(data);
        }
    }

    private String createFileNameForListingRecord(final Map<String, Object> request, final String jsonDir) {
        final StringBuffer fileNameBuffer = new StringBuffer(jsonDir);
        final String fileName = fileNameBuffer.append(((Map) request.get("_Case")).get("caseListingRequestId"))
            .append(".json")
            .toString();
        return fileName;
    }

    private String createFileNameForSessionRecord(final Map<String, Object> request, final String jsonDir) {
        final StringBuffer fileNameBuffer = new StringBuffer(jsonDir);
        final String fileName = fileNameBuffer.append((String) request.get("SessionIDCaseHQ")).append(".json").toString();
        return fileName;
    }

    private String lookUpAndDeleteListingFile(final String listing_id, final File[] files) {
        String message = "Schedule does not exist.";
        for (final File file : files) {
            final String fileName = file.getName();
            if (fileName.contains(listing_id)) {
                file.delete();
                message = "The request was received successfully.";
                break;
            }
        }
        return message;
    }

    public Map<String, Object> getSessionById(String sessionId) throws IOException {
        final File f = new File(sessions_dir);
        Map<String, Object> sessionsMap = null;
        if (f.exists()) {
            final File[] files = f.listFiles();
            for (final File file : files) {
                final String fileName = file.getName();
                if (fileName.equals(sessionId + ".json")) {
                    final Map<String, Object> session = objectMapper.readValue(
                        new File(sessions_dir + fileName),
                        Map.class
                    );
                    sessionsMap = session;
                    break;
                }
            }
        }
        return sessionsMap;
    }

}
