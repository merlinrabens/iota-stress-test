# IOTA Node Stress Test

This is a simple stress test scenario for IOTA nodes by doing fund transfers in a loop. Since this is the first version, PoW has to be done remotely by the node (will be changed soon). The simulation creates HTML reports. The logfile points to their location after the simulation has finished.

## Prerequisites

1. You need a [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [maven](https://maven.apache.org/) installed.
2. You have to set a `JAVA_HOME` environment variable which points to your Java folder. If you don't know how, please have a look at this [tutorial](http://javarevisited.blogspot.de/2012/02/how-to-set-javahome-environment-in.html). You can check if it's set properly by invoking `javac -version`.

## Instructions

1. Clone this repository:

```
git clone https://github.com/bluedigits/iota-stress-test
```

2. Go to the project folder and adjust configuration parameters in the `iota.config` file according to the table below:

```
cd iota-stress-test
```

3. Run the simulation:

```
mvn scala:run
```

## Configuration

Please find below the configuration parameters.
 
Parameter name | Description | Default value        
:---: | :---: | :---: 
host | The IOTA nodes to test. Please specify in format `http://host:port` | N/A
seed | The seed the addresses should be generated from. Please ensure having at least one address with at lest 1 IOTA funds in total. **Caution:** Since the stress test _can_ reuse one or more addresses multiple times you should definitely create a new seed for testing purposes which has only little funds on it. | N/A
security | The security levels used for generating addresses. Can be 1 (less safe), 2, or three (most safe) | 2
depth | The depth used for tip selection | 4
addresses | The max. no. of addresses that will be generated seed-based. They will be re-used heavily by the stress-test | 20
users | The no. of parallel users that will requesting the specified nodes | 1
repetitions | No. of repetitions for the stress test loop | 10
duration | The max. duration the loop will be interrupted after. The unit is _minutes_ | 5  
pause | Pause between requests made to the node. Unit: _milliseconds_ | 0
debug | If you want to see extra output in your logs you can set this property to `true` | false  
