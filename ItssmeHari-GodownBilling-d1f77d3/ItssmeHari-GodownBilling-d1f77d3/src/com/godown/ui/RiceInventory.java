package com.godown.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiceInventory {
    private static final String FILE_PATH = "rice_inventory.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static List<String> riceVarieties = new ArrayList<>();

    static {
        loadRiceVarieties();
    }

    public static void loadRiceVarieties() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                riceVarieties = objectMapper.readValue(file, new TypeReference<List<String>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getRiceVarieties() {
        return riceVarieties;
    }

    public static void addRiceVariety(String variety) {
        if (!riceVarieties.contains(variety)) {
            riceVarieties.add(variety);
            saveRiceVarieties();
        }
    }

    private static void saveRiceVarieties() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), riceVarieties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
