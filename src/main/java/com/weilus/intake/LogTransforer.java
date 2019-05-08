package com.weilus.intake;

import java.nio.file.Path;

/**
 * Created by liutq on 2019/3/21.
 */
public interface LogTransforer {

    void readAndWrite(Path log_path, Path logpospath);

}
