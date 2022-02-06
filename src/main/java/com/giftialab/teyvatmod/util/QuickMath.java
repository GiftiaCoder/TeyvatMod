package com.giftialab.teyvatmod.util;

public class QuickMath {

	private static final int SAMPLE_RATE = 100;
	private static final int MAP_SIZE = 360 * SAMPLE_RATE;
	private static final float[] COSA_VAL_MAP = new float[MAP_SIZE];
	private static final float[] SINA_VAL_MAP = new float[MAP_SIZE];
	private static final float[] TANA_VAL_MAP = new float[MAP_SIZE];
	
	private static final int ATANA_SAMPLE_RATE = 500;
	private static final int ATANA_VAL_RANGE = 80;
	private static final int ATANA_KEY_UP_BOUND = ATANA_SAMPLE_RATE * ATANA_VAL_RANGE;
	private static final int ATANA_KEY_LOW_BOUND = -ATANA_KEY_UP_BOUND;
	private static final int ATANA_MAP_SIZE = ATANA_KEY_UP_BOUND * 2 + 1;
	private static final float[] ATANA_VAL_MAP = new float[ATANA_MAP_SIZE];
	
	private static final int ASINA_SAMPLE_RATE = 1000;
	private static final float[] ASINA_VAL_MAP = new float[ASINA_SAMPLE_RATE * 2 + 1];
	
	static {
		for (int i = 0; i < MAP_SIZE; ++i) {
			double angle = (Math.PI * i) / (180.0 * SAMPLE_RATE);
			COSA_VAL_MAP[i] = (float) Math.cos(angle);
			SINA_VAL_MAP[i] = (float) Math.sin(angle);
			TANA_VAL_MAP[i] = (float) Math.tan(angle);
		}
		for (int i = 0, j = ATANA_KEY_LOW_BOUND; i < ATANA_MAP_SIZE; ++i, ++j) {
			ATANA_VAL_MAP[i] = (float) (Math.atan((double) j / ATANA_SAMPLE_RATE) * 180 / Math.PI);
		}
		for (int i = -ASINA_SAMPLE_RATE, j = 0; i <= ASINA_SAMPLE_RATE; ++i, ++j) {
			ASINA_VAL_MAP[j] = (float) (Math.asin((double) i / ASINA_SAMPLE_RATE) * 180 / Math.PI);
		}
	}
	
	public static float cosA(float a) {
		return COSA_VAL_MAP[Math.abs((int) (a * SAMPLE_RATE)) % MAP_SIZE];
	}
	public static float sinA(float a) {
		return SINA_VAL_MAP[Math.abs((int) (a * SAMPLE_RATE)) % MAP_SIZE];
	}
	public static float tanA(float a) {
		return TANA_VAL_MAP[Math.abs((int) (a * SAMPLE_RATE)) % MAP_SIZE];
	}
	
	public static float atanA(float t) {
		int key = (int) (t * ATANA_SAMPLE_RATE);
		if (key > ATANA_KEY_UP_BOUND) {
			return 90;
		} else if (key < ATANA_KEY_LOW_BOUND) {
			return -90;
		}
		return ATANA_VAL_MAP[key + ATANA_KEY_UP_BOUND];
	}
	public static float vecToAng(float x, float z) {
		if (x != 0) {
			float ang = atanA(z / x);
			if (x < 0) {
				ang += 180;
			}
			if (ang < 0) {
				ang += 360;
			}
			return ang;
		} else {
			return 90;
		}
	}
	
	public static float asinA(float f) {
		return ASINA_VAL_MAP[(int) (f * ASINA_SAMPLE_RATE)];
	}
//	public static float acosA(float f) {
//		// TODO
//		return 0;
//	}
	
}
