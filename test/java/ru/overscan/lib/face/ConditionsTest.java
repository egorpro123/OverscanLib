package ru.overscan.lib.face;

import junit.framework.Assert;

import org.junit.Test;

import ru.overscan.lib.face.Conditions;

public class ConditionsTest {

	@Test
	public void shouldInitialize() {
		Conditions conds = new Conditions();
		conds.addCondition("cond1", Conditions.UNCHECKED);
		conds.addCondition("cond2");
		Assert.assertFalse(conds.isOk());
		Assert.assertFalse(conds.isOk("cond1"));
		Assert.assertTrue(conds.isOk("cond2"));
	}	

	@Test
	public void shouldInitializeDefaultOk() {
		Conditions conds = new Conditions();
		conds.addCondition("cond1");
		conds.addCondition("cond2");
		Assert.assertTrue(conds.isOk());
	}	
	
	@Test
	public void shouldSetEnableDisable() {
		Conditions conds = new Conditions();
		conds.addCondition("cond1", Conditions.UNCHECKED);
		conds.addCondition("cond2");
		conds.disable("cond1");
		Assert.assertTrue(conds.isOk());
		conds.enable("cond2");
		Assert.assertFalse(conds.isOk());
		Assert.assertFalse(conds.isOk("cond2"));
	}	

	@Test
	public void shouldSetUncheck() {
		Conditions conds = new Conditions();
		conds.addCondition("cond1");
		conds.addCondition("cond2");
		Assert.assertTrue(conds.isOk());
		conds.uncheck("cond1");
		Assert.assertFalse(conds.isOk());
		Assert.assertFalse(conds.isOk("cond1"));
		Assert.assertTrue(conds.isOk("cond2"));
	}	

	@Test
	public void shouldSetAcceptUnaccept() {
		Conditions conds = new Conditions();
		conds.addCondition("cond1");
		conds.addCondition("cond2");
		Assert.assertTrue(conds.isOk());
		conds.accept("cond1");
		Assert.assertTrue(conds.isOk());
		conds.uncheck("cond1");
		Assert.assertFalse(conds.isOk("cond1"));
		conds.accept("cond1");
		conds.refuse("cond1");
		Assert.assertFalse(conds.isOk("cond1"));
	}	
	
}
