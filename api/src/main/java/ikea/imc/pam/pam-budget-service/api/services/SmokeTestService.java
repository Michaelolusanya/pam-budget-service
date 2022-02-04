package org.imc.pam.boilerplate.api.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.properties.NetworkProperties;
import org.imc.pam.boilerplate.properties.PathProperties;
import org.imc.pam.boilerplate.tools.GeneralJavaTools;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

@Service
public class SmokeTestService {

    private static final Logger logger = LogManager.getLogger(SmokeTestService.class);

    private final NetworkProperties networkProperties;
    private final PathProperties pathProperties;

    public SmokeTestService(NetworkProperties networkProperties, PathProperties pathProperties) {
        this.networkProperties = networkProperties;
        this.pathProperties = pathProperties;
    }

    public ResponseMsg runSmokeTest(String token) {
        GeneralJavaTools gjt = new GeneralJavaTools();
        try {
            String smokeTestStringResult = executeSmokeTestScript(token);
            logger.warn(smokeTestStringResult);
            JSONArray jsonArr = new JSONArray(smokeTestStringResult);
            List<Object> list = gjt.jsonArrToList(jsonArr);
            return new ResponseMsg(200, list);
        } catch (Exception e) {
            logger.warn("============EXCEPTION IN runSmokeTest=====================");
            logger.warn(e.getMessage());
            System.out.println(e.getMessage());
            return new ResponseMsg(500);
        }
    }

    private String executeSmokeTestScript(String token) {
        logger.warn("================= Smoke Test: Started =================");
        try {
            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            "python3",
                            pathProperties.getSmokeTestPath(),
                            "-d",
                            networkProperties.getDomain(),
                            "-j",
                            token,
                            "-a");
            logger.warn("Running smoke test on: " + pathProperties.getSmokeTestPath());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = in.lines().map(this::parseLine).collect(Collectors.joining());
            logger.warn("================= Smoke Test: Finished =================");
            return result;
        } catch (Exception ex) {
            logger.warn("================= Smoke Test: Crashed =================");
            logger.warn(ex.getMessage());
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private String parseLine(String line) {
        return "0".equals(line) ? "" : line;
    }
}
