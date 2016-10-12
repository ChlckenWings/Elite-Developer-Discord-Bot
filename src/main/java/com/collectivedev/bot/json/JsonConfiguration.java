package com.collectivedev.bot.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a JSON configuration file.<br>
 * The root of the file must be an object rather than an array.
 */
public class JsonConfiguration {

    private final File destination;

    private JsonObject root = null;

    public JsonConfiguration(File destination) {
        this.destination = destination;
    }

    public JsonConfiguration(String destination) {
        this(new File(destination));
    }

    public JsonObject getRoot() {
        if(root == null) {
            root = (JsonObject) new JsonParser().parse(readJson());
        }

        return root;
    }

    public JsonElement get(String key) {
        return get0(key);
    }

    public String getString(String key) {
        return get(key).getAsString();
    }

    public int getInt(String key) {
        return get(key).getAsInt();
    }

    public long getLong(String key) {
        return get(key).getAsLong();
    }

    public char getChar(String key) {
        return get(key).getAsCharacter();
    }

    public boolean getBoolean(String key) {
        return get(key).getAsBoolean();
    }

    public short getShort(String key) {
        return get(key).getAsShort();
    }

    public byte getByte(String key) {
        return get(key).getAsByte();
    }

    public List<JsonElement> getList(String key) {
        JsonElement e = get(key);

        if(e.isJsonArray()) {
            List<JsonElement> list = new ArrayList<>();
            e.getAsJsonArray().forEach(list::add);
            return list;
        }

        return null;
    }

    private JsonElement get0(String key) {
        String[] parts = key.split("\\.");

        JsonObject o = getRoot();

        for(String part : parts) {
            if(o != null) {
                JsonElement e = o.get(part);

                if(!e.isJsonObject()) {
                    return e;
                } else {
                    o = e.getAsJsonObject();
                }
            } else {
                return null;
            }
        }

        return o;
    }

    public void copyDefaults(String fromFile) {
        destination.getParentFile().mkdirs();

        try (InputStream is = getStream(fromFile)) {
            Files.copy(is, destination.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readJson() {
        return readJsonFromFile(destination.getAbsolutePath());
    }

    private String readJsonFromFile(String file) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private InputStream getStream(String file) {
        return JsonConfiguration.class.getClassLoader().getResourceAsStream(file);
    }

    private void addProperty0(String key, Object value) {
        if(value instanceof Number) {
            root.addProperty(key, (Number) value);
        } else if(value instanceof Boolean) {
            root.addProperty(key, (Boolean) value);
        } else if(value instanceof Character) {
            root.addProperty(key, (Character) value);
        } else if(value instanceof String) {
            root.addProperty(key, (String) value);
        }
    }

    public void addProperty(String key, Object value) {
        addProperty0(key, value);
    }

    public void addProperty(String key, JsonElement element) {
        root.add(key, element);
    }

    public void write() {
        try(Writer writer = new BufferedWriter(new FileWriter(destination))) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}