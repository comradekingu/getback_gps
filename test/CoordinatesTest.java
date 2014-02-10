/**
 * Unit tests for Coordinates class
 *
 * Copyright (C) 2012-2014 Dieter Adriaenssens
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
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */

import com.github.ruleant.getback_gps.lib.Coordinate;
import com.github.ruleant.getback_gps.lib.CoordinateConverterInterface;
import com.github.ruleant.getback_gps.lib.Coordinates;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Coordinates class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CoordinatesTest extends TestCase {
    /**
     * Instance of the coordinates class.
     */
    private Coordinates coordinates;

    /**
     * Mock for objectConverter class.
     */
    private CoordinateConverterInterface converter;

    /**
     * Test coordinate 0 (0, 0).
     */
    private Coordinate coordinate0;

    /**
     * Test coordinate 1 (0, 20).
     */
    private Coordinate coordinate1;

    /**
     * Test coordinate 2 (30, 40).
     */
    private Coordinate coordinate2;

    /**
     * Test converted coordinate 1 (-20, 0).
     */
    private Coordinate convertedCoordinate1;

    /**
     * Test converted coordinate 2 (-40, -30).
     */
    private Coordinate convertedCoordinate2;

    /**
     * 3 POINTS.
     */
    public static final int NUM_POINTS_3 = 3;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        coordinates = new Coordinates();

        coordinate0 = new Coordinate(0, 0);
        coordinate1 = new Coordinate(0, CoordinateTest.UNIT_20);
        coordinate2 = new Coordinate(CoordinateTest.UNIT_30,
                CoordinateTest.UNIT_40);

        convertedCoordinate1 = new Coordinate(-1 * CoordinateTest.UNIT_20, 0);
        convertedCoordinate2 = new Coordinate(-1 * CoordinateTest.UNIT_40,
                -1 * CoordinateTest.UNIT_30);

        // create mock object
        converter = mock(CoordinateConverterInterface.class);

        // setup mock object
        when(converter.getConvertedCoordinate(coordinate1))
                .thenReturn(convertedCoordinate1);
        when(converter.getConvertedCoordinate(coordinate2))
                .thenReturn(convertedCoordinate2);
    }

    /**
     * Tests empty collection.
     */
    public final void testEmpty() {
        assertEquals(0, coordinates.getSize());
        assertEquals(0, coordinates.toArray().length);
        assertEquals(0, coordinates.toLinesArray().length);
    }

    /**
     * Tests adding Coordinate with polar coordinates.
     */
    public final void testAddPolarCoordinate() {
        coordinates.addCoordinate(CoordinateTest.RADIUS_20,
                                    CoordinateTest.ANGLE_45);
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests adding Coordinate with Cartesian coordinates.
     */
    public final void testAddCartesianCoordinate() {
        coordinates.addCoordinate(CoordinateTest.UNIT_20,
                                    CoordinateTest.UNIT_30);
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.UNIT_20, coordinate.getCartesianX());
        assertEquals(CoordinateTest.UNIT_30, coordinate.getCartesianY());
    }

    /**
     * Tests adding Coordinate with Coordinate instance.
     */
    public final void testAddCoordinate() {
        coordinates.addCoordinate(new Coordinate(CoordinateTest.RADIUS_20,
                                    CoordinateTest.ANGLE_45));
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests null value for new coordinate in setCoordinate.
     */
    public final void testSetCoordinateNull() {
        try {
            coordinates.addCoordinate(null);
            fail("should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Parameter coordinate should not be null",
                    e.getMessage());
        }
    }

    /**
     * Tests adding multiple Coordinates.
     */
    public final void testAddMultipleCoordinates() {
        coordinates.addCoordinate(CoordinateTest.RADIUS_20,
                                    CoordinateTest.ANGLE_45);
        assertEquals(1, coordinates.getSize());

        coordinates.addCoordinate(CoordinateTest.RADIUS_50,
                                    CoordinateTest.ANGLE_90);

        assertEquals(2, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(2, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());

        coordinate = (Coordinate) coordinatesArray[1];
        assertEquals(CoordinateTest.ANGLE_90, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_50, coordinate.getPolarRadius());
    }

    /**
     * Tests toLinesArray, one line.
     */
    public final void testToLinesArray() {
        coordinates.addCoordinate(coordinate1);
        // array should be empty in only 1 point is added,
        // two lines are needed to draw a line
        assertEquals(0, coordinates.toLinesArray().length);

        coordinates.addCoordinate(coordinate2);
        float[] coordinatesArray = coordinates.toLinesArray();
        assertEquals(Coordinates.NUM_COORD_LINE, coordinatesArray.length);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_X]);
        assertCoordinates(coordinatesArray, 0);
    }

    /**
     * Tests toLinesArray, multiple lines.
     */
    public final void testToLinesArrayMulti() {
        coordinates.addCoordinate(coordinate0);
        coordinates.addCoordinate(coordinate1);
        coordinates.addCoordinate(coordinate2);
        float[] coordinatesArray = coordinates.toLinesArray();
        assertEquals(NUM_POINTS_3 * Coordinates.NUM_COORD_LINE,
                coordinatesArray.length);
        // first line
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_X]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_Y]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_END_X]);
        assertEquals((float) CoordinateTest.UNIT_20,
                coordinatesArray[Coordinates.POS_END_Y]);

        // second line
        assertCoordinates(coordinates.toLinesArray(), 1);

        // closing line
        assertEquals((float) CoordinateTest.UNIT_30,
                coordinatesArray[(NUM_POINTS_3 - 1)
                        * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_START_X]);
        assertEquals((float) CoordinateTest.UNIT_40,
                coordinatesArray[(NUM_POINTS_3 - 1)
                        * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_START_Y]);
        assertEquals((float) 0,
                coordinatesArray[(NUM_POINTS_3 - 1)
                        * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_END_X]);
        assertEquals((float) 0,
                coordinatesArray[(NUM_POINTS_3 - 1)
                        * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_END_Y]);
    }

    /**
     * Tests toLinesArray, multiple lines, not closing the line.
     */
    public final void testToLinesArrayMultiNoClosing() {
        coordinates.addCoordinate(coordinate0);
        coordinates.addCoordinate(coordinate1);
        coordinates.addCoordinate(coordinate2);
        coordinates.setCloseLine(false);
        float[] coordinatesArray = coordinates.toLinesArray();
        assertEquals(2 * Coordinates.NUM_COORD_LINE,
                coordinatesArray.length);
        // first line
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_X]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_Y]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_END_X]);
        assertEquals((float) CoordinateTest.UNIT_20,
                coordinatesArray[Coordinates.POS_END_Y]);

        // second line
        assertCoordinates(coordinates.toLinesArray(), 1);
    }

    /**
     * Test closing the line.
     */
    public final void testToLinesArrayClosing() {
        coordinates.addCoordinate(coordinate0);
        coordinates.addCoordinate(coordinate1);
        coordinates.addCoordinate(coordinate2);

        // testing default value (= closing the line)
        // 3 lines expected
        assertEquals(NUM_POINTS_3 * Coordinates.NUM_COORD_LINE,
                coordinates.toLinesArray().length);

        // test not closing the line
        coordinates.setCloseLine(false);
        // 2 lines expected
        assertEquals(2 * Coordinates.NUM_COORD_LINE,
                coordinates.toLinesArray().length);

        // test closing the line
        coordinates.setCloseLine(true);
        // 3 lines expected
        assertEquals(NUM_POINTS_3 * Coordinates.NUM_COORD_LINE,
                coordinates.toLinesArray().length);
    }

    /**
     * Tests setting CoordinateConverter class.
     */
    public final void testSetCoordinateConverter() {
        coordinates.setCoordinateConverter(converter);

        coordinates.addCoordinate(coordinate1);
        coordinates.addCoordinate(coordinate2);
        assertConvertedCoordinates(coordinates.toLinesArray());
    }

    /**
     * Tests constructor taking CoordinateConverter as a parameter.
     */
    public final void testConstructorCoordinateConverter() {
        coordinates = new Coordinates(converter);

        coordinates.addCoordinate(coordinate1);
        coordinates.addCoordinate(coordinate2);
        assertConvertedCoordinates(coordinates.toLinesArray());
    }

    /**
     * Assert unconverted coordinates.
     *
     * @param coordinatesArray array with Cartesian coordinates
     * @param lineNumber number of the line
     *                   (to determine offset in coordinate array)
     */
    private void assertCoordinates(final float[] coordinatesArray,
                                   final int lineNumber) {
        int offset = Coordinates.NUM_COORD_LINE * lineNumber;
        assertEquals((float) 0,
                coordinatesArray[offset + Coordinates.POS_START_X]);
        assertEquals((float) CoordinateTest.UNIT_20,
                coordinatesArray[offset + Coordinates.POS_START_Y]);
        assertEquals((float) CoordinateTest.UNIT_30,
                coordinatesArray[offset + Coordinates.POS_END_X]);
        assertEquals((float) CoordinateTest.UNIT_40,
                coordinatesArray[offset + Coordinates.POS_END_Y]);
    }

    /**
     * Assert converted coordinates.
     *
     * @param coordinatesArray array with Cartesian coordinates
     */
    private void assertConvertedCoordinates(final float[] coordinatesArray) {
        assertEquals(Coordinates.NUM_COORD_LINE, coordinatesArray.length);
        assertEquals((float) -1 * CoordinateTest.UNIT_20,
                coordinatesArray[Coordinates.POS_START_X]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_Y]);
        assertEquals((float) -1 * CoordinateTest.UNIT_40,
                coordinatesArray[Coordinates.POS_END_X]);
        assertEquals((float) -1 * CoordinateTest.UNIT_30,
                coordinatesArray[Coordinates.POS_END_Y]);
    }

    /**
     * Tests null value for new converter in setCoordinateConverter.
     */
    public final void testSetCoordinateConverterNull() {
        try {
            coordinates.setCoordinateConverter(null);
            fail("should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Parameter converter should not be null",
                    e.getMessage());
        }
    }
}
