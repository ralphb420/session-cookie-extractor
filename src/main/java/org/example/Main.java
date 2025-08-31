package org.example;

import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.session.Session;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {

    public static boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        /*
        Setup The Browser Path alongside the desired user data directory
        The user data is for the cookies that were logon
        */
        String browserPath = "C:\\Users\\bitui\\AppData\\Local\\BraveSoftware\\Brave-Browser\\Application\\brave.exe";
        String debugPort = "9222";
        String userDataDir = "C:\\Users\\bitui\\AppData\\Local\\BraveSoftware\\Brave-Browser\\User Data";

        // Launch the browser
        if (!isPortOpen("localhost", Integer.parseInt(debugPort))) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        browserPath,
                        "--remote-debugging-port=" + debugPort,
                        "--user-data-dir=" + userDataDir
                );
                pb.directory(new File("C:\\")); // optional working dir
                pb.start();
                System.out.println("Launched Browser in debug mode on port " + debugPort);

                // Wait a bit to let Brave start up
                Thread.sleep(3000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("Browser already running with remote-debugging-port " + debugPort);
        }



        // Now connect with cdp4j
        try (SessionFactory factory = new SessionFactory("localhost", Integer.parseInt(debugPort))) {
            // Management session (attached to the browser itself)
            try (Session manage = factory.create()) {
                String targetId = manage.getCommand().getTarget().createTarget("about:blank");
                try (Session tab = factory.create(targetId)) {
                    tab.navigate("https://www.eldorado.gg/");
                    tab.waitDocumentReady();

                    var cookies = tab.getCommand().getNetwork().getCookies();
                    cookies.forEach(c ->
                            System.out.println(c.getName() + "=" + c.getValue())
                    );
                }
            }
        }
    }
}