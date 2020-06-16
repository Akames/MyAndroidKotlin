package com.akame.commonlib.abs;

import java.util.List;

/**
 * @Author: Akame
 * @Date: 2019/2/25
 * @Description:
 */
public interface ICompressCallBack {
    void compressSuccess(List<String> paths);

    void compressFail(String errorMsg);
}
