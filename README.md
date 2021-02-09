
Netuitive Java Agent
=======================

The Netuitive Java Agent leverages the Zorka Agent and Zorka Spy to instrument a programmable bytecode instrumentation engine that's useful in monitoring Java applications and is capable of tracing (profiling) production environments. It is designed to integrate your Java applications seamlessly with [Virtana](https://www.virtana.com/products/cloudwisdom/).

See the Netuitive [help docs](https://docs.virtana.com/en/java-agent.html) for more information, or contact Netuitive support at [cloudwisdom.support@virtana.com](mailto:cloudwisdom.support@virtana.com).

Changes to the Zorka Agent
---------------------------
The byte code instrumentation portion of the Java agent remains the same: through shell scripts you can configure the Zorka agent to report method-level statistics in mbeans. The Netuitive Java agent takes over from there. Every minute, the Java Agent uses the Zorka library to grab JVM system metrics and attributes from the mbeans. The agent then takes the data from the mbeans (as counters) and aggregates them into rates/gauge types of values. After aggregation, the Java agent utilizes Netuitive's Cloud API to send the data to analyzed by Netuitive, which is then visualized into graphs.

## Building in Docker
There is Dockerfile in this project for containerized building and testing of the Netuitive Java Agent. To build and test with this Dockerfile run the following:
```
cd zorka/
docker build -t netuitive-java-agent .
docker run -it -e JAVA_API_KEY=<your Netuitive Java API key> netuitive-java-agent
```

The demo application is an interactive command line calculator app, so you need to run the agent in interactive mode. Soon after running the app you will see data coming in under the element **My Application**.
