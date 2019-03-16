package netstuff;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SuppressWarnings("ALL")
public class DoSomeHTTPStuff {
    private static final String URL = "https://restcountries.eu/rest/v2/all";

    public static void main(String[] args) {
        try {
            URL thePlaceFromWhereYOuWannaGetYourData = new URL(URL);
            HttpURLConnection myHttpConnection = (HttpURLConnection) thePlaceFromWhereYOuWannaGetYourData
                    .openConnection();
            String response = getResponseAfterSubmittingRequestQueryMapAndContentTypeMap(myHttpConnection,
                    null,null,
                    null);
            
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e1) {
            System.out.println("Cannot cast to HttpURLConnection");
        }
    }

    public static String getResponseAfterSubmittingRequestQueryMapAndContentTypeMap(final HttpURLConnection httpURLConnection,
                                                                             Map<String, String> requestQueryMap,
                                                                             Map<String, String> headers,
                                                                             String requestMethodName)
            throws IOException {
        decorateWithHTTPMethodsAndContentTypeHeader(httpURLConnection ,headers,requestMethodName);
        InputStream responseStreamFromServer =
                getInputStreamAfterFiringRequestToServer(httpURLConnection ,makeQueryString(requestQueryMap));
        return readResponseDataUsingBufferedReader(responseStreamFromServer).toString();
    }

    /**
     * Read the response that comes in as an {@link InputStream} using a {@link BufferedReader}. In our case we are
     * certain that the response that comes in will be string format. That's why this implementation works. But in
     * real world the data can be in binary(Images, Videos, Audio or just random Files)
     * @param responseFromServer
     * @return a {@link StringBuilder} object that contains the data from the response that can be printed somewhere
     * and humans can read it
     * @throws IOException
     */
    private static StringBuilder readResponseDataUsingBufferedReader(InputStream responseFromServer) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseFromServer));
        String resultFromServer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((resultFromServer = bufferedReader.readLine()) != null) {
            stringBuilder.append(resultFromServer);
        }
        return stringBuilder;
    }

    /**
     * We pass the querry sting that we created {@link DoSomeHTTPStuff#makeQueryString(Map)} and then fire off the
     * request to the server. We receive the response but we check for errors as well. If the respnose code is less
     * than 400 then we have a proper response and read it using {@link HttpURLConnection#getInputStream()} ,
     * otherwise ther are errors and we read the {@link HttpURLConnection#getErrorStream()}
     * @implNote Note that if the query string is empty then simply fire of the request using the
     * {@link HttpURLConnection#connect()} method
     * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">HTTP response codes</a>
     * @param myHttpConnection The connection instance to be used to handle HTTP stuff
     * @param queryString For the server
     * @return {@link InputStream} depending on the response code of the response
     * @throws IOException
     */
    private static InputStream getInputStreamAfterFiringRequestToServer(HttpURLConnection myHttpConnection,
                                                                        String queryString) throws IOException {
        if (queryString!=null && !queryString.isEmpty()) {
            OutputStream outputStreamToTheServer = myHttpConnection.getOutputStream();
            outputStreamToTheServer.write(queryString.getBytes());
        } else {
            myHttpConnection.connect();
        }

        int responseCode = myHttpConnection.getResponseCode();
        if (responseCode < 400) {
            return myHttpConnection.getInputStream();
        } else {
            return myHttpConnection.getErrorStream();
        }
    }

    /**
     * We <b>decorate</b> the {@linkplain HttpURLConnection} instance with the required headers and specify the HTTP
     * request method if any
     * @see <a href="https://dzone.com/articles/decorator-design-pattern-in-java">Decorator Design Pattern</a>
     * @see <a href="https://tools.ietf.org/html/rfc2616">The RFC of HTTP protocol by IETF</a>
     * @param myHttpConnection
     * @param headers
     * @param requestMethodType
     * @throws ProtocolException
     */
    private static void decorateWithHTTPMethodsAndContentTypeHeader(HttpURLConnection myHttpConnection,
                                                                    Map<String, String> headers,
                                                                    String requestMethodType) throws ProtocolException {
        if (requestMethodType != null && !requestMethodType.isEmpty()){
            myHttpConnection.setRequestMethod(requestMethodType); // Make sure that you pass in a valid name or this
            // will throw an exception
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> contentTypeEntry : headers.entrySet()) {
                myHttpConnection.addRequestProperty(contentTypeEntry.getKey(),contentTypeEntry.getValue());
            }
        }
    }

    /**
     * This method is used to make the request query String that will be sent to the server
     * The format of the request is as follows : <b>parameterName=parameterValue&</b>
     * @param parameterValueMap We use a {@linkplain Map} because the nature of the data is like a bunch of key value
     *                         pairs
     * @return the query sting in the specified format above
     * @throws UnsupportedEncodingException if the specified encoding is not found (in out case this will never
     * happen because we are using the charset that is specified in the Java library and so we are immune to this
     */
    private static String makeQueryString(Map<String,String> parameterValueMap) throws UnsupportedEncodingException {
        if (parameterValueMap == null || parameterValueMap.isEmpty()) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> mapEntry : parameterValueMap.entrySet()) {
                stringBuilder
                        .append(mapEntry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(mapEntry.getValue(), StandardCharsets.UTF_8.name()))
                        .append("&");
            }
            return stringBuilder.toString();
        }
    }
}