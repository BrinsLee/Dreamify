package com.brins.commom.utils;

import android.os.Looper;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.log.DrLog;

/**
 * 断言类
 * Debug 模式下起作用
 * @author jasonzuo
 */
public class KGAssert {

	/**
	 * Asserts that two booleans are equal.
	 */
	static public void assertEquals(boolean expected, boolean actual) {
		assertEquals(null, expected, actual);
	}

	static public void assertSubThread() {
		if (DrLog.isDebug())
			assertTrue(Looper.getMainLooper() != Looper.myLooper());
	}

	static public void assertMainThread() {
		if (DrLog.isDebug())
			assertTrue(Looper.getMainLooper() == Looper.myLooper());
	}

	static public void assertNotMainThread() {
		if (DrLog.isDebug())
			assertTrue(Looper.getMainLooper() != Looper.myLooper());
	}


	static public void assertForeProcess() {
		if (DrLog.isDebug())
			assertTrue(DRCommonApplication.isForeProcess());
	}

	static public void assertBackProcess() {
		if (DrLog.isDebug())
			assertTrue(DRCommonApplication.isSupportProcess());
	}

	/**
	 * Asserts that two bytes are equal.
	 */
	static public void assertEquals(byte expected, byte actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two chars are equal.
	 */
	static public void assertEquals(char expected, char actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two doubles are equal concerning a delta. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	static public void assertEquals(double expected, double actual, double delta) {
		assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that two floats are equal concerning a delta. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	static public void assertEquals(float expected, float actual, float delta) {
		assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that two ints are equal.
	 */
	static public void assertEquals(int expected, int actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two longs are equal.
	 */
	static public void assertEquals(long expected, long actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two objects are equal. If they are not an
	 * AssertionFailedError is thrown.
	 */
	static public void assertEquals(Object expected, Object actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two shorts are equal.
	 */
	static public void assertEquals(short expected, short actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two booleans are equal. If they are not an
	 * AssertionFailedError is thrown with the given message.
	 */
	static public void assertEquals(String message, boolean expected,
			boolean actual) {
		if (!DrLog.isDebug())
			return;
		assertEquals(message, Boolean.valueOf(expected), Boolean.valueOf(actual));
	}

	/**
	 * Asserts that two bytes are equal. If they are not an AssertionFailedError
	 * is thrown with the given message.
	 */
	static public void assertEquals(String message, byte expected, byte actual) {
		if (!DrLog.isDebug())
			return;
		assertEquals(message, Byte.valueOf(expected), Byte.valueOf(actual));
	}

	/**
	 * Asserts that two chars are equal. If they are not an AssertionFailedError
	 * is thrown with the given message.
	 */
	static public void assertEquals(String message, char expected, char actual) {
		if (!DrLog.isDebug())
			return;
		assertEquals(message, Character.valueOf(expected), Character.valueOf(actual));
	}

	/**
	 * Asserts that two doubles are equal concerning a delta. If they are not an
	 * AssertionFailedError is thrown with the given message. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	static public void assertEquals(String message, double expected,
			double actual, double delta) {
		if (!DrLog.isDebug())
			return;

		// handle infinity specially since subtracting to infinite values gives
		// NaN and the
		// the following test fails
		if (Double.isInfinite(expected)) {
			if (!(expected == actual))
				failNotEquals(message, Double.valueOf(expected), Double.valueOf(actual));
		} else if (!(Math.abs(expected - actual) <= delta)) // Because
															// comparison
															// with NaN always
															// returns false
			failNotEquals(message, Double.valueOf(expected), Double.valueOf(actual));
	}

	/**
	 * Asserts that two floats are equal concerning a delta. If they are not an
	 * AssertionFailedError is thrown with the given message. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	static public void assertEquals(String message, float expected,
			float actual, float delta) {
		if (!DrLog.isDebug())
			return;

		// handle infinity specially since subtracting to infinite values gives
		// NaN and the
		// the following test fails
		if (Float.isInfinite(expected)) {
			if (!(expected == actual))
				failNotEquals(message, Float.valueOf(expected), Float.valueOf(actual));
		} else if (!(Math.abs(expected - actual) <= delta))
			failNotEquals(message, Float.valueOf(expected), Float.valueOf(actual));
	}

	/**
	 * Asserts that two ints are equal. If they are not an AssertionFailedError
	 * is thrown with the given message.
	 */
	static public void assertEquals(String message, int expected, int actual) {
		if (!DrLog.isDebug())
			return;
		assertEquals(message, Integer.valueOf(expected), Integer.valueOf(actual));
	}

	public static void assertHoldLock(Object lock) {
		if (!DrLog.isDebug())
			return;

		assertTrue(Thread.holdsLock(lock));
	}

	/**
	 * Asserts that two longs are equal. If they are not an AssertionFailedError
	 * is thrown with the given message.
	 */
	static public void assertEquals(String message, long expected, long actual) {
		if (!DrLog.isDebug())
			return;
		assertEquals(message, Long.valueOf(expected), Long.valueOf(actual));
	}

	/**
	 * Asserts that two objects are equal. If they are not an
	 * AssertionFailedError is thrown with the given message.
	 */
	static public void assertEquals(String message, Object expected,
			Object actual) {
		if (!DrLog.isDebug())
			return;

		if (expected == null && actual == null)
			return;
		if (expected != null && expected.equals(actual))
			return;
		failNotEquals(message, expected, actual);
	}

	/**
	 * Asserts that two shorts are equal. If they are not an
	 * AssertionFailedError is thrown with the given message.
	 */
	static public void assertEquals(String message, short expected, short actual) {
		if (!DrLog.isDebug())
			return;
		assertEquals(message, new Short(expected), new Short(actual));
	}

	/**
	 * Asserts that two Strings are equal.
	 */
	static public void assertEquals(String expected, String actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two Strings are equal.
	 */
	static public void assertEquals(String message, String expected,
			String actual) {
		if (!DrLog.isDebug())
			return;

		if (expected == null && actual == null)
			return;
		if (expected != null && expected.equals(actual))
			return;

		fail(format(message, expected, actual));
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * AssertionFailedError.
	 */
	static public void assertFalse(boolean condition) {
		assertFalse(null, condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * AssertionFailedError with the given message.
	 */
	static public void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}

	/**
	 * Asserts that an object isn't null.
	 */
	static public void assertNotNull(Object object) {
		assertNotNull(null, object);
	}

	/**
	 * Asserts that an object isn't null. If it is an AssertionFailedError is
	 * thrown with the given message.
	 */
	static public void assertNotNull(String message, Object object) {
		assertTrue(message, object != null);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not the
	 * same an AssertionFailedError is thrown.
	 */
	static public void assertNotSame(Object expected, Object actual) {
		assertNotSame(null, expected, actual);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not an
	 * AssertionFailedError is thrown with the given message.
	 */
	static public void assertNotSame(String message, Object expected,
			Object actual) {
		if (expected == actual)
			failSame(message);
	}

	/**
	 * Asserts that an object is null.
	 */
	static public void assertNull(Object object) {
		assertNull(null, object);
	}

	/**
	 * Asserts that an object is null. If it is not an AssertionFailedError is
	 * thrown with the given message.
	 */
	static public void assertNull(String message, Object object) {
		assertTrue(message, object == null);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not the
	 * same an AssertionFailedError is thrown.
	 */
	static public void assertSame(Object expected, Object actual) {
		assertSame(null, expected, actual);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not an
	 * AssertionFailedError is thrown with the given message.
	 */
	static public void assertSame(String message, Object expected, Object actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * AssertionFailedError.
	 */
	static public void assertTrue(boolean condition) {
		assertTrue(null, condition);
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * AssertionFailedError with the given message.
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition)
			fail(message);
	}

	/**
	 * Fails a test with no message.
	 */
	static public void fail() {
		fail("");
	}

	/**
	 * Fails a test with the given message.
	 */
	static public void fail(String message) {
		if (DrLog.isDebug()) {
			throw new Error(message);
		}
	}

	static public void fail(Throwable throwable) {
		if (DrLog.DEBUG) {
			throw new Error(throwable);
		}
	}

	static private void failNotEquals(String message, Object expected,
			Object actual) {
		if (!DrLog.isDebug())
			return;
		fail(format(message, expected, actual));
	}

	static private void failNotSame(String message, Object expected,
			Object actual) {
		if (!DrLog.isDebug())
			return;
		String formatted = "";
		if (message != null)
			formatted = message + " ";
		fail(formatted + "expected same:<" + expected + "> was not:<" + actual
				+ ">");
	}

	static private void failSame(String message) {
		if (!DrLog.isDebug())
			return;
		String formatted = "";
		if (message != null)
			formatted = message + " ";
		fail(formatted + "expected not same");
	}

	static String format(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null)
			formatted = message + " ";
		return formatted + "expected:<" + expected + "> but was:<" + actual
				+ ">";
	}

	static public void logFail() {
		try {
			fail();
		} catch (Error e) { // kg-suppress REGULAR.ERROR-1
			if (DrLog.DEBUG) DrLog.e("AssertionFailedError: ", e.getMessage());
		}
	}

	static public void logFail(String message) {
		try {
			fail(message);
		} catch (Error e) { // kg-suppress REGULAR.ERROR-1
			if (DrLog.DEBUG) DrLog.e("AssertionFailedError: ", e.getMessage());
		}
	}

	static public void logNotNull(Object object) {
		if (!DrLog.isDebug())
			return;
		try {
			assertNotNull(null, object);
		} catch (Error e) { // kg-suppress REGULAR.ERROR-1
			if (DrLog.DEBUG) DrLog.e("AssertionFailedError: ", e.getMessage());
		}
	}

	static public void logNotNull(String message, Object object) {
		if (!DrLog.isDebug())
			return;
		try {
			assertTrue(message, object != null);
		} catch (Error e) { // kg-suppress REGULAR.ERROR-1
			if (DrLog.DEBUG) DrLog.e("AssertionFailedError: ", e.getMessage());
		}
	}

	static public void logTrue(boolean condition) {
		try {
			assertTrue(condition);
		} catch (Error e) { // kg-suppress REGULAR.ERROR-1
			if (DrLog.DEBUG) DrLog.e("AssertionFailedError: ", e.getMessage());
		}
	}

	static public void logTrue(String message, boolean condition) {
		try {
			assertTrue(message, condition);
		} catch (Error e) { // kg-suppress REGULAR.ERROR-1
			if (DrLog.DEBUG) DrLog.e("AssertionFailedError: ", e.getMessage());
		}
	}

	static public void fail(String message, Throwable cause) {
		if (DrLog.isDebug()) {
			if (cause != null) {
				throw new Error(message, cause);
			} else {
				throw new Error(message);
			}
		}
	}

	protected KGAssert() {
	}
}
