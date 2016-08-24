package com.github.randoapp.test.api;

import android.content.Context;

import com.github.randoapp.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

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
        VolleySingleton.getInstance().httpClient = mockClient(HttpStatus.SC_OK,
                "{\"imageURL\":\"http:\\/\\/dev.img.l.rando4me.s3.amazonaws.com\\/24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b.jpg\"" +
                        ",\"mapURL\":\"http:\\/\\/rando4.me\\/map\\/large\\/c425b557fcbde6cd337150d22811837d.jpg\"," +
                        "\"imageSizeURL\":{\"small\":\"http:\\/\\/dev.img.s.rando4me.s3.amazonaws.com\\/24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b.jpg\",\"large\":\"http:\\/\\/dev.img.l.rando4me.s3.amazonaws.com\\/24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b.jpg\",\"medium\":\"http:\\/\\/dev.img.m.rando4me.s3.amazonaws.com\\/24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b.jpg\"}," +
                        "\"randoId\":\"24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b\",\"mapSizeURL\":{\"small\":\"http:\\/\\/rando4.me\\/map\\/small\\/c425b557fcbde6cd337150d22811837d.jpg\",\"large\":\"http:\\/\\/rando4.me\\/map\\/large\\/c425b557fcbde6cd337150d22811837d.jpg\",\"medium\":\"http:\\/\\/rando4.me\\/map\\/medium\\/c425b557fcbde6cd337150d22811837d.jpg\"}," +
                        "\"creation\":1471081017405}");


                /*"{" +
                "'creation': '1383670800877'," +
                "'imageURL': 'http://rando4.me/image/abcd/abcdadfwefwef.jpg'," +
                "'mapURL': ''}");*/
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
                "'mapURL': 'http://rando4.me/map/eeee/eeeewef3242f32f.jpg'" +
                "}," +
                "'stranger': {" +
                "'randoId': 'abcwef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/abc/abcwef3242f32f.jpg', " +
                "'mapURL': 'http://rando4.me/map/azca/azcacwef3242f32f.jpg'" +
                "}" +
                "},{" +
                "'user': {" +
                "'randoId': 'abcdw0ef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/abcd/abcdw0ef3242f32f.jpg', " +
                "'creation': '1383670400877'," +
                "'mapURL': 'http://rando4.me/map/bcde/bcdecwef3242f32f.jpg'" +
                "}," +
                "'stranger': {" +
                "'randoId': 'abcd3cwef3242f32f'," +
                "'imageURL': 'http://rando4.me/image/abcd/abcd3cwef3242f32f.jpg', " +
                "'mapURL': 'http://rando4.me/map/abcd/abcd5wef3242f32f.jpg'" +
                "}" +
                "}]}");
    }

    public static void mockAPIForFetchUserNewAPI(Context context) throws IOException {

        VolleySingleton.getInstance().httpClient = mockClient(HttpStatus.SC_OK, "{\n" +
                "    'email': 'user@gmail.com',\n" +
                "    'out': [\n" +
                "        {\n" +
                "            'creation': 1402667705513,\n" +
                "            'randoId': 'bdae3ae04c121f0169f7b011941eb916fc0c43bd56',\n" +
                "            'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "            'imageSizeURL': {\n" +
                "                'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg'\n" +
                "            },\n" +
                "            'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "            'mapSizeURL': {\n" +
                "                'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "            },\n" +
                "            'delete': 0,\n" +
                "            'report': 0,\n" +
                "            'bonAppetit': 0\n" +
                "        },\n" +
                "        {\n" +
                "            'creation': 1402667703607,\n" +
                "            'randoId': '3721d383e2a339f23f5833e6eb8aeb6eadde65206b',\n" +
                "            'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "            'imageSizeURL': {\n" +
                "                'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg'\n" +
                "            },\n" +
                "            'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "            'mapSizeURL': {\n" +
                "                'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "            },\n" +
                "            'delete': 0,\n" +
                "            'report': 0,\n" +
                "            'bonAppetit': 0\n" +
                "        }\n" +
                "    ],\n" +
                "    'in': [\n" +
                "        {\n" +
                "            'creation': 1402309411776,\n" +
                "            'randoId': 'fd35d7c5086f98c49981280d279e225f6c930a7339',\n" +
                "            'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "            'imageSizeURL': {\n" +
                "                'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg'\n" +
                "            },\n" +
                "            'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "            'mapSizeURL': {\n" +
                "                'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "            },\n" +
                "            'delete': 0,\n" +
                "            'report': 0,\n" +
                "            'bonAppetit': 0\n" +
                "        }\n" +
                "    ],\n" +
                "    'randos': [\n" +
                "        {\n" +
                "            'user': {\n" +
                "                'creation': 1402667705513,\n" +
                "                'randoId': 'bdae3ae04c121f0169f7b011941eb916fc0c43bd56',\n" +
                "                'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                    'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                    'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg'\n" +
                "                },\n" +
                "                'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            },\n" +
                "            'stranger': {\n" +
                "                'creation': 0,\n" +
                "                'randoId': '',\n" +
                "                'imageURL': '',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': '',\n" +
                "                    'medium': '',\n" +
                "                    'small': ''\n" +
                "                },\n" +
                "                'mapURL': '',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': '',\n" +
                "                    'medium': '',\n" +
                "                    'small': ''\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            'user': {\n" +
                "                'creation': 1402667703607,\n" +
                "                'randoId': '3721d383e2a339f23f5833e6eb8aeb6eadde65206b',\n" +
                "                'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                    'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                    'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg'\n" +
                "                },\n" +
                "                'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            },\n" +
                "            'stranger': {\n" +
                "                'creation': 1402309411776,\n" +
                "                'randoId': 'fd35d7c5086f98c49981280d279e225f6c930a7339',\n" +
                "                'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                    'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                    'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg'\n" +
                "                },\n" +
                "                'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}");
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

    public static JSONObject getUserFetchJSONObject() throws JSONException {
        return new JSONObject("{\n" +
                "    'email': 'user@gmail.com',\n" +
                "    'out': [\n" +
                "        {\n" +
                "            'creation': 1402667705513,\n" +
                "            'randoId': 'bdae3ae04c121f0169f7b011941eb916fc0c43bd56',\n" +
                "            'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "            'imageSizeURL': {\n" +
                "                'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg'\n" +
                "            },\n" +
                "            'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "            'mapSizeURL': {\n" +
                "                'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "            },\n" +
                "            'delete': 0,\n" +
                "            'report': 0,\n" +
                "            'bonAppetit': 0\n" +
                "        },\n" +
                "        {\n" +
                "            'creation': 1402667703607,\n" +
                "            'randoId': '3721d383e2a339f23f5833e6eb8aeb6eadde65206b',\n" +
                "            'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "            'imageSizeURL': {\n" +
                "                'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg'\n" +
                "            },\n" +
                "            'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "            'mapSizeURL': {\n" +
                "                'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "            },\n" +
                "            'delete': 0,\n" +
                "            'report': 0,\n" +
                "            'bonAppetit': 0\n" +
                "        }\n" +
                "    ],\n" +
                "    'in': [\n" +
                "        {\n" +
                "            'creation': 1402309411776,\n" +
                "            'randoId': 'fd35d7c5086f98c49981280d279e225f6c930a7339',\n" +
                "            'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "            'imageSizeURL': {\n" +
                "                'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg'\n" +
                "            },\n" +
                "            'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "            'mapSizeURL': {\n" +
                "                'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "            },\n" +
                "            'delete': 0,\n" +
                "            'report': 0,\n" +
                "            'bonAppetit': 0\n" +
                "        }\n" +
                "    ],\n" +
                "    'randos': [\n" +
                "        {\n" +
                "            'user': {\n" +
                "                'creation': 1402667705513,\n" +
                "                'randoId': 'bdae3ae04c121f0169f7b011941eb916fc0c43bd56',\n" +
                "                'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                    'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg',\n" +
                "                    'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/bdae3ae04c121f0169f7b011941eb916fc0c43bd56.jpg'\n" +
                "                },\n" +
                "                'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            },\n" +
                "            'stranger': {\n" +
                "                'creation': 0,\n" +
                "                'randoId': '',\n" +
                "                'imageURL': '',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': '',\n" +
                "                    'medium': '',\n" +
                "                    'small': ''\n" +
                "                },\n" +
                "                'mapURL': '',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': '',\n" +
                "                    'medium': '',\n" +
                "                    'small': ''\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            'user': {\n" +
                "                'creation': 1402667703607,\n" +
                "                'randoId': '3721d383e2a339f23f5833e6eb8aeb6eadde65206b',\n" +
                "                'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                    'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg',\n" +
                "                    'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/3721d383e2a339f23f5833e6eb8aeb6eadde65206b.jpg'\n" +
                "                },\n" +
                "                'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            },\n" +
                "            'stranger': {\n" +
                "                'creation': 1402309411776,\n" +
                "                'randoId': 'fd35d7c5086f98c49981280d279e225f6c930a7339',\n" +
                "                'imageURL': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                'imageSizeURL': {\n" +
                "                    'large': 'http://s3.amazonaws.com/dev.img.l.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                    'medium': 'http://s3.amazonaws.com/dev.img.m.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg',\n" +
                "                    'small': 'http://s3.amazonaws.com/dev.img.s.rando4me/fd35d7c5086f98c49981280d279e225f6c930a7339.jpg'\n" +
                "                },\n" +
                "                'mapURL': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                'mapSizeURL': {\n" +
                "                    'large': 'http://rando4.me/map/large/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'medium': 'http://rando4.me/map/medium/c425b557fcbde6cd337150d22811837d.jpg',\n" +
                "                    'small': 'http://rando4.me/map/small/c425b557fcbde6cd337150d22811837d.jpg'\n" +
                "                },\n" +
                "                'delete': 0,\n" +
                "                'report': 0,\n" +
                "                'bonAppetit': 0\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}");
    }
}
