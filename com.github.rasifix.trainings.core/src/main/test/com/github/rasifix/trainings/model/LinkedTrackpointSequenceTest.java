package com.github.rasifix.trainings.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.github.rasifix.trainings.model.attr.HeartRateAttribute;

public class LinkedTrackpointSequenceTest {
	
	private TrackpointSequence sequence = new LinkedTrackpointSequence();

	@Test
	public void testEmptySequence() throws Exception {
		assertTrue("expecting empty list", sequence.isEmpty());
		assertEquals(0, sequence.size());
		sequence.add(new Trackpoint(0));
	}
	
	@Test
	public void testAdd() throws Exception {
		sequence.add(new Trackpoint(0));
		
		assertFalse("expecting non-empty list", sequence.isEmpty());
		assertEquals(1, sequence.size());
		
		sequence.add(new Trackpoint(2));
		assertEquals(2, sequence.size());
		
		sequence.add(1, new Trackpoint(1));
		assertEquals(3, sequence.size());
	}
	
	@Test
	public void testRemoveAtEnd() throws Exception {
		Trackpoint last = new Trackpoint(2);
		
		sequence.add(new Trackpoint(0));
		sequence.add(new Trackpoint(1));
		sequence.add(last);
		
		assertEquals(3, sequence.size());
		
		assertTrue(sequence.remove(last));
		assertEquals(2, sequence.size());
		assertFalse(sequence.contains(last));
	}
	
	@Test
	public void testRemoveAtStart() throws Exception {
		Trackpoint first = new Trackpoint(0);
		
		sequence.add(first);
		sequence.add(new Trackpoint(1));
		sequence.add(new Trackpoint(2));
		
		assertEquals(3, sequence.size());
		
		assertTrue(sequence.remove(first));
		assertEquals(2, sequence.size());
		assertFalse(sequence.contains(first));
	}
	
	@Test
	public void testRemoveBetween() throws Exception {
		Trackpoint middle = new Trackpoint(0);
		
		sequence.add(new Trackpoint(0));
		sequence.add(middle);
		sequence.add(new Trackpoint(2));
		
		assertEquals(3, sequence.size());
		
		assertTrue(sequence.remove(middle));
		assertEquals(2, sequence.size());
		assertFalse(sequence.contains(middle));
	}

	@Test
	public void testIsFirst() throws Exception {
		Trackpoint tp = new Trackpoint(0);
		sequence.add(tp);
		assertTrue(sequence.isFirst(tp));
		
		sequence.add(new Trackpoint(1));
		assertTrue(sequence.isFirst(tp));
	}
	
	@Test
	public void testIsLast() throws Exception {
		Trackpoint tp = new Trackpoint(0);
		sequence.add(tp);
		assertTrue(sequence.isLast(tp));
		
		tp = new Trackpoint(1);
		sequence.add(tp);
		assertTrue(sequence.isLast(tp));
	}
	
	@Test
	public void testNavigation() {
		sequence.add(new Trackpoint(0));
		sequence.add(new Trackpoint(1));
		sequence.add(new Trackpoint(2));
		
		Trackpoint tp = sequence.getFirst();
		for (int i = 0; i < sequence.size() - 1; i++) {
			assertTrue(sequence.hasNext(tp));
			tp = sequence.next(tp);
		}
		assertFalse(sequence.hasNext(tp));
		assertEquals(sequence.getLast(), tp);
		
		for (int i = 0; i < sequence.size() - 1; i++) {
			assertTrue(sequence.hasPrevious(tp));
			tp = sequence.previous(tp);
		}
		assertFalse(sequence.hasPrevious(tp));
		assertEquals(sequence.getFirst(), tp);
	}
	
	@Test
	public void testEmptyListIterator() throws Exception {
		ListIterator<Trackpoint> it = sequence.listIterator();
		assertFalse("there should be no next", it.hasNext());
		assertFalse("there should be no previous", it.hasPrevious());
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testExceptionOnNextOnEmptyList() {
		sequence.listIterator().next();
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testExceptionOnPreviousOnEmptyList() {
		sequence.listIterator().previous();
	}
	
	@Test
	public void testSelectOnEmptySequence() throws Exception {
		TrackpointSequence filtered = sequence.select(HeartRateAttribute.class);
		assertTrue(filtered.isEmpty());
	}
	
	@Test
	public void testEmptySelectSequence() throws Exception {
		sequence.add(new Trackpoint(0));
		sequence.add(new Trackpoint(1));
		sequence.add(new Trackpoint(2));
		assertFalse(sequence.isEmpty());
		
		TrackpointSequence filtered = sequence.select(HeartRateAttribute.class);
		assertTrue(filtered.isEmpty());
	}
	
	@Test
	public void testFullSelectSequence() throws Exception {
		sequence.add(createTrackpoint(0, new HeartRateAttribute(100)));
		sequence.add(createTrackpoint(1, new HeartRateAttribute(100)));
		sequence.add(createTrackpoint(2, new HeartRateAttribute(100)));
		assertFalse(sequence.isEmpty());
		
		TrackpointSequence filtered = sequence.select(HeartRateAttribute.class);
		assertFalse(filtered.isEmpty());
		assertEquals(3, filtered.size());
	}
	
	@Test
	public void testPartialSelectSequence() throws Exception {
		sequence.add(createTrackpoint(0, new HeartRateAttribute(100)));
		sequence.add(new Trackpoint(1));
		sequence.add(createTrackpoint(2, new HeartRateAttribute(100)));
		assertFalse(sequence.isEmpty());
		
		TrackpointSequence filtered = sequence.select(HeartRateAttribute.class);
		assertFalse(filtered.isEmpty());
		assertEquals(2, filtered.size());
	}
	
	private static Trackpoint createTrackpoint(long elapsed, TrackpointAttribute attribute) {
		Trackpoint tp = new Trackpoint(elapsed);
		tp.addAttribute(attribute);
		return tp;
	}
	
}
