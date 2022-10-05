Gina Events
===========

This project provides a library of events / listeners designed to record user interactions and system responses.
This is used as a base for the gina-replay project.

Getting started
---------------

# If you want to use this library with FIB, just check the next section.

To use this library you should :

* Instantiate an EventManager

* Create a subclass of EventDescription to describe your event

* Create a class implementing Registrable

* Instantiate a GinaEventNotifier targeting your EventManager, and your Registrable

* Use the raise method of your notifier whenever you want to through a new Event notification

* You can now create a class implementing the interface GinaEventListener

* And register them to the EventManager with addListener(GinaEventListener l)

* Your listener will receive all the notifications by the eventPerformed(GinaEvent e, Stack*GinaStackEvent* stack) method

For detailed information on the different elements, see the rest of this document. You can also find examples
in gina-replay/src/test/java/cases.

Using GinaEvent with FIB
========================

Getting started
---------------

This library has already been implemented in Gina in order to manage FIB event

To use this library you should :

* Instantiate an EventManager

* Give it to any FIB you want to manage, using the instanciateController(FIBComponent, LocalizedDelegate, *EventManager*)
  (By doing this you will register the given FIB and its widgets to this EventManager)

* You can now create a class implementing the interface GinaEventListener

* And register them to the EventManager with addListener(GinaEventListener l)

* Your listener will receive all the notifications by the eventPerformed(GinaEvent e, Stack*GinaStackEvent* stack) method


How to create a new event kind for a new FIB Widget ?
-----------------------------------------------------

* Extend the FIBEventDescription to describe your own event.

* Adapt the FIBEventDescription children hierarchy and the FIBEventFactory, or create a new hierarchy
(in the second option, please remember to register your model when instantiating the EventManager)

* Override the executeEvent method (in gina-core/gina.view.widget.impl) as follow
```
@Override
public void executeEvent(EventDescription e) \{
	widgetExecuting = true;

	switch (e.getAction()) \{
		case FIBMyEventDescription.MY_ACTION:
			// do something
			break;
	}

	widgetExecuting = false;
}
```

* Add code to throw events whenever a certain action is performed (in gina-swing/gina.swing.view.widget)
```
GinaStackEvent stack = CLASSNAME.this.GENotifier.raise(FIBEventFactory.getInstance().createMyEvent(
		FIBMyEventDescription.MY_ACTION, ...));
...
stack.end();
```

Technical information
=====================

What is a GinaEvent and an EventDescrition ?
--------------------------------------------

GinaEvent is used to represent any event that could happen.
It is divided in two :
- UserInteraction : representing an event done by the user (as a click, a selection, ...)
- SystemEvent : representing the response of the system to a stimulus. This stimulus can be a UserInteraction.

In both cases, the GinaEvent (UserInteraction or SystemEvent) has an EventDescription property that contains all the
required data to describe and manage an Event.

This class is abstract and should be extended to support your own events.

An EventDescription contains two kind of data :
- the identity of the event : its emitter and its name (e.g. "Value Changed" for Widget "TextField1" in window X)
- the attributes of the event (e.g. newValue = "text modified")

The emitter is especially identified by a Registrable object, unique in a session.

The name of the event correspond to the action realized (e.g "Checkbox checked"). A namespace is added to avoid
collisions between EventDescriptions.

During recording, an EventDescription has two main purposes :
- it saves the description of an event through its contructor
- it saves the identity of the event through the method setIdentity

During a replay/check session, an EventDescription has multiple roles, it :
- checks if two events have the same identity
- checks if two events have the same attributes
- finds a target object (the widget that emitted the event during the recording session) and command
it to execute the event

When you extend an EventDescription to describe your own event, you should :
- adapt the attributes to describes the identity of the emitter and the event
- override the setIdentity method to describe the identity
- override the matchesIdentity method that check if two EventDescription has the same identity
- override the checkMatchingEvent method that check if two EventDescription has the same attributes
- override the execute method that retrieve the target and transmit the order to execute the event
- override the getNamespace method to specify the namespace of your description

The library enables you to keep track of causality of the events (e.g. what UserInteraction
causes what series of SystemEvent).

How to instantiate a GinaEvent and an EventDescrition ?
-------------------------------------------------------

A GinaEvent is thrown by a GinaEventNotifier. This class is abstract and two methods have to be overrided :
- GinaEvent.KIND computeClass(D d) : used to determined if your event is an UserInteraction or a SystemEvent
- void setIdentity(D d, Object o) : used to set the identity of your eventDescription using an object o

To instantiate the EventDescription you are free to do as you like. You solution we used is an EventFactory.

The GinaEventNotifier uses the raise method to through a GinaEvent.
This method will also create a new stack of events used to determine causality of events, so it is important
to close this event stacks once finished (usally just before leaving the scope where you called the raise method).

```
{
...
GinaStackEvent stack = ginaEventNotifier.raise(eventDescription, this);
...[all events raised here are considered as consequences of our first event,
... that could also be a consequence of another event]
stack.end();
}
```
