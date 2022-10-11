Gina Replay
===========

This project use the Gina Events library to provides a recording/replaying system.
This library should be used to record sessions and to replay them while checking the system responses.

Getting started
---------------

To use this library you should :

* instantiate a GinaReplayManager

```manager = new GinaReplayManager();```

* add the different EventDescription you want it to manage

```manager.addEventDescriptionModels(FIBEventDescription.class);```

* create a GinaReplaySession and register it in the GinaReplayManager

```
GinaReplaySession session = new GinaReplaySession(manager);
session.setCurrentSession(recorder);
```

* start the GinaReplaySession, it will be by default in recording mode except if it is started externally in
replay mode

```session.start();```

* you can use GinaReplaySession.isRecording() to check if the system is in recording mode

For detailed examples, please check gina-replay/src/test/java/cases.

Replay Mode
-----------

To start a replay mode :
* instantiate a ReplayTestConfiguration

```ReplayTestConfiguration testConfiguration = new ReplayTestConfiguration();```

* load a scenario File into it

```
File scenarioDir = ((FileResourceImpl) ResourceLocator.locateResource("scenarii/last-scenario")).getFile();
testConfiguration.loadScenario(scenarioDir);
```

* use runMain method to start the main of the target application, this will init and configure the GinaReplayManager

```testConfiguration.runMain();```

* you can know how many events there are to be played by using Scenario.size

```testConfiguration.getSession().getScenario().size();```

* use GinaReplaySession.checkNextStep to play and check the next event of your scenario

```testConfiguration.getSession().checkNextStep(true);```

GinaReplaySession
-----------------

A GinaReplaySession instance represents a recording/replaying session.
It has a Scenario (a list of events) and read from/write into it.
It also manages the record an replay strategies.

Strategies
----------

There are two strategies : one for recording, and one for checking.
The strategies extend the abstract RecordingStrategy and CheckingStrategy classes and describes how events should
be stored or checked.
Please check out the examples StandardRecordingStrategy and StrictCheckingStrategy.


