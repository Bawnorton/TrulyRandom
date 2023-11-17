package com.bawnorton.trulyrandom.client.network;

import net.fabricmc.loader.api.Version;

public class ClientNetworking {
    private static boolean installedOnServer = false;
    private static Version serverVersion;

    public static boolean isInstalledOnServer() {
        return installedOnServer;
    }

    public static Version getServerVersion() {
        return serverVersion;
    }

    public static void setServerVersion(Version version) {
        installedOnServer = true;
        serverVersion = version;
    }
}
