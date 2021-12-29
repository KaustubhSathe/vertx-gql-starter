package config;

import com.typesafe.config.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class HttpConfig {
    private static final int DEFAULT_COMPRESSION_LEVEL = 1;
    private static final int IDLE_TIMEOUT = 30;

    @Optional
    @NonNull
    private String host = "0.0.0.0";

    @Optional
    private int port;

    @Optional
    private int compressionLevel = DEFAULT_COMPRESSION_LEVEL;

    @Optional
    private boolean compressionEnabled = false;

    @Optional
    private int idleTimeOut = IDLE_TIMEOUT;

    @Optional
    private boolean reusePort = true;

    @Optional
    private boolean reuseAddress = true;

    @Optional
    private boolean tcpFastOpen = true;

    @Optional
    private boolean tcpNoDelay = true;

    @Optional
    private boolean tcpQuickAck = true;

    @Optional
    private boolean tcpKeepAlive = true;

    @Optional
    private boolean useAlpn = true;

    public int getPort() {
        return port > 0 ? port : Integer.parseInt(System.getProperty("http.default.port", "8080"));
    }
}
