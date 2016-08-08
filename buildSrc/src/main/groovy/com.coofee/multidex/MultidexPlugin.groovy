package com.coofee.multidex

import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.internal.transforms.DexTransform
import com.android.builder.core.AndroidBuilder
import com.android.builder.core.DexOptions
import com.android.ide.common.process.ProcessException
import com.android.ide.common.process.ProcessOutputHandler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class MultidexPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!project.hasProperty('android') || !project.android.hasProperty('applicationVariants')) {
            return;
        }

        MultidexExtension multidexConfig = project.extensions.create("multidexConfig", MultidexExtension.class);
        project.afterEvaluate {
            // for < 1.5.0;
            project.tasks.matching {
                it.name.startsWith('dex')
            }.each { dx ->
                if (dx.additionalParameters == null) {
                    dx.additionalParameters = []
                }
                dx.additionalParameters += multidexConfig.toDxParameters();
            }

            // support transform api; 1.5.0+
            project.tasks.matching {
                it instanceof TransformTask
            }.each { TransformTask transformTask ->
                Transform transform = transformTask.transform;
                if (transform.name.equals('dex')) {
                    DexTransform dexTransform = (DexTransform) transform;
                    if (dexTransform.multiDex) {
                        // why not work???
                        // replaceMethodConvertByteCode(dexTransform);

                        // wrapper androidBuilder and add dex paramters.
                        replaceFieldAndroidBuilder(dexTransform, multidexConfig);
                    }
                }
            }
        }

        // get collect multidex components task
        def multidexTaskNames = [];
        project.android.applicationVariants.all { variant ->
            def flavorName = variant.flavorName == null ? "" : variant.flavorName;
            def buildType = variant.buildType.name == null ? "" : variant.buildType.name;
            def multidexTaskName = "collect${flavorName.capitalize()}${buildType.capitalize()}MultiDexComponents".toString();
            multidexTaskNames.add(multidexTaskName);
        }

        // 修改manifest_keep文件;
        project.gradle.taskGraph.beforeTask { Task multiDexTask ->
            if (!multidexTaskNames.contains(multiDexTask.name)) {
                return;
            }

            println "multiDexTask=${multiDexTask.name}; multiDexTask.outputFile=${multiDexTask.outputFile}"
            multiDexTask.doLast {
                File originKeepFile = multiDexTask.outputFile
                def tempKeepFile = new File(originKeepFile.parentFile, 'manifest_keep_temp.txt');

                def excludeMainDexs = ["Activity { <init>(); }",
                                       "Service { <init>(); }",
                                       "Receiver { <init>(); }"];

                originKeepFile.eachLine("utf-8") { str, linenumber ->
                    boolean excluded = excludeMainDexs.find { key ->
                        return str.contains(key);
                    }

                    if (!excluded) {
                        tempKeepFile.append("${str}\n")
                    }
                }
                def result = originKeepFile.delete()
                println "delele origin multidex manifest_keep=${originKeepFile} successed? ${result}"

                def mainDexKeepFileReader = new File("${project.projectDir}/main-keep-list.txt").newReader('utf-8');
                tempKeepFile.append(mainDexKeepFileReader);
                result = tempKeepFile.renameTo(originKeepFile);
                println "rename ${tempKeepFile} to ${originKeepFile} successed? ${result}"
            }
        }

    }

    private
    static void replaceFieldAndroidBuilder(DexTransform dexTransform, MultidexExtension multidexConfig) {
        def fieldAndroidBuilder = DexTransform.class.getDeclaredField('androidBuilder');
        fieldAndroidBuilder.setAccessible(true);

        AndroidBuilder androidBuilder = dexTransform.androidBuilder;
        fieldAndroidBuilder.set(dexTransform, AndroidBuilderWrapper.wrapperAndroidBuilder(androidBuilder, multidexConfig));
    }

    private static void replaceMethodConvertByteCode(DexTransform dexTransform) {
        println "replaceMethodConvertByteCode, hook method."
//        AndroidBuilder.class.metaClass.invokeMethod = { String name, Object[] args ->
        // http://stackoverflow.com/questions/10125903/groovy-overriding-invokemethod-for-a-single-instance
//            println "replaceMethodConvertByteCode: name=${name}, args=${args}"
//            try {
//                final int INDEX_ADDITIONAL_PARAMETERS = 5;
//                if ("convertByteCode".equals(name) && (args != null && args.length >= 9)) {
//                    // public void convertByteCode(Collection<File> inputs, File outDexFolder, boolean multidex, File mainDexList, DexOptions dexOptions, List<String> additionalParameters, boolean incremental, boolean optimize, ProcessOutputHandler processOutputHandler) throws IOException, InterruptedException, ProcessException {
//
//                    println "replaceMethodConvertByteCode invoke convertByteCode";
//                    List<String> additionalParameters = (List<String>) args[INDEX_ADDITIONAL_PARAMETERS];
//                    if (additionalParameters == null) {
//                        additionalParameters = new ArrayList<String>();
//                    }
//                    additionalParameters += '--multi-dex'
//                    additionalParameters += '--minimal-main-dex'
//                    // additionalParameters += '--set-max-idx-number=55000'
//                    println "replaceMethodConvertByteCode invoke convertByteCode, additionalParameters=${additionalParameters}";
//                    args[INDEX_ADDITIONAL_PARAMETERS] = additionalParameters;
//                }
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//            delegate.class.metaClass.getMetaMethod(name, args)?.invoke(delegate, args)
//        }

        AndroidBuilder androidBuilder = dexTransform.androidBuilder;
        // public void convertByteCode(Collection<File> inputs, File outDexFolder, boolean multidex, File mainDexList, DexOptions dexOptions, List<String> additionalParameters, boolean incremental, boolean optimize, ProcessOutputHandler processOutputHandler) throws IOException, InterruptedException, ProcessException {
        Class[] argsClasses = [Collection.class, File.class, boolean.class, File.class, DexOptions.class, List.class, boolean.class, boolean.class, ProcessOutputHandler.class].toArray(new Class[0]);
        def methodConvertByteCode = AndroidBuilder.class.metaClass.&pickMethod("convertByteCode", argsClasses);
        println "methodConvertByteCode=${methodConvertByteCode}"
        androidBuilder.class.metaClass.convertByteCode = { Collection<File> inputs, File outDexFolder, boolean multidex, File mainDexList, DexOptions dexOptions, List<String> additionalParameters, boolean incremental, boolean optimize, ProcessOutputHandler processOutputHandler ->
            if (additionalParameters == null) {
                additionalParameters = new ArrayList<String>();
            }
            additionalParameters +=
                    additionalParameters += '--multi-dex'
            additionalParameters += '--minimal-main-dex'
            println "convertByteCode: additionalParameters=${additionalParameters}"
            // additionalParameters += '--set-max-idx-number=55000'
            methodConvertByteCode.invoke(androidBuilder, inputs, outDexFolder, multidex, mainDexList, dexOptions, additionalParameters, incremental, optimize, processOutputHandler);
        };
    }


    private static class AndroidBuilderWrapper extends AndroidBuilder {

        private MultidexExtension multidexConfig;

        public
        static AndroidBuilder wrapperAndroidBuilder(AndroidBuilder androidBuilder, MultidexExtension multidexConfig) {
            return new AndroidBuilderWrapper(androidBuilder, multidexConfig);
        }

        AndroidBuilderWrapper(AndroidBuilder androidBuilder, MultidexExtension multidexConfig) {
            super(androidBuilder.mProjectId, androidBuilder.mCreatedBy, androidBuilder.getProcessExecutor(), androidBuilder.mJavaProcessExecutor, androidBuilder.getErrorReporter(), androidBuilder.getLogger(), androidBuilder.mVerboseExec);
            setTargetInfo(androidBuilder.sdkInfo, androidBuilder.targetInfo, androidBuilder.mLibraryRequests);
            this.multidexConfig = multidexConfig;
        }

        @Override
        void convertByteCode(Collection<File> inputs, File outDexFolder, boolean multidex, File mainDexList, DexOptions dexOptions, List<String> additionalParameters, boolean incremental, boolean optimize, ProcessOutputHandler processOutputHandler) throws IOException, InterruptedException, ProcessException {
            println "AndroidBuilderWrapper invoke convertByteCode";
            if (additionalParameters == null) {
                additionalParameters = new ArrayList<String>();
            }
            additionalParameters += multidexConfig.toDxParameters();
            println "AndroidBuilderWrapper invoke convertByteCode, additionalParameters=${additionalParameters}";
            super.convertByteCode(inputs, outDexFolder, multidex, mainDexList, dexOptions, additionalParameters, incremental, optimize, processOutputHandler)
        }
    }
}