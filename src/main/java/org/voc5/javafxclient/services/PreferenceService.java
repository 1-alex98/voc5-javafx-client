package org.voc5.javafxclient.services;

import com.google.gson.Gson;
import org.voc5.javafxclient.dto.Preferences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class PreferenceService {
    private static final String APP_DATA_SUB_FOLDER = "voc5";
    private static final String USER_HOME_SUB_FOLDER = ".voc5";
    private static final String PREFS_FILE_NAME = "client.pefs";
    private static PreferenceService preferenceService;
    private final Path prefFileLocation;
    private final Gson gson = new Gson();
    private Preferences preferences;

    private PreferenceService() {
        Path preferenceFileLocation = getPreferenceFileLocation();
        prefFileLocation = preferenceFileLocation.resolve(PREFS_FILE_NAME);
        if (Files.exists(prefFileLocation)) {
            readPreferences();
        } else {
            preferences = new Preferences();
        }
    }

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    public static PreferenceService getInstance() {
        if (preferenceService == null) {
            preferenceService = new PreferenceService();
        }
        return preferenceService;
    }

    private void readPreferences() {
        try {
            String content = Files.readString(prefFileLocation);
            preferences = gson.fromJson(content, Preferences.class);
        } catch (IOException e) {
            preferences = new Preferences();
            e.printStackTrace();
        }
    }

    public void storeInBackGround() {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Files.exists(prefFileLocation.getParent())) {
                    Files.createDirectories(prefFileLocation.getParent());
                }
                Files.writeString(prefFileLocation, gson.toJson(preferences));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Path getPreferenceFileLocation() {
        if (getOsName().startsWith("Windows")) {
            return Paths.get(System.getenv("APPDATA")).resolve(APP_DATA_SUB_FOLDER);
        }
        return Paths.get(System.getProperty("user.home")).resolve(USER_HOME_SUB_FOLDER);
    }

    public Preferences getPreferences() {
        return preferences;
    }
}
