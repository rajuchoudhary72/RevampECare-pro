import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * A composable that executes a callback when specific lifecycle events occur.
 * This is useful for running code when the composable (and its containing screen)
 * enters certain lifecycle states, such as ON_RESUME.
 *
 * @param onEvent The callback to be executed when any of the specified [events] occur.
 *                Using [rememberUpdatedState] to ensure the latest lambda is always used.
 * @param events A variable number of [Lifecycle.Event] to observe.
 */
@Composable
fun OnLifecycleEvent(
    onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit,
    vararg events: Lifecycle.Event,
) {
    // Safely update the lambda to be called
    val currentOnEvent by rememberUpdatedState(onEvent)

    // Get the lifecycle owner from the composition
    val lifecycleOwner = LocalLifecycleOwner.current

    // Use DisposableEffect to add and remove the observer
    DisposableEffect(lifecycleOwner, events) {
        val observer = LifecycleEventObserver { owner, event ->
            // Trigger the callback if the current event is in our target list
            if (event in events) {
                currentOnEvent(owner, event)
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // Specify the cleanup logic
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}