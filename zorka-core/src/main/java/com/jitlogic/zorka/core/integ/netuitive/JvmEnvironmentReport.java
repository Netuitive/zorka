package com.jitlogic.zorka.core.integ.netuitive;

import com.jitlogic.zorka.common.util.ZorkaConfig;
import com.jitlogic.zorka.common.util.ZorkaLog;
import com.jitlogic.zorka.common.util.ZorkaLogger;
import com.jitlogic.zorka.common.util.ZorkaUtil;
import com.jitlogic.zorka.core.ZorkaLib;

import java.util.List;

/**
 * This class is used to gather static JVM settings as Element.attributes
 */
public class JvmEnvironmentReport extends AbstractStatsReport {

    /* Logger */
    private final ZorkaLog log = ZorkaLogger.getLog(JvmEnvironmentReport.class);

    public JvmEnvironmentReport(ZorkaConfig config, ZorkaLib zorka) {
        super(config, zorka);
    }

    @Override
    public Element collect(Long timestamp) {
        log.debug(ZorkaLogger.ZPM_DEBUG, "start collecting environment stats");
        try {
            String _mbs = "java";

            //operating system
            String _osmbean = "java.lang:type=OperatingSystem";
            String arch = (String) zorka.jmx(_mbs, _osmbean, "Arch");
            addAttribute("arch", arch);
            String os = (String) zorka.jmx(_mbs, _osmbean, "Name");
            addAttribute("os", os);
            String osVersion = (String) zorka.jmx(_mbs, _osmbean, "Version");
            addAttribute("os.version", osVersion);
            Integer availableProcessors = (Integer) zorka.jmx(_mbs, _osmbean, "AvailableProcessors");
            addAttribute("os.processors", availableProcessors);

            //java virtual machine
            String _jvmmbean = "java.lang:type=Runtime";
            String jvmName = (String) zorka.jmx(_mbs, _jvmmbean, "VmName");
            addAttribute("jvm.name", jvmName);
            String jvmVersion = (String) zorka.jmx(_mbs, _jvmmbean, "VmVersion");
            addAttribute("jvm.version", jvmVersion);
            String jvmVendor = (String) zorka.jmx(_mbs, _jvmmbean, "VmVendor");
            addAttribute("jvm.vendor", jvmVendor);
            String classPath = (String) zorka.jmx(_mbs, _jvmmbean, "ClassPath");
            //addAttribute("jvm.classpath", classPath);
            String[] jvmArgs = (String[]) zorka.jmx(_mbs, _jvmmbean, "InputArguments");
            //addAttribute("jvm.args", ZorkaUtil.join(";", jvmArgs));

            //heap
            String _memmbean = "java.lang:type=Memory";
            Long initHeapSize = (Long) zorka.jmx(_mbs, _memmbean, "HeapMemoryUsage", "init");
            addAttribute("heap.initial", initHeapSize);
            Long maxHeapSize = (Long) zorka.jmx(_mbs, _memmbean, "HeapMemoryUsage", "max");
            addAttribute("heap.max", maxHeapSize);
            log.debug(ZorkaLogger.ZPM_DEBUG, "finished collecting environment stats");
        } catch (Exception e) {
            log.error(ZorkaLogger.ZPM_ERRORS, "finished collecting environment stats with error: ", e);
        }
        return elementBuilder.build();
    }

    private void addAttribute(String name, Object value) {
        elementBuilder.attribute(name, value);
    }
}
