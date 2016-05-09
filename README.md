
Netuitive Java Agent
=======================

The Netuitive Java Agent leverages the Zorka Agent and Zorka Spy to instrument a programmable bytecode instrumentation engine that's useful in monitoring Java applications and is capable of tracing (profiling) production environments. It is designed to integrate your Java applications seamlessly with [Netuitive](https://http://www.netuitive.com/).

See the Netuitive [help docs](https://help.netuitive.com/Content/Misc/Datasources/new_jvm_datasource.htm) for more information, or contact Netuitive support at [support@netuitive.com](mailto:support@netuitive.com).

Changes to the Zorka Agent
---------------------------
The byte code instrumentation portion of the Java agent remains the same: through shell scripts you can configure the Zorka agent to report method-level statistics in mbeans. The Netuitive Java agent takes over from there. Every minute, the Java Agent uses the Zorka library to grab JVM system metrics and attributes from the mbeans. The agent then takes the data from the mbeans (as counters) and aggregates them into rates/gauge types of values. After aggregation, the Java agent utilizes Netuitive's Cloud API to send the data to analyzed by Netuitive, which is then visualized into graphs.