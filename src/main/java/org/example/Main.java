package org.example;

import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.session.Session;

import java.util.List;
import java.io.File;
import java.io.IOException;
import io.webfolder.cdp.type.target.TargetInfo;
import java.net.Socket;
import java.util.stream.Collectors;

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
                pb.directory(new File("C:\\"));
                pb.start();
                System.out.println("Launched Browser in debug mode on port " + debugPort);

                Thread.sleep(3000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("Browser already running with remote-debugging-port " + debugPort);
        }



        // cdp4j api
        try (SessionFactory factory = new SessionFactory("localhost", Integer.parseInt(debugPort))) {
            try (Session session = factory.create()) {
                session.navigate("https://www.eldorado.gg/");
                session.waitDocumentReady();

                // format header
                String cookieHeader = session.getCommand().getNetwork().getCookies()
                        .stream()
                        .map(c -> c.getName() + "=" + c.getValue())
                        .collect(Collectors.joining("; "));

                System.out.println("Cookies for session:");
                System.out.println(cookieHeader);
            }
        }
    }
}
