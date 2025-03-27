package com.cnmt.controller;

import com.cnmt.server.TcpServer;
import com.cnmt.service.FhirService;
import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.validation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Validated
@Produces
@Controller("/fhir")
public class FhirController {
    private static final Logger log = LoggerFactory.getLogger(FhirController.class);

    private final FhirService fhirService;
    private final TcpServer tcpServer;

    public FhirController(FhirService fhirService, TcpServer tcpServer) {
        this.fhirService = fhirService;
        this.tcpServer = tcpServer;
    }

    @Get(value = "/version", produces = {MediaType.TEXT_PLAIN})
    public HttpResponse<String> getFhirVersionApi() {
        log.trace("Version called");
        try {
            return HttpResponse.ok(fhirService.getVersion());
        } catch (Exception e) {
            log.warn("Unable to get FHIR version - {}", e.getMessage());
            return HttpResponse.serverError(String.format("Unable to get FHIR version - %s", e.getMessage()));
        }
    }

    @Get(value = "/statistics", produces = {MediaType.APPLICATION_JSON})
    public HttpResponse<Map<String, String>> getMessageStatisticsApi() {
        log.trace("Message statistics called");
        try {
            return HttpResponse.ok(tcpServer.getStatistics());
        } catch (Exception e) {
            log.warn("Unable to get FHIR version - {}", e.getMessage());
            return HttpResponse.serverError(Collections.singletonMap("Unable to get message statistics", e.getMessage()));
        }
    }

    @Get(value = "/parse-details/", produces = {MediaType.APPLICATION_JSON})
    public HttpResponse<Map<String, Map<Boolean, String>>> getMessageParseDetailsApi() {
        log.trace("Parse details called");
        try {
            return HttpResponse.ok(tcpServer.getParseDetails());
        } catch (Exception e) {
            log.warn("Unable to get FHIR version - {}", e.getMessage());
            return HttpResponse.serverError(Collections.singletonMap("Unable to get parsing details", Collections.singletonMap(false, e.getMessage())));
        }
    }

//    @Error(global = true)
//    public HttpResponse<JsonError> jsonError(HttpRequest<?> request, JsonSyntaxException e) {
//        return getErrorResponse(request, e, "Malformed JSON");
//    }

    @Error(global = true)
    public HttpResponse<JsonError> conversionError(HttpRequest<?> request, ConversionErrorException e) {
        return getErrorResponse(request, e, "Unsupported JSON");
    }

//    @Error(global = true)
//    public HttpResponse<JsonError> genericError(HttpRequest<?> request, Exception e) {
//        log.warn("Generic: Request {} - {} ({})\n\tStack trace: {}", request, e, e.getCause(), e.getStackTrace());
//        JsonError error = new JsonError("Unable to handle request: " + e.getMessage())
//                .link(Link.SELF, Link.of(request.getUri()));
//
//        if (e instanceof NotFoundException) {
//            return HttpResponse.<JsonError>notFound().body(error);
//        } else if (e instanceof UnsupportedMediaException) {
//            return HttpResponse.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
//        } else if (e instanceof UnsatisfiedBodyRouteException) {
//            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(error);
//        } else if (e instanceof ConnectException) {
//            return HttpResponse.status(HttpStatus.CONNECTION_TIMED_OUT).body(error);
//        } else {
//            return HttpResponse.<JsonError>serverError().body(error);
//        }
//    }

    private static HttpResponse<JsonError> getErrorResponse(HttpRequest<?> request, RuntimeException e, String message) {
        log.warn("{}: Request {} - {} ({})\n\tStack trace: {}", message, request, e, e.getCause(), e.getStackTrace());

        return HttpResponse.<JsonError>status(HttpStatus.BAD_REQUEST, message)
                .body(new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri())));
    }
}
