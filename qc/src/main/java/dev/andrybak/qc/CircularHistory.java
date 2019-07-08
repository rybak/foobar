package dev.andrybak.qc;

import java.util.ArrayList;
import java.util.List;

/**
 * Circular buffer. You can insert new entries at the cursor. Replace what's under the cursor. And go around the circle
 * in both directions.
 * <p>
 * Imagine regular reading order as a wheel.
 * <pre>
 *                         current regular reading
 *                               |
 *                               |
 *                               v
 *                    297  7  | 222 |  4017 177
 *                    298  8  | 223 |  4018 178
 *                    299  9  | 224 |  4019 179
 * current history:--[300 10  |<225>|  4020 180]--
 *                    301 11  | 226 |  4021 181
 *                    302 12  | 227 |  4022 182
 *                    303 13  | 228 |  4023 183
 *                    ... ..    ...    .... ...
 *                      \  \            /   /
 *                       \  \          /   /
 *                        \  \        /   /
 *                        possible regular readings
 * </pre>
 * Regular reading rotates a wheel, history jumps to another "regular reading" wheel. When you do regular reading, you
 * replace your remembered place at the current history cursor. Jumping movements (i.e. {@link #addEntry(int)}) add
 * (insert) another wheel to the spoke of the diagram.
 */
class CircularHistory {
	private final List<Integer> delegate = new ArrayList<>();
	private int cursor;

	CircularHistory(int startingValue) {
		delegate.add(startingValue);
		cursor = 0;
	}

	void addEntry(int newEntry) {
		int previousPosition = -1;
		for (int i = 0; i < delegate.size(); i++) {
			int candidate = delegate.get(i);
			if (candidate == newEntry) {
				previousPosition = i;
				break;
			}

		}
		if (previousPosition >= 0) {
			if (previousPosition == cursor)
				return;
			delegate.remove(previousPosition);
			if (previousPosition < cursor)
				cursor--;
		}
		delegate.add(cursor + 1, newEntry);
		cursor++;
	}

	void updateCurrent(int comicNum) {
		delegate.set(cursor, comicNum);
	}

	int next() {
		cursor++;
		if (cursor == delegate.size())
			cursor = 0;
		return getCurrent();
	}

	int prev() {
		cursor--;
		if (cursor == -1)
			cursor = delegate.size() - 1;
		return getCurrent();
	}

	int abandonCurrent() {
		if (delegate.size() == 1) {
			assert cursor == 0;
			return getCurrent();
		}
		delegate.remove(cursor);
		if (cursor == 0)
			return getCurrent();
		return prev();
	}

	/**
	 * only for testing
	 */
	int getCurrent() {
		return delegate.get(cursor);
	}
}
