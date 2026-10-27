# State design pattern

Study example based on **Lecture 4 and DesignPatterns.pdf, slides 86–93**. This simplified code and explanation were created for studying; they are not the professor's original code. Printed actions stand in for actual media-player operations.

## Problem statement

A media player can be Stopped, Playing, or Paused. Its response to Play, Pause, and Stop depends on its current state.

For example, Play starts playback when stopped, resumes it when paused, and leaves an already playing player playing.

We want to organize this behavior without repeating a large state-checking conditional inside every command method.

## Analogy: phone modes

A phone reacts differently to an incoming call depending on its mode: it may ring, vibrate, or remain silent. It is the same phone receiving the same event; its current mode determines its behavior.

The media-player example follows the same idea. Its current state determines the meaning of a command.

## Transition rules from the course

| Current state | Play | Pause | Stop |
| --- | --- | --- | --- |
| Stopped | Start; become Playing | Cannot pause; remain Stopped | Already stopped; remain Stopped |
| Playing | Already playing; remain Playing | Pause; become Paused | Stop; become Stopped |
| Paused | Resume; become Playing | Already paused; remain Paused | Stop; become Stopped |

## Naïve approach

Store a string or other state value in the player, then check it inside every command:

```text
play():  check Stopped / Playing / Paused
pause(): check Stopped / Playing / Paused
stop():  check Stopped / Playing / Paused
```

This can work, but adding states or operations requires maintaining many branches. Missing a branch can leave an operation unhandled during execution.

## State pattern solution

[StateExample.java](StateExample.java) contains the complete runnable example.

| Role | Responsibility | Code |
| --- | --- | --- |
| Context | Store the current state and delegate commands | MediaPlayer |
| State interface | Declare operations every concrete state must handle | MediaPlayerState |
| Concrete states | Implement state-specific behavior and transitions | StoppedState, PlayState, PausedState |

Each state class implements all three commands. Even an action that leaves the state unchanged has an implementation, such as printing "Already paused".

## Delegation: let the current state handle the command

The player stores a reference to a MediaPlayerState and forwards each command:

```java
public void play() {
    currentState.play(this);
}
```

Read this as: "Call play on the current state object and pass it a reference to this media player."

- `currentState` is the object deciding what the command means.
- `this` refers to the MediaPlayer whose method is running.
- The state method receives that reference through its `context` parameter.

Passing the reference creates no new MediaPlayer. It lets the state object request a transition on the existing player.

## Changing the state

StoppedState handles Play like this:

```java
@Override
public void play(MediaPlayer context) {
    System.out.println("Starting playback");
    context.setState(new PlayState());
}
```

The new PlayState object becomes the player's current handler. The next command is delegated to that object.

The MediaPlayer remains the same object and class. The phrase "appears to have changed its class" in the definition describes its changed behavior; the player's actual Java class does not change.

## Trace the default example

```java
MediaPlayer player = new MediaPlayer();
player.play();
player.pause();
player.play();
player.stop();
player.pause();
```

| Command | State object handling it | Resulting state |
| --- | --- | --- |
| First `play()` | StoppedState | PlayState |
| `pause()` | PlayState | PausedState |
| Second `play()` | PausedState | PlayState |
| `stop()` | PlayState | StoppedState |
| Final `pause()` | StoppedState | StoppedState |

The first command follows this call flow:

```text
player.play()
    → StoppedState.play(player)
        → start playback
        → player.setState(new PlayState())
```

The next command follows:

```text
player.pause()
    → PlayState.pause(player)
        → pause playback
        → player.setState(new PausedState())
```

Pressing Play runs the play method of the current state, not automatically the method of the destination state.

## Benefits and limits

- Keep each state's behavior together in its own class.
- Make transitions explicit through setState calls.
- Keep the player's command methods small and independent of state-specific branches.
- A concrete state missing a required interface method fails to compile.
- The programmer still must implement the correct transitions; the interface does not verify the business rules in the diagram.
- The design introduces more classes, so it is most useful when the state-dependent behavior warrants that structure.

State is a behavioral pattern: it organizes how the player's response depends on its current state.

## Run the example

With Java 11 or later, run from this folder:

```sh
java StateExample.java
```

Expected output:

```text
Starting playback
Pausing playback
Resuming playback
Stopping playback
Player stopped; cannot pause
```

The default main demonstrates starting, pausing, resuming, stopping, and a command that leaves the player stopped. Change the commands in main to try the other transitions in the table.

## Check your understanding

1. While paused, which state object handles the next play command?
2. Why do state methods receive a MediaPlayer context parameter?
3. Does changing state create a new player or replace its state reference?
4. What error can the compiler catch, and what transition mistakes must the programmer still prevent?
