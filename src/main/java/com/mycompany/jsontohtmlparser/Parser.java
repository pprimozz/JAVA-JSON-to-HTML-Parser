import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Parser {

    private StringBuilder contentBuilder;
    private FileWriter myWriter;
    private StringBuilder result;

    public String convertJSONtoString(String path) {
        this.contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();

    }

    public String parseJsontoHTML(Set<Map.Entry<String, JsonElement>> entries, Boolean firstLoop, StringBuilder result2) {
        this.result = new StringBuilder();

        if (firstLoop == true) {
            result.append("<!DOCTYPE html>");
            result.append(System.getProperty("line.separator"));
            result.append("<HTML>");
            result.append(System.getProperty("line.separator"));
        } else {
            result = result2;
        }

        for (Map.Entry<String, JsonElement> entry : entries) {
            if (entry.getValue().isJsonObject()) {
                if (entry.getKey().equals("head")) {
                    result.append("<" + entry.getKey() + ">");
                    result.append(System.getProperty("line.separator"));
                } else if (entry.getKey().equals("body")) {
                    result.append("<" + entry.getKey() + ">");
                } else if (entry.getKey().equals("viewport")) {
                    result.append("<meta name='" + entry.getKey() + "' content='");
                } else if (entry.getKey().equals("style")) {
                    result.append(entry.getKey() + "=\"");

                    for (Map.Entry<String, JsonElement> entryInnerObjStyle : entry.getValue().getAsJsonObject().entrySet()) {
                        result.append(entryInnerObjStyle.getKey() + ":" + entryInnerObjStyle.getValue().toString().replaceAll("\"", "") + ";");
                    }
                }

                for (Map.Entry<String, JsonElement> entryInnerObj : entry.getValue().getAsJsonObject().entrySet()) {
                    if (entryInnerObj.getValue().isJsonObject()) {
                        if (entryInnerObj.getKey().equals("meta")) {
                            result.append("<" + entryInnerObj.getKey() + " ");
                            parseJsontoHTML(entryInnerObj.getValue().getAsJsonObject().entrySet(), false, result);
                        }
                        if (entryInnerObj.getKey().equals("attributes")) {
                            result.deleteCharAt(result.length() - 1);
                            result.append(" ");
                            parseJsontoHTML(entryInnerObj.getValue().getAsJsonObject().entrySet(), false, result);
                        }

                        if (entryInnerObj.getKey().equals("div")) {
                            result.append("<div> ");
                            for (Map.Entry<String, JsonElement> entryInnerObjDiv : entryInnerObj.getValue().getAsJsonObject().entrySet()) {
                                if (entryInnerObjDiv.getKey().equals("attributes")) {
                                    for (Map.Entry<String, JsonElement> entryInnerObjDivAttr : entryInnerObjDiv.getValue().getAsJsonObject().entrySet()) {
                                        result.deleteCharAt(result.length() - 2);
                                        result.append(entryInnerObjDivAttr.getKey() + "=" + entryInnerObjDivAttr.getValue() + "\"");
                                    }
                                    result.deleteCharAt(result.length() - 2);
                                    result.append(">");
                                    result.append(System.getProperty("line.separator"));
                                } else if (entryInnerObjDiv.getValue().isJsonObject()) {
                                    for (Map.Entry<String, JsonElement> entryInnerObjDivTags : entryInnerObjDiv.getValue().getAsJsonObject().entrySet()) {
                                        result.append("<" + entryInnerObjDiv.getKey() + ">");
                                        result.append(System.getProperty("line.separator"));
                                        result.append("<" + entryInnerObjDivTags.getKey() + ">" + entryInnerObjDivTags.getValue().toString().replaceAll("\"", "") + "</" + entryInnerObjDivTags.getKey() + ">");
                                        result.append(System.getProperty("line.separator"));
                                        result.append("</" + entryInnerObjDiv.getKey() + ">");
                                        result.append(System.getProperty("line.separator"));
                                    }
                                } else {
                                    result.append("<" + entryInnerObjDiv.getKey() + ">" + entryInnerObjDiv.getValue().toString().replaceAll("\"", "") + "</" + entryInnerObjDiv.getKey() + ">");
                                    result.append(System.getProperty("line.separator"));
                                }
                            }
                            result.append("</div> ");
                            result.append(System.getProperty("line.separator"));
                        }
                    }
                    //ČE JE ARRAY
                    else if (entryInnerObj.getValue().isJsonArray()) {
                        // pretvori v JSONArray in loopa
                        System.out.println(entryInnerObj.getKey());
                        JsonArray jsonarr = entryInnerObj.getValue().getAsJsonArray();
                        for (JsonElement ell : jsonarr) {
                            //pretvori vsak element arraya c JSONObject
                            JsonObject jsonArrObj = ell.getAsJsonObject();
                            for (Map.Entry<String, JsonElement> entryInnerArr : jsonArrObj.entrySet()) {
                                if (entryInnerArr.getValue().isJsonObject()) {
                                    System.out.println(entryInnerArr.getKey());
                                    parseJsontoHTML(entryInnerArr.getValue().getAsJsonObject().entrySet(), false, result);
                                } else {
                                    System.out.println(entryInnerArr.getKey() + " " + entryInnerArr.getValue());
                                }
                            }
                        }
                    } else {
                        if (entry.getKey().equals("viewport")) {
                            result.append(entryInnerObj.getKey() + "=" + entryInnerObj.getValue() + ",");
                        } else if (entryInnerObj.getKey().equals("title")) {
                            result.append("<" + entryInnerObj.getKey() + ">" + entryInnerObj.getValue().toString().replaceAll("\"", "") + "</" + entryInnerObj.getKey() + ">");
                            result.append(System.getProperty("line.separator"));
                        } else if (entry.getKey().equals("body")) {
                            result.append("<" + entryInnerObj.getKey() + ">" + entryInnerObj.getValue().toString().replaceAll("\"", "") + "</" + entryInnerObj.getKey() + ">");
                            result.append(System.getProperty("line.separator"));
                        }
                    }
                }
                if (!entry.getKey().equals("html")) {
                    if (entry.getKey().equals("viewport")) {
                        result.deleteCharAt(result.length() - 1);
                        result.append("'>");
                    } else if (entry.getKey().equals("style")) {
                        result.deleteCharAt(result.length() - 1);
                        result.append("\">");
                    } else {
                        result.append("</" + entry.getKey() + ">");
                        result.append(System.getProperty("line.separator"));
                    }
                }
            }
            //ČE JE ARRAY
            else if (entry.getValue().isJsonArray()) {
                // pretvori v JSONArray in loopa
                JsonArray jsonarr = entry.getValue().getAsJsonArray();
                for (JsonElement ell : jsonarr) {
                    //pretvori vsak element arraya c JSONObject
                    JsonObject jsonArrObj = ell.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entryInnerArr : jsonArrObj.entrySet()) {
                        if (entryInnerArr.getValue().isJsonObject()) {
                            System.out.println(entryInnerArr.getKey());
                            parseJsontoHTML(entryInnerArr.getValue().getAsJsonObject().entrySet(), false, result);
                        } else {
                            System.out.println(entryInnerArr.getKey() + " " + entryInnerArr.getValue());
                        }
                    }
                }
            } else {
                if (entry.getKey().equals("charset")) {
                    result.append(entry.getKey() + "=" + entry.getValue() + ">");
                    result.append(System.getProperty("line.separator"));
                } else if (entry.getKey().equals("author")) {
                    result.append("<meta name='" + entry.getKey() + "' content=" + entry.getValue() + ">");
                    result.append(System.getProperty("line.separator"));
                } else if (entry.getKey().equals("keywords")) {
                    result.append("<meta name='" + entry.getKey() + "' content=" + entry.getValue() + ">");
                    result.append(System.getProperty("line.separator"));
                } else if (!entry.getKey().equals("doctype") && !entry.getKey().equals("language")) {
                    result.append(entry.getKey() + "=" + entry.getValue() + " ");
                }
            }

        }
        result.append(System.getProperty("line.separator"));
        return result.toString();
    }

    public void witeFile(String content) {
        content += "\n </HTML>";
        try {
            this.myWriter = new FileWriter("filename.html");
            this.myWriter.write(content);
            this.myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
