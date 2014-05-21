/**
 * Unit tests for CircularAverage class
 *
 * Copyright (C) 2014 Dieter Adriaenssens
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @package com.github.ruleant.getback_gps
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CircularAverage class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class CircularAverageTest {
    /**
     * Value for alpha parameter.
     */
    private static final float ALPHA_VALUE = 0.5f;
    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.0001;
    /**
     * Value reached after 1 cycle with alpha = 0.5 : 50 %.
     */
    private static final float CYCLE1 = 0.5f;
    /**
     * Value reached after 2 cycles with alpha = 0.5 : 75 %.
     */
    private static final float CYCLE2 = 0.75f;
    /**
     * Value reached after 3 cycles with alpha = 0.5 : 87.5 %.
     */
    private static final float CYCLE3 = 0.875f;
    /**
     * Value reached after 4 cycles with alpha = 0.5 : 93.75 %.
     */
    private static final float CYCLE4 = 0.9375f;
    /**
     * Value reached after 5 cycles with alpha = 0.5 : 96.875 %.
     */
    private static final float CYCLE5 = 0.96875f;
    /**
     * Exception message when value is out of range.
     */
    private static final String MESSAGE_VALUE_RANGE
            = "parameter alpha is not in range 0.0 .. 1.0";
    /**
     * Expected Exception.
     */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        assertEquals(0.0f, CircularAverage.getAverageValue(0, 0, 0), ACCURACY);
    }

    /**
     * Tests range of alpha parameter.
     */
    @Test
    public final void testAlphaParameterRange() {
        // valid range for parameter alpha
        assertEquals(0.0f, CircularAverage.getAverageValue(0, 0, 0), ACCURACY);
        assertEquals(
                0.0f,
                CircularAverage.getAverageValue(0, 0, ALPHA_VALUE),
                ACCURACY);
        assertEquals(0.0f, CircularAverage.getAverageValue(0, 0, 1), ACCURACY);
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        // invalid range for parameter alpha
        CircularAverage.getAverageValue(0, 0, -1);
    }

    /**
     * Tests out of range value, bigger than highest allowed value.
     */
    @Test
    public final void testOutOfRangeValueBigger() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        CircularAverage.getAverageValue(0, 0, 2);
    }

    /**
     * Tests getAverageValue() method, with a positive step.
     */
    @Test
    public final void testAverageValuePositiveStep() {
        // initial value = 10, applied step = 50
        testAverageValueAfterStep(10, 50);
        // initial value = 10, applied step = 100
        testAverageValueAfterStep(10, 100);
        // initial value = 10, applied step = 180
        testAverageValueAfterStep(10, 180);

        // initial value = 100, applied step = 100
        testAverageValueAfterStep(100, 100);
        // initial value = 100, applied step = 180
        testAverageValueAfterStep(100, 180);
    }

    /**
     * Tests getAverageValue() method, with a positive step,
     * crossing maximum value, 340°-> 30°.
     */
    @Test
    public final void testAverageValuePositiveStepCrossMax() {
        // initial value = 340
        // applied step = 50
        testAverageValueAfterStep(340, 50);
    }

    /**
     * Tests getAverageValue() method, with a negative step.
     */
    @Test
    public final void testAverageValueNegativeStep() {
        // initial value = 100
        // applied step = -50
        testAverageValueAfterStep(100, -50);
    }

    /**
     * Tests getAverageValue() method, with a positive step,
     * crossing minimum value, 30°-> 340/300/220°.
     */
    @Test
    public final void testAverageValueNegativeStepCrossMin() {
        // initial value = 30, applied step = -50
        testAverageValueAfterStep(30, -50);
        // initial value = 30, applied step = -90
        testAverageValueAfterStep(30, -90);
        // initial value = 30, applied step = -170
        testAverageValueAfterStep(30, -170);
    }

    /**
     * Tests getAverageValue() in 5 cycles after a step is applied.
     *
     * @param initialValue Initial value
     * @param stepValue    Step to apply to initial value
     */
    private void testAverageValueAfterStep(final float initialValue,
                                                 final float stepValue) {
        // check initial state.
        assertEquals(
                FormatUtils.normalizeAngle(initialValue),
                CircularAverage.getAverageValue(initialValue, initialValue,
                        ALPHA_VALUE),
                ACCURACY
        );

        // with an alpha value of .5, the new value should be >95%
        // of the set point value after 5 cycles.
        float setPoint = (float) FormatUtils.normalizeAngle(
                initialValue + stepValue);
        // cycle 1 : 50%
        float expectedCycle1 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE1 * stepValue);
        assertEquals(
                expectedCycle1,
                CircularAverage.getAverageValue(initialValue,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 2 : 75%
        float expectedCycle2 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE2 * stepValue);
        assertEquals(
                expectedCycle2,
                CircularAverage.getAverageValue(expectedCycle1,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 3 : 87.5%
        float expectedCycle3 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE3 * stepValue);
        assertEquals(
                expectedCycle3,
                CircularAverage.getAverageValue(expectedCycle2,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 4 : 93.75%
        float expectedCycle4 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE4 * stepValue);
        assertEquals(
                expectedCycle4,
                CircularAverage.getAverageValue(expectedCycle3,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 5 : 96.875%
        float expectedCycle5 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE5 * stepValue);
        assertEquals(
                expectedCycle5,
                CircularAverage.getAverageValue(expectedCycle4,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
    }
}
