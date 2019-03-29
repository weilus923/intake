package com.weilus.intake;

import java.util.List;

/**
 * Created by liutq on 2019/3/21.
 */
public interface LogTransforer {

    void out(List<String> lines,String source);
}
