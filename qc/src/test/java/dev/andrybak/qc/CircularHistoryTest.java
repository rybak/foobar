package dev.andrybak.qc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andrei Rybak
 */
class CircularHistoryTest {

	private static final int FINISH = 23;
	private static final int START = 12;
	private static final int SIZE = FINISH - START + 1;

	@SuppressWarnings("ConstantConditions")
	@Test
	void testThatConstantsAreSane() {
		assertTrue(FINISH > START);
		assertTrue(SIZE > 0);
	}

	@Test
	void testThatOnlyStartingEntryWorksCorrectly() {
		CircularHistory h = new CircularHistory(START);
		for (int i = 0; i < 100; i++)
			assertEquals(START, h.next());
		for (int i = 0; i < 100; i++)
			assertEquals(START, h.prev());
	}

	@Test
	void testThatAddingMovesTheCursor() {
		int i = START;
		CircularHistory h = new CircularHistory(i);
		for (i = 124; i <= FINISH; i++) {
			h.addEntry(i);
			assertEquals(i, h.getCurrent());
		}
	}

	@Test
	void testThatPrevAndNextWork() {
		CircularHistory h = new CircularHistory(START);
		for (int i = START + 1; i <= FINISH; i++)
			h.addEntry(i);

		assertEquals(FINISH, h.getCurrent());
		for (int i = FINISH - 1; i >= START; i--)
			assertEquals(i, h.prev());
		assertEquals(START, h.getCurrent());
		assertEquals(FINISH, h.prev());
		for (int i = FINISH - 1; i >= START; i--)
			assertEquals(i, h.prev());
		assertEquals(START, h.getCurrent());

		for (int i = START + 1; i <= FINISH; i++)
			assertEquals(i, h.next());
		assertEquals(FINISH, h.getCurrent());
		assertEquals(START, h.next());
		for (int i = START + 1; i <= FINISH; i++)
			assertEquals(i, h.next());
		assertEquals(FINISH, h.getCurrent());
	}

	@Test
	void testThatUpdateCurrentIsSane() {
		CircularHistory h = new CircularHistory(START);
		for (int i = START + 1; i <= FINISH; i++)
			h.addEntry(i);
		int arbitrary = (FINISH + START) / 2;
		//noinspection StatementWithEmptyBody
		while (h.next() != arbitrary) {
			// do nothing
		}
		h.updateCurrent(FINISH * 100);
		for (int i = 0; i < SIZE * 10; i++)
			assertNotEquals(arbitrary, h.next());
		for (int i = 0; i < SIZE * 10; i++)
			assertNotEquals(arbitrary, h.prev());
	}

	@Test
	void testThatUpdateCurrentUpdatesCircleCorrectly() {
		CircularHistory h = new CircularHistory(10);
		h.addEntry(20);
		h.updateCurrent(42);
		assertEquals(10, h.prev());
		assertEquals(42, h.prev());
		assertEquals(10, h.next());
		h.updateCurrent(300);
		assertEquals(42, h.next());
		assertEquals(300, h.next());
		assertEquals(42, h.prev());
	}

	@Test
	void testThatDuplicatesBeforeCursorAreRemoved() {
		CircularHistory h = new CircularHistory(10);
		h.addEntry(20);
		h.addEntry(30);
		h.addEntry(40);
		h.prev();
		assertEquals(30, h.getCurrent());
		h.addEntry(10);
		assertEquals(10, h.getCurrent());
		assertEquals(30, h.prev());
		assertEquals(20, h.prev());
		assertEquals(40, h.prev());
		assertEquals(10, h.prev());
		assertEquals(30, h.prev());
	}

	@Test
	void testThatDuplicatesAfterCursorAreRemoved() {
		CircularHistory h = new CircularHistory(50);
		h.addEntry(10);
		h.addEntry(30);
		h.addEntry(20);
		h.addEntry(60);
		h.prev();
		h.prev();
		h.prev();
		assertEquals(10, h.getCurrent());
		h.addEntry(20);
		assertEquals(20, h.getCurrent());
		assertEquals(10, h.prev());
		assertEquals(20, h.next());
		assertEquals(30, h.next());
		assertEquals(60, h.next());
		assertEquals(50, h.next());
		assertEquals(10, h.next());
		assertEquals(20, h.next());
	}

	@Test
	void testThatUpdateCurrentDoesNotRemoveDuplicates() {
		CircularHistory h = new CircularHistory(100);
		h.addEntry(200);
		h.addEntry(300);
		h.addEntry(201);
		h.updateCurrent(200); // go back one comic
		assertEquals(300, h.prev());
		assertEquals(200, h.prev());
		assertEquals(100, h.prev());
		assertEquals(200, h.prev());
		assertEquals(300, h.prev());
	}

	@Test
	void testThatAbandonWorksInTheMiddle() {
		CircularHistory h = new CircularHistory(0);
		h.addEntry(100);
		h.addEntry(200);
		h.addEntry(234);
		h.addEntry(300);
		h.addEntry(400);
		h.prev();
		h.prev();
		assertEquals(234, h.getCurrent());
		assertEquals(200, h.abandonCurrent());
		assertEquals(300, h.next());
		assertEquals(400, h.next());
		assertEquals(0, h.next());
		assertEquals(100, h.next());
		assertEquals(200, h.next());
		assertEquals(300, h.next());
	}

	@Test
	void testThatAbandonWorksAtTheStart() {
		CircularHistory h = new CircularHistory(234);
		h.addEntry(300);
		h.addEntry(400);
		h.prev();
		h.prev();
		assertEquals(234, h.getCurrent());
		assertEquals(300, h.abandonCurrent());
		assertEquals(400, h.next());
		assertEquals(300, h.next());
	}

	@Test
	void testThatLastEntryCannotBeAbandoned() {
		CircularHistory h = new CircularHistory(12345);
		h.addEntry(10);
		h.addEntry(20);
		assertEquals(10, h.abandonCurrent());
		assertEquals(12345, h.abandonCurrent());
		for (int i = 0; i < 10; i++)
			assertEquals(12345, h.abandonCurrent());
	}

	@Test
	void testThatCurrentInTheMiddleIsSerializedLast() {
		CircularHistory h = new CircularHistory(50);
		h.addEntry(10);
		h.addEntry(90);
		h.addEntry(30);
		h.prev();
		h.prev();
		assertEquals(10, h.getCurrent());
		assertEquals(Arrays.asList(90, 30, 50, 10), h.serialize());
	}

	@Test
	void testThatCurrentInTheEndIsSerializedLast() {
		CircularHistory h = new CircularHistory(50);
		h.addEntry(10);
		h.addEntry(90);
		h.addEntry(30);
		assertEquals(30, h.getCurrent());
		assertEquals(Arrays.asList(50, 10, 90, 30), h.serialize());
	}

	@Test
	void testThatDeserializationPutsCursorAtTheEnd() {
		CircularHistory h = CircularHistory.deserialize(Arrays.asList(50, 40, 30, 60));
		assertEquals(60, h.getCurrent());
		assertEquals(30, h.prev());
		assertEquals(60, h.next());
		assertEquals(50, h.next());
		assertEquals(40, h.next());
		assertEquals(30, h.next());
		assertEquals(60, h.next());
	}
}