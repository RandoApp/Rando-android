package com.github.randoapp.test.api;

import com.github.randoapp.network.VolleySingleton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APITestHelper {

    public static void mockAPIWithError() throws IOException {
        VolleySingleton.getInstance().httpClient = mockClient(HttpStatus.SC_INTERNAL_SERVER_ERROR, "{'code': '501'," +
                "'message': 'Internal Server Error'," +
                "'description': 'See https://github.com/dimhold/rando/wiki/Errors/#system'}");
    }

    public static void mockAPIForUploadFood() throws IOException {
        VolleySingleton.getInstance().httpClient = mockClient(HttpStatus.SC_OK, "{" +
                "'creation': '1383670800877'," +
                "'imageURL': 'http://rando4.me/image/abcd/abcdadfwefwef.jpg'," +
                "'mapURL': ''}");
    }

    public static void mockAPIForDownloadFood() throws IOException {
        VolleySingleton.getInstance().httpClient = mockClient(HttpStatus.SC_OK, "jpg file");
    }

    public static void mockAPIForFetchUser() throws IOException {
        VolleySingleton.getInstance().httpClient = mockClient(HttpStatus.SC_OK, "{'email': 'user@mail.com'," +
                "'randos': [" +
                "{" +
                "'user': {" +
                "'randoId': 'ddddcwef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/dddd/ddddcwef3242f32f.jpg', " +
                "'creation': '1383690800877'," +
                "'mapURL': 'http://rando4.me/map/eeee/eeeewef3242f32f.jpg'," +
                "'bonAppetit': '0'" +
                "}," +
                "'stranger': {" +
                "'randoId': 'abcwef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/abc/abcwef3242f32f.jpg', " +
                "'mapURL': 'http://rando4.me/map/azca/azcacwef3242f32f.jpg'," +
                "'bonAppetit': '1'" +
                "}" +
                "},{" +
                "'user': {" +
                "'randoId': 'abcdw0ef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/abcd/abcdw0ef3242f32f.jpg', " +
                "'creation': '1383670400877'," +
                "'mapURL': 'http://rando4.me/map/bcde/bcdecwef3242f32f.jpg'," +
                "'bonAppetit': '1'" +
                "}," +
                "'stranger': {" +
                "'randoId': 'abcd3cwef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/abcd/abcd3cwef3242f32f.jpg', " +
                "'mapURL': 'http://rando4.me/map/abcd/abcd5wef3242f32f.jpg'," +
                "'bonAppetit': '0'" +
                "}" +
                "}]}");
    }

    public static void mockAPI(int statusCode, String response) throws IOException {
        VolleySingleton.getInstance().httpClient = mockClient(statusCode, response);
    }

    private static HttpClient mockClient(int statusCode, String response) throws IOException {
        HttpEntity entityMock = mock(HttpEntity.class);
        when(entityMock.getContent()).thenReturn(new ByteArrayInputStream(response.getBytes()));

        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(statusCode);

        HttpResponse responseMock = mock(HttpResponse.class);
        when(responseMock.getEntity()).thenReturn(entityMock);
        when(responseMock.getStatusLine()).thenReturn(statusLineMock);

        HttpClient clientMock = mock(HttpClient.class);
        when(clientMock.execute(isA(HttpUriRequest.class))).thenReturn(responseMock);

        return clientMock;
    }
}
