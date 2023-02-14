package org.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class JSONObjectTest {

    @org.junit.jupiter.api.Test
    void getJSONArray() {
    }

    @org.junit.jupiter.api.Test
    void getNames() {
    }

    @org.junit.jupiter.api.Test
    void has() {
    }

    @org.junit.jupiter.api.Test
    void keys() {
    }

    @org.junit.jupiter.api.Test
    void keySet() {
    }

    @org.junit.jupiter.api.Test
    void names() {
    }

    @org.junit.jupiter.api.Test
    void put() {
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        //JSONObject jsonObject = new JSONObject("{myKey:2}");
        //System.out.println(jsonObject.toString());

        StringBuilder stringBuilder = new StringBuilder();

        try {
            Scanner fileIn = new Scanner(new File("config.json"));
            //fileIn.useDelimiter("");
            while(fileIn.hasNextLine()){
                stringBuilder.append(fileIn.nextLine());
            }//end while
        } catch (FileNotFoundException e) {
            System.out.println("File not found. ");
        }
        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        System.out.println(jsonObject.toString());
    }
}