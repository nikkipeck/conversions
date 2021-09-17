package com.romans.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.romans.converter.RomanNumeralConverter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * The RomanNumeralServer class sets up a basic HttpServer to listen on port
 * 8080 it serves requests the the romannumeral context
 * 
 * @author nikki
 * @since 09/15/2021
 */

public class RomanNumeralServer {

    /* Injects the RomanNumeralConverter for convenience */
    private RomanNumeralConverter rmc;
    private StringBuilder errors = new StringBuilder();

    @Inject
    public RomanNumeralServer(RomanNumeralConverter rmc) {
        this.rmc = rmc;
    }

    /*
     * startServer() starts a com.sun.net.httpserver.HttpServer listening on 8080 a
     * context and a threaded executor long term this should be switched to a more
     * robust server, leaning on an outside implementation like Jetty Error handling
     * is minimal here
     */
    public HttpServer server;

    public void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/romannumeral", new RomanNumeralHandler());
            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
     * stopServer() stops the server
     */
    public void stopServer() {
        if (server != null)
            server.stop(0);
    }

    /**
     * RomanNumeralHandler provides a mapping between URI path and exchange handler
     * parses query string and sends response data or errors
     */
    class RomanNumeralHandler implements HttpHandler {

        /**
         * Method to handle requests to the /romannumeral context of HttpServer
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestedUri = exchange.getRequestURI();

            String rawQuery = requestedUri.getQuery();
            if (rawQuery == null) {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }

            List<NameValuePair> params = URLEncodedUtils.parse(requestedUri, StandardCharsets.UTF_8);

            String body = "";
            if (params.size() == 1)
                body = handleQuery(params);
            else if (params.size() == 2)
                body = handleRange(params);
            else
                body = "Invalid query string. Format example: query={4}, or min={3}&max={8}";

            if (errors.length() > 0)
                exchange.sendResponseHeaders(400, body.length());
            else
                exchange.sendResponseHeaders(200, body.length());

            OutputStream os = exchange.getResponseBody();
            os.write(body.getBytes());
            os.close();

            errors.delete(0, errors.length()); // ugh, but we need to empty the builder
            exchange.close();
        }

        private String handleQuery(List<NameValuePair> query) {
            String payload = "";
            for (NameValuePair q : query) {
                if (q.getName().equals("query")) {
                    int ins = -1;
                    String qval = q.getValue();

                    if (qval == null || qval.length() < 1) {
                        errors.append("query must include a value. Format example: query={939}");
                        return errors.toString();
                    }

                    try {
                        ins = Integer.parseInt(qval.substring(qval.indexOf("{") + 1, qval.lastIndexOf("}")));
                    } catch (NumberFormatException nfe) {
                        errors.append(
                                "query value must be of type int values 1-3,999. Query string format example: query={11}");
                        return errors.toString();
                    }

                    if (validInt(ins))
                        payload = mapIntToRomanJson(ins).toString();
                } else {
                    errors.append("Invalid query string. Format example: query={11}");
                    payload = errors.toString();
                }
            }

            return payload;
        }

        private String handleRange(List<NameValuePair> range) {
            String payload = "";
            int[] minMax = new int[2];

            for (NameValuePair q : range) {
                String name = q.getName();
                if (name.equals("min") || name.equals("max")) {
                    int ins = -1;
                    String qval = q.getValue();

                    try {
                        ins = Integer.parseInt(qval.substring(qval.indexOf("{") + 1, qval.lastIndexOf("}")));
                    } catch (NumberFormatException nfe) {
                        errors.append(
                                "min and max values must be of type int. Query string format example: min={1}&max={3}");
                        return errors.toString();
                    }

                    if (name.equals("min"))
                        minMax[0] = ins;
                    else if (name.equals("max"))
                        minMax[1] = ins;
                } else {
                    errors.append("Invalid query string. Format example: min={1}&max={3}");
                    payload = errors.toString();
                }
            }

            if (errors.length() < 1)
                payload = getRangePayload(minMax);

            return payload;
        }

        /* decodes query strings according to specified encoding */
        /*
         * private String decode(String query) { try { return URLDecoder.decode(query,
         * "UTF-8"); } catch (IllegalArgumentException iae) {
         * errors.append("Illegal argument in query. Format example: query={54}"); }
         * catch (UnsupportedEncodingException uee) {
         * errors.append("Unsupported encoding detected. Please use UTF-8"); } return
         * ""; }
         */

