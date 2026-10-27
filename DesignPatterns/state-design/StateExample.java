/*
 * Study example based on DesignPatterns.pdf, slides 86–93.
 * Printed actions stand in for actual media-player operations.
 * Run: java StateExample.java
 */
public class StateExample {
    public static void main(String[] args) {
        MediaPlayer player = new MediaPlayer(); // Starts in StoppedState.

        player.play();  // StoppedState handles Play and changes to PlayState.
        player.pause(); // PlayState handles Pause and changes to PausedState.
        player.play();  // PausedState handles Play and resumes playback.
        player.stop();  // PlayState handles Stop and changes to StoppedState.
        player.pause(); // StoppedState handles Pause; the state stays stopped.
    }
}

// STATE CONTRACT: each concrete state must handle all three commands.
// context refers to the same MediaPlayer object whose command is being handled.
interface MediaPlayerState {
    void play(MediaPlayer context);
    void pause(MediaPlayer context);
    void stop(MediaPlayer context);
}

// CONTEXT: the player delegates commands to its current state object.
class MediaPlayer {
    private MediaPlayerState currentState;

    public MediaPlayer() {
        currentState = new StoppedState();
    }

    public void setState(MediaPlayerState state) {
        this.currentState = state; // Replace the handler, not the MediaPlayer itself.
    }

    public void play() {
        currentState.play(this); // Give the current handler a reference to this player.
    }

    public void pause() {
        currentState.pause(this);
    }

    public void stop() {
        currentState.stop(this);
    }
}

// Behavior of all commands while STOPPED.
class StoppedState implements MediaPlayerState {
    @Override
    public void play(MediaPlayer context) {
        System.out.println("Starting playback");
        context.setState(new PlayState());
    }

    @Override
    public void pause(MediaPlayer context) {
        System.out.println("Player stopped; cannot pause");
    }

    @Override
    public void stop(MediaPlayer context) {
        System.out.println("Already stopped");
    }
}

// Behavior of all commands while PLAYING.
class PlayState implements MediaPlayerState {
    @Override
    public void play(MediaPlayer context) {
        System.out.println("Already playing");
    }

    @Override
    public void pause(MediaPlayer context) {
        System.out.println("Pausing playback");
        context.setState(new PausedState());
    }

    @Override
    public void stop(MediaPlayer context) {
        System.out.println("Stopping playback");
        context.setState(new StoppedState());
    }
}

// Behavior of all commands while PAUSED.
class PausedState implements MediaPlayerState {
    @Override
    public void play(MediaPlayer context) {
        System.out.println("Resuming playback");
        context.setState(new PlayState());
    }

    @Override
    public void pause(MediaPlayer context) {
        System.out.println("Already paused");
    }

    @Override
    public void stop(MediaPlayer context) {
        System.out.println("Stopping playback");
        context.setState(new StoppedState());
    }
}

/*
 * First command:
 * player.play()
 *     -> currentState.play(this), where currentState refers to StoppedState
 *     -> StoppedState.play(player)
 *         -> context.setState(new PlayState())
 *
 * Next command:
 * player.pause()
 *     -> PlayState.pause(player)
 *         -> context.setState(new PausedState())
 *
 * One MediaPlayer remains throughout. Its currentState reference changes.
 * Pressing Play invokes play() on the CURRENT state, not the destination state.
 */
