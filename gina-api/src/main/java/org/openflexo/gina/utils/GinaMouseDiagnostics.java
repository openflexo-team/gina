package org.openflexo.gina.utils;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Flag-gated diagnostic instrumentation for mouse handling across Gina Swing widgets.
 *
 * Enable with {@code -Dgina.mousediag=true}. Inert and zero-cost unless enabled: every
 * public method starts with a check of {@link #MOUSE_DEBUG} (a {@code static final} the
 * JIT folds away), mirroring the existing flag-gated diagnostics conventions in this
 * workspace ({@code -Ddiana.paintdebug}, {@code -Dgina.tablediag}: kept in the tree, reach
 * for it first when a mouse-interaction symptom needs objective data instead of a fresh
 * guess).
 *
 * <p>
 * Every line is prefixed {@code [gina.mouse#n]} with a monotonic sequence number (so
 * interleaved listeners on different components can be reordered when reading the log) and,
 * for RELEASED/CLICKED, the elapsed time and pixel distance since the matching PRESSED on the
 * <em>same</em> component. Those two quantities matter because AWT only synthesizes a
 * {@code MOUSE_CLICKED} event when the release lands on the same component that received the
 * press, and several platforms additionally suppress it if the pointer moved at all in
 * between (this is a documented source of "the click sometimes doesn't register" symptoms,
 * especially with trackpads, and Gina's dispatch of {@code clickAction}/{@code
 * rightClickAction}/{@code doubleClickAction} is entirely driven off {@code mouseClicked} -
 * see {@code SwingViewFactory.SwingMouseAdapter} and {@code JFDTablePanel}). Logging every
 * PRESSED/RELEASED/CLICKED lets a missing CLICKED after a PRESSED+RELEASED pair be spotted
 * directly in the log, which is the objective signature of a suppressed click.
 * </p>
 */
public final class GinaMouseDiagnostics {

	/**
	 * Master switch. System property {@code gina.mousediag}, e.g. {@code
	 * -Dgina.mousediag=true}.
	 */
	public static final boolean MOUSE_DEBUG = Boolean.getBoolean("gina.mousediag");

	private GinaMouseDiagnostics() {
	}

	private static final AtomicLong SEQUENCE = new AtomicLong();

	private static final class PressInfo {
		final Point point;
		final long nanoTime;
		final int button;

		PressInfo(Point point, long nanoTime, int button) {
			this.point = point;
			this.nanoTime = nanoTime;
			this.button = button;
		}
	}

	// Keyed by component identity; a WeakHashMap so instrumented components are never retained.
	private static final Map<Component, PressInfo> LAST_PRESS = Collections.synchronizedMap(new WeakHashMap<Component, PressInfo>());

	/**
	 * Log a {@code mousePressed} event and remember it (component-keyed) so a later
	 * RELEASED/CLICKED on the same component can report elapsed time and pixel drift.
	 */
	public static void logPressed(Component source, MouseEvent e, String widgetLabel) {
		if (!MOUSE_DEBUG) {
			return;
		}
		LAST_PRESS.put(source, new PressInfo(e.getPoint(), System.nanoTime(), e.getButton()));
		print("PRESSED", source, e, widgetLabel, null);
	}

	/** Log a {@code mouseReleased} event, correlated against the last PRESSED on {@code source}. */
	public static void logReleased(Component source, MouseEvent e, String widgetLabel) {
		if (!MOUSE_DEBUG) {
			return;
		}
		print("RELEASED", source, e, widgetLabel, deltaSincePress(source, e));
	}

	/** Log a {@code mouseClicked} event, correlated against the last PRESSED on {@code source}. */
	public static void logClicked(Component source, MouseEvent e, String widgetLabel) {
		if (!MOUSE_DEBUG) {
			return;
		}
		print("CLICKED", source, e, widgetLabel, deltaSincePress(source, e));
	}

	/**
	 * Log whether a FIB mouse action ({@code clickAction}/{@code rightClickAction}/
	 * {@code doubleClickAction}) was actually dispatched for a given widget, and why not when
	 * it wasn't (e.g. no matching action declared in the FIB).
	 */
	public static void logDispatch(String widgetLabel, String actionKind, boolean fired, String detail) {
		if (!MOUSE_DEBUG) {
			return;
		}
		System.err.println("[gina.mouse#" + SEQUENCE.incrementAndGet() + "] DISPATCH widget=" + widgetLabel + " action=" + actionKind
				+ " fired=" + fired + (detail != null ? " | " + detail : ""));
	}

	/**
	 * Log that a FIB mouse action was invoked but raised an exception (today silently
	 * swallowed with a bare {@code printStackTrace()} in {@code FIBWidgetViewImpl}) - from
	 * the user's point of view this looks exactly like "right-click does nothing".
	 */
	public static void logDispatchFailed(String widgetLabel, String actionKind, Throwable t) {
		if (!MOUSE_DEBUG) {
			return;
		}
		System.err.println("[gina.mouse#" + SEQUENCE.incrementAndGet() + "] DISPATCH-FAILED widget=" + widgetLabel + " action="
				+ actionKind + " : " + t.getClass().getSimpleName() + ": " + t.getMessage());
	}

	/**
	 * Log a drag-and-drop gesture lifecycle event ({@code registered}, {@code recognized}...)
	 * on a component also carrying click/right-click dispatch - a candidate suspect for
	 * mouse presses being consumed by DnD machinery instead of reaching the click dispatcher.
	 */
	public static void logDragGesture(Component source, String phase, String detail) {
		if (!MOUSE_DEBUG) {
			return;
		}
		System.err.println("[gina.mouse#" + SEQUENCE.incrementAndGet() + "] DRAG-GESTURE " + phase + " on " + describeComponent(source)
				+ (detail != null ? " | " + detail : ""));
	}

	private static String deltaSincePress(Component source, MouseEvent e) {
		PressInfo press = LAST_PRESS.get(source);
		if (press == null) {
			return "noMatchingPressOnThisComponent=true";
		}
		double distance = press.point.distance(e.getPoint());
		double elapsedMs = (System.nanoTime() - press.nanoTime) / 1_000_000.0;
		return String.format(Locale.ROOT, "sincePress[pressButton=%d, distancePx=%.1f, elapsedMs=%.1f]", press.button, distance,
				elapsedMs);
	}

	private static void print(String phase, Component source, MouseEvent e, String widgetLabel, String extra) {
		StringBuilder sb = new StringBuilder();
		sb.append("[gina.mouse#").append(SEQUENCE.incrementAndGet()).append("] ").append(phase);
		sb.append(" widget=").append(widgetLabel);
		sb.append(" on=").append(describeComponent(source));
		sb.append(" button=").append(e.getButton());
		sb.append(" clickCount=").append(e.getClickCount());
		sb.append(" popupTrigger=").append(e.isPopupTrigger());
		sb.append(" modifiersEx=").append(MouseEvent.getModifiersExText(e.getModifiersEx()));
		sb.append(" point=").append(e.getPoint());
		if (extra != null) {
			sb.append(" | ").append(extra);
		}
		sb.append(" | thread=").append(Thread.currentThread().getName());
		System.err.println(sb);
	}

	private static String describeComponent(Component c) {
		if (c == null) {
			return "null";
		}
		return c.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(c));
	}
}
