package com.github.randoapp.test.api;

import org.json.JSONException;
import org.json.JSONObject;

public class APITestHelper {

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
