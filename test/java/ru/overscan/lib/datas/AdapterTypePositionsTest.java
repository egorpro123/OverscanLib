package ru.overscan.lib.data;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by fizio on 30.03.2016.
 */
//public class AdapterTypePositionsTest extends TestCase {
public class AdapterTypePositionsTest {

	@Test
	public void shouldReturnRightType() {
		AdapterTypePositions ps = new AdapterTypePositions();
		ps.add(1, 1);
		ps.add(2, 10);
		ps.add(3, 5);

		Assert.assertTrue(ps.getTypeAtPosition(0) == 1);
		Assert.assertTrue(ps.getTypeAtPosition(1) == 2);
		Assert.assertTrue(ps.getTypeAtPosition(5) == 2);
		Assert.assertTrue(ps.getTypeAtPosition(10) == 2);
		Assert.assertTrue(ps.getTypeAtPosition(11) == 3);
		Assert.assertTrue(ps.getTypeAtPosition(15) == 3);
		Assert.assertTrue(ps.getTypeAtPosition(16) == -1);

	}

	@Test
	public void shouldReturnCounts() {
		AdapterTypePositions tp = new AdapterTypePositions();
		tp.add(1, 1);
		tp.add(2, 10);
		tp.add(3, 5);
		Assert.assertTrue(tp.getTypeCount() == 3);
		Assert.assertTrue(tp.getPositionsCount() == 16);
	}


	@Test
	public void shouldReturnPositionsInType() {
		AdapterTypePositions tp = new AdapterTypePositions();
		tp.add(1, 1);
		tp.add(2, 10);
		tp.add(3, 5);


		Assert.assertTrue(tp.getPositionInType(0) == 0);
        Assert.assertTrue(tp.getPositionInType(1) == 0);
        Assert.assertTrue(tp.getPositionInType(10) == 9);
        Assert.assertTrue(tp.getPositionInType(11) == 0);
        Assert.assertTrue(tp.getPositionInType(12) == 1);
        Assert.assertTrue(tp.getPositionInType(15) == 4);
        Assert.assertTrue(tp.getPositionInType(16) == -1);
	}

}