package com.coofee.multidex
/**
 * Created by zhaocongying on 16/8/8.
 */
public class MultidexExtension {
    public boolean minimalMainDex;
    public int maxIdxNumber;
    public List<String> additionalParameters = new ArrayList<String>();

    public List<String> toDxParameters() {
        List<String> parameters = new ArrayList<String>();
        parameters += '--multi-dex'
        if (this.minimalMainDex) {
            parameters += '--minimal-main-dex'
        } else {
            if (maxIdxNumber > 65535) {
                maxIdxNumber = 65535;
            }
            parameters += "--set-max-idx-number=${maxIdxxNumber}"
        }
        parameters += additionalParameters;
        return parameters;
    }
}
