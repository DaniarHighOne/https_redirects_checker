package kz.fcbk.echo.core.seederstest.service;


import kz.fcbk.echo.core.seederstest.models.RedirectedUrlStep;
import kz.fcbk.echo.core.seederstest.models.UrlResponseResult;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ResultWriterService {

    private final Lock fileLock = new ReentrantLock();
    private final String outputFile;


    public ResultWriterService(String outputFile) {
        this.outputFile = outputFile;
    }

    public void writeResult(UrlResponseResult result) {
        fileLock.lock();
        try (FileWriter writer = new FileWriter(outputFile,true)) {
            writer.write(formatResult(result));
            writer.write("\n\n");
        } catch (IOException ex) {
            log.error("Write error: {}", ex.getMessage());
        } finally {
            fileLock.unlock();
        }
    }
    private String formatResult(UrlResponseResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Request Result ===\n");
        sb.append("Original URL: ").append(result.url()).append("\n");

        if (result.hasRedirects()) {
            sb.append("Redirect Chain URLS: (").append(result.redirectChain().size()).append(" steps):\n");
            for (int i = 0; i < result.redirectChain().size(); i++) {
                RedirectedUrlStep step = result.redirectChain().get(i);
                sb.append("  ").append(i + 1).append(". ")
                        .append(step.url()).append(" [Status: ").append(step.statusCode()).append("]\n");
            }
            sb.append("Final Status: ").append(result.getFinalStatusCode()).append("\n");
        } else {
            sb.append("No redirects\n");
            sb.append("Status Code: ").append(result.getFinalStatusCode()).append("\n");
        }

        if (result.error() != null) {
            sb.append("Error: ").append(result.error()).append("\n");
        }

        return sb.toString();
    }

}
