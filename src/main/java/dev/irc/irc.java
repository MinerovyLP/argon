package dev.irc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class IRC implements ClientModInitializer {
    private static final String PREFIX = "§a[IRC Chat] §6> ";
    private static boolean enabled = false;
    private static WebSocket ws;

    @Override
    public void onInitializeClient() {
        // Register /irc command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("irc")
                .executes(ctx -> {
                    toggle();
                    return 1;
                }));
        });

        // Intercept outgoing chat
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (enabled && !message.startsWith("/")) {
                if (ws != null) {
                    ws.sendText("§f" + MinecraftClient.getInstance().getSession().getUsername() + ": " + message, true);
                }
                return false; // Cancel local send
            }
            return true;
        });
    }

    private static void toggle() {
        enabled = !enabled;
        if (enabled) {
            openConnection();
            sendLocal(PREFIX + "Enabling IRC Chat");
        } else {
            closeConnection();
            sendLocal(PREFIX + "Disabling IRC Chat");
        }
    }

    private static void openConnection() {
        HttpClient client = HttpClient.newHttpClient();
        ws = client.newWebSocketBuilder().buildAsync(URI.create("ws://129.151.220.79:3002"), new WebSocket.Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                sendLocal(PREFIX + "Connected!");
                webSocket.sendText("?auth " + MinecraftClient.getInstance().getSession().getUsername(), true);
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                sendLocal(PREFIX + data);
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                sendLocal(PREFIX + "Disconnected!");
                enabled = false;
                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }
        }).join();
    }

    private static void closeConnection() {
        if (ws != null) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
            ws = null;
        }
    }

    private static void sendLocal(String msg) {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(msg));
        });
    }
}
