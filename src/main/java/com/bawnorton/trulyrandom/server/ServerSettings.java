package com.bawnorton.trulyrandom.server;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ServerSettings {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Modules loadSettings(Path settingsPath, Modules defaultModules) {
        return Files.exists(settingsPath) ? readModules(settingsPath, defaultModules) : writeModules(settingsPath, defaultModules);
    }

    private static Modules readModules(Path settingsPath, Modules defaultModules) {
        JsonElement json;
        try (BufferedReader reader = Files.newBufferedReader(settingsPath)) {
            json = GSON.fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            TrulyRandom.LOGGER.error("Failed to parse settings file, falling back to default.", e);
            return writeModules(settingsPath, defaultModules);
        }
        DataResult<Modules> decodedModules = Modules.CODEC.parse(JsonOps.INSTANCE, json);
        if(decodedModules.isError()) {
            TrulyRandom.LOGGER.error("Failed to parse settings file: {}, falling back to default.", decodedModules.error().map(DataResult.Error::message).orElse("unknown error"));
            return writeModules(settingsPath, defaultModules);
        }
        return writeModules(settingsPath, decodedModules.getOrThrow());
    }

    private static Modules writeModules(Path settingsPath, Modules defaultModules) {
        DataResult<JsonElement> encodedModules = Modules.CODEC.encodeStart(JsonOps.INSTANCE, defaultModules);
        if(encodedModules.isSuccess()) {
            JsonElement json = encodedModules.getOrThrow();
            try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(settingsPath))) {
                writer.setFormattingStyle(FormattingStyle.PRETTY);
                GSON.toJson(json, writer);
            } catch (IOException e) {
                TrulyRandom.LOGGER.warn("Failed to write default modules to {}.", settingsPath);
            }
        }
        return defaultModules;
    }
}
