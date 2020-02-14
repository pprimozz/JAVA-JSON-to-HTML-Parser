/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * @author Primož
 */
public class Main {
    public static void main(String[] args) throws IOException {

        Parser myparser = new Parser();

        //tukaj se setira vhodna JSON datoteka
        String filePath = "C:\\Users\\Primoz\\Documents\\JSONtoHTMLParser\\src\\main\\java\\helloWorld.json";

        //najprej konvertira json file v String zaradi lažje nadaljnje obdelave
        String jsonStr = myparser.convertJSONtoString(filePath);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonStr);
        JsonObject obj = element.getAsJsonObject();

        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        StringBuilder result = new StringBuilder();
        String content = myparser.parseJsontoHTML(entries, true, result);

        myparser.witeFile(content);

    }
}


