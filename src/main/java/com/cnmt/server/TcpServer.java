package com.cnmt.server;

import java.util.Map;

/**
 * An interface for a health data receiver over the TCP.
 */
public interface TcpServer {
    /**
     * Method to check that the server is running.
     *
     * @return true if started
     */
    boolean isStarted();

    /**
     * Method to get processing statistics.
     *
     * @return map <i>item -> processing statistics<i/>
     */
    Map<String, String> getStatistics();

    /**
     * Method to get parsing/uploading to FHIR details.
     *
     * @return map <i>date time -> parse/upload status -> parsing/uploading details<i/>
     */
    Map<String, Map<Boolean, String>> getParseDetails();
}