        /*
         * split query returns an int[] with min and max values
         * 
         * @param query is a query string matching min={4}&max={7} format validates min
         * and max are ints in the allowable range 1-3,999
         * 
         * @return int[] with min and max values
         */
        /*
         * private int[] splitQuery(String query) throws UnsupportedEncodingException {
         * String decoded = URLDecoder.decode(query, "UTF-8"); String[] pairs =
         * decoded.split("&");
         * 
         * int[] minMax = new int[2];
         * 
         * for (int i = 0; i < pairs.length; i++) { String pair = pairs[i]; int start =
         * pair.indexOf("{") + 1; int end = pair.indexOf("}");
         * 
         * try { int mm = Integer.parseInt(pair.substring(start, end)); minMax[i] = mm;
         * } catch (NumberFormatException nfe) { errors.append(
         * "min and max values must be of type int. Query string format example: min={1}&max={3}"
         * ); return new int[0]; } }
         * 
         * if (minMax.length != 2) errors.append(
         * "min and max values are required, only min and max are allowed. Query string format example: min={1}&max={3}"
         * ); if (minMax[0] > minMax[1])
         * errors.append("min value must be less than max value. min " + minMax[0] +
         * ", max " + minMax[1]);
         * 
         * return minMax; }
         */

        /*
         * getRangePayload builds json payload for range requests
         * 
         * @param int[] containing minimum and maximum values for range request
         * 
         * @return String representing json payload example int[4,6] would return
         * "{"conversions":[{"input":"4","output":"IV"},{"input":"5","output":"V"},{"input":"6","output":"VI"}]}"
         */
        private String getRangePayload(int[] minMax) {
            for (int i : minMax) {
                if (!validInt(i))
                    return errors.toString();
            }

            return mapListToJson(minMax).toString();
        }

        /*
         * getSinglePayload builds json payload for query requests
         * 
         * @param String query in the format query={17}
         * 
         * @return String json payload example query={17} would return
         * "{"input":"17","output":"XVII"}"
         */
        /*
         * private String getSinglePayload(String query) { int qint = -1; try { qint =
         * Integer.parseInt(query.substring(query.indexOf("{") + 1,
         * query.lastIndexOf("}"))); } catch (NumberFormatException nfe) {
         * errors.append(
         * "query value must be of type int values 1-3,999. Query string format example: query={11}"
         * ); return errors.toString(); }
         * 
         * if (!validInt(qint)) return errors.toString();
         * 
         * return mapIntToRomanJson(qint).toString(); }
         */

        /*
         * mapIntToRomanJson will take an integer, convert it to a roman numeral string,
         * and return it as a jsonobject
         * 
         * @param int numer to convert
         * 
         * @return json payload example 3 would return "{"input":"3","output":"III"}"
         */
        private JsonObject mapIntToRomanJson(int number) {
            JsonObject payload = new JsonObject();
            payload.addProperty("input", String.valueOf(number));
            payload.addProperty("output", rmc.intToRomanConversion(number));
            return payload;
        }

        /*
         * mapListTojson turns an int[] into a json payload
         * 
         * @param int[] with min and max values
         * 
         * @return jsonobject with conversions for range example int[3,4] would return
         * "{"conversions":[{"input":"3","output":"III"},{"input":"4","output":"IV"}]}"
         */
        private JsonObject mapListToJson(int[] minMax) {
            IntStream stream = IntStream.rangeClosed(minMax[0], minMax[1]); // rangeClosed is endInclusive
            List<JsonObject> inners = stream.mapToObj(i -> mapIntToRomanJson(i)).collect(Collectors.toList());

            Gson gson = new GsonBuilder().create();

            JsonObject outer = new JsonObject();
            outer.add("conversions", gson.toJsonTree(inners));

            return outer;
        }

        /*
         * validInt checks if int it is in an allowable range
         * 
         * @param int
         */
        private boolean validInt(int toCheck) {
            if (toCheck < 1 || toCheck > 3999) {
                errors.append("min and max must be values between 1 and 3,999: " + toCheck + " not allowed");
                return false;
            }
            return true;
        }
    }
}