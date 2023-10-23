package org.mate;

import android.content.Context;
import android.os.StrictMode;

import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.MersenneTwister;
import org.mate.commons.utils.TimeoutRun;
import org.mate.commons.utils.manifest.Manifest;
import org.mate.exploration.Algorithm;
import org.mate.interaction.DeviceMgr;
import org.mate.interaction.EnvironmentManager;
import org.mate.interaction.UIAbstractionLayer;
import org.mate.service.MATEService;
import org.mate.utils.coverage.Coverage;
import org.mate.utils.coverage.CoverageUtils;
import org.mate.utils.manifest.ManifestParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MATE {

    // TODO: make singleton
    public MATE(String packageName, Context context) {
        Registry.registerContext(context);

        // should resolve android.os.FileUriExposedException
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // should resolve android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Integer serverPort = null;
        try (FileInputStream fis = context.openFileInput("port");
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            serverPort = Integer.valueOf(reader.readLine());
            MATELog.log_acc("Using server port: " + serverPort);
        } catch (IOException e) {
            MATELog.log_acc("Couldn't read server port, fall back to default port!");
        }

        EnvironmentManager environmentManager;
        try {
            if (serverPort == null) {
                environmentManager = new EnvironmentManager();
            } else {
                environmentManager = new EnvironmentManager(serverPort);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to setup EnvironmentManager", e);
        }
        Registry.registerEnvironmentManager(environmentManager);
        Registry.registerProperties(new Properties(environmentManager.getProperties()));

        if (Properties.RANDOM_SEED() == null) {
            Properties.setProperty("random_seed", System.currentTimeMillis());
        }

        Random rnd = new MersenneTwister(Properties.RANDOM_SEED());
        Registry.registerRandom(rnd);

        MATELog.log_acc("TIMEOUT: " + Properties.TIMEOUT());
        Registry.registerTimeout(Properties.TIMEOUT() * 60 * 1000);

        Registry.registerPackageName(packageName);
        MATELog.log_acc("Package name: " + Registry.getPackageName());

        // try to allocate emulator
        String emulator = Registry.getEnvironmentManager().allocateEmulator(Registry.getPackageName());
        MATELog.log_acc("Emulator: " + emulator);

        if (emulator == null || emulator.isEmpty()) {
            throw new IllegalStateException("Emulator couldn't be properly allocated!");
        }

        MATEService.ensureRepresentationLayerIsConnected();

        try {
            Manifest manifest = ManifestParser.parseManifest(Registry.getPackageName());
            Registry.registerManifest(manifest);
            Registry.registerMainActivity(manifest.getMainActivity());
        } catch (XmlPullParserException | IOException e) {
            throw new IllegalStateException("Couldn't parse AndroidManifest.xml!", e);
        }

        MATELog.log_acc("Main activity: " + Registry.getMainActivity());

        final DeviceMgr deviceMgr = new DeviceMgr(Registry.getPackageName());
        Registry.registerDeviceMgr(deviceMgr);

        // internally checks for permission dialogs and grants permissions if required
        Registry.registerUiAbstractionLayer(new UIAbstractionLayer(deviceMgr, Registry.getPackageName()));

        // check whether the representation layer was built for the same package name as the one
        // we are being asked to test
        String representationLayerPackageName = deviceMgr.getRepresentationLayerTargetPackageName();
        if (!Registry.getPackageName().equals(representationLayerPackageName)) {
            MATELog.log_acc("Representation layer has package name " +
                    representationLayerPackageName +
                    ", but MATE Client was launched for package name " +
                    Registry.getPackageName());
            throw new IllegalStateException("Representation layer was built for another package " +
                    "name!");
        }

        // check whether the AUT could be successfully started
        String currentPackageName = deviceMgr.getCurrentPackageName();
        if (!Registry.getPackageName().equals(currentPackageName)) {
            MATELog.log_acc("Currently displayed app: " + currentPackageName);
            throw new IllegalStateException("Couldn't launch app under test!");
        }

        if (Properties.GRAPH_TYPE() != null) {
            // initialise a graph
            MATELog.log_acc("Initialising graph!");
            Registry.getEnvironmentManager().initGraph();
        }
    }

    /**
     * Executes the given algorithm in a timeout controlled loop.
     *
     * @param algorithm The algorithm to be executed, e.g. random exploration.
     */
    public void testApp(final Algorithm algorithm) {
        Registry.registerRunningAlgorithm(algorithm);

        MATELog.log_acc("Activities:");
        for (String activity : Registry.getUiAbstractionLayer().getActivities()) {
            MATELog.log_acc("\t" + activity);
        }

        try {
            boolean finishedWithoutTimeout = TimeoutRun.timeoutRun(() -> {
                algorithm.run();
                return null;
            }, Registry.getTimeout());

            if (finishedWithoutTimeout) {
                MATELog.log("MATE algorithm run finished without timeout");
            } else {
                MATELog.log("MATE algorithm run finished due to timeout");
            }
        } catch (Exception e) {
            MATELog.log("MATE algorithm run finished with exception: " + e.getMessage());
            e.printStackTrace();
        } finally {

            if (Properties.COVERAGE() != Coverage.NO_COVERAGE) {
                CoverageUtils.logFinalCoverage();
            }

            if (Properties.GRAPH_TYPE() != null) {
                Registry.getEnvironmentManager().drawGraph(Properties.DRAW_RAW_GRAPH());
            }

            Registry.getEnvironmentManager().releaseEmulator();
            // EnvironmentManager.deleteAllScreenShots(packageName);
            try {
                Registry.unregisterEnvironmentManager();
                Registry.unregisterUiAbstractionLayer();
                Registry.unregisterDeviceMgr();
                Registry.unregisterProperties();
                Registry.unregisterRandom();
                Registry.unregisterPackageName();
                Registry.unregisterTimeout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        return sdf.format(new Date());
    }
}
