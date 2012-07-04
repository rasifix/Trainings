package com.github.rasifix.trainings.model;

import java.util.AbstractSequentialList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class LinkedTrackpointSequence extends AbstractSequentialList<Trackpoint> implements TrackpointSequence {
	
	private final Map<Trackpoint, Entry> lookup = new HashMap<Trackpoint, Entry>();
	
	private final Entry head = new Entry();
	
	private final Entry tail = new Entry();
	
	private int size;
	
	public LinkedTrackpointSequence() {
		head.next = tail;
		tail.previous = head;
	}
	
	@Override
	public boolean isFirst(Trackpoint trackpoint) {
		return trackpoint == getFirst();
	}
	
	@Override
	public Trackpoint getFirst() {
		if (head.next == null) {
			throw new NoSuchElementException();
		}
		return head.next.trackpoint;
	}
	
	@Override
	public void addFirst(Trackpoint trackpoint) {
		Entry entry = createEntry(trackpoint, head, head.next);
		head.next.previous = entry;
		head.next = entry;
		size += 1;
	}
	
	@Override
	public Trackpoint removeFirst() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		Entry entry = head.next;
		removeEntry(entry);
		lookup.remove(entry.trackpoint);
		return entry.trackpoint;
	}
	
	@Override
	public boolean isLast(Trackpoint trackpoint) {
		return trackpoint == getLast();
	}
	
	@Override
	public Trackpoint getLast() {
		if (tail.previous== null) {
			throw new NoSuchElementException();
		}
		return tail.previous.trackpoint;
	}
	
	@Override
	public void addLast(Trackpoint trackpoint) {
		Entry entry = createEntry(trackpoint, tail.previous, tail);
		tail.previous.next = entry;
		tail.previous = entry;
		size += 1;
	}
	
	@Override
	public Trackpoint removeLast() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		Entry entry = tail.previous;
		removeEntry(entry);
		lookup.remove(entry.trackpoint);
		return entry.trackpoint;
	}
	
	private Entry lookup(Trackpoint trackpoint) {
		Entry entry = lookup.get(trackpoint);
		if (entry == null) {
			throw new IllegalArgumentException("trackpoint not contained in this sequence");
		}
		return entry;
	}

	@Override
	public boolean hasNext(Trackpoint trackpoint) {
		return lookup(trackpoint).next != tail;
	}

	@Override
	public Trackpoint next(Trackpoint trackpoint) {
		if (!hasNext(trackpoint)) {
			throw new NoSuchElementException();
		}
		return lookup(trackpoint).next.trackpoint;
	}

	@Override
	public boolean hasPrevious(Trackpoint trackpoint) {
		return lookup(trackpoint).previous != head;
	}

	@Override
	public Trackpoint previous(Trackpoint trackpoint) {
		if (!hasPrevious(trackpoint)) {
			throw new NoSuchElementException();
		}
		return lookup(trackpoint).previous.trackpoint;
	}

	@Override
	public boolean removeAllAfter(Trackpoint trackpoint) {
		Entry entry = lookup(trackpoint);
		if (entry.next == tail) {
			return false;
		}
		entry.next = tail;
		tail.previous = entry;
		return true;
	}

	@Override
	public boolean removeAllBefore(Trackpoint trackpoint) {
		Entry entry = lookup(trackpoint);
		if (entry.previous == head) {
			return false;
		}
		entry.previous = head;
		head.next = entry;
		return true;
	}
	
	@Override
	public boolean remove(Object trackpoint) {
		if (!lookup.containsKey(trackpoint)) {
			return false;
		}
		Entry entry = lookup.remove(trackpoint);
		removeEntry(entry);
		return true;
	}

	private void removeEntry(Entry entry) {
		entry.previous.next = entry.next;
		entry.next.previous = entry.previous;
		size -= 1;
		modCount++;
	}
	
	@Override
	public boolean add(Trackpoint trackpoint) {
		addBefore(trackpoint, tail);
		return true;
	}

    protected Entry addBefore(Trackpoint trackpoint, Entry entry) {
		Entry newEntry = createEntry(trackpoint, entry.previous, entry);
		newEntry.previous.next = newEntry;
		newEntry.next.previous = newEntry;
		size++;
		modCount++;
		return newEntry;
    }

	private Entry createEntry(Trackpoint trackpoint, Entry previous, Entry next) {
		Entry entry = new Entry(trackpoint, previous, next);
		lookup.put(trackpoint, entry);
		return entry;
	}

	@Override
	public TrackpointSequence select(Class<? extends TrackpointAttribute> attribute) {
		TrackpointSequence result = new LinkedTrackpointSequence();
		for (Trackpoint trackpoint : this) {
			if (trackpoint.hasAttribute(attribute)) {
				result.add(trackpoint);
			}
		}
		return result;
	}

	@Override
	public ListIterator<Trackpoint> listIterator(int index) {
		return new TrackpointSequenceIterator(index);
	}

	@Override
	public int size() {
		return size;
	}
	
	private class Entry {
		
		private Trackpoint trackpoint;
		
		private Entry next;
		
		private Entry previous;
		
		private Entry() {
			// entry without trackpoint
		}
		
		private Entry(Trackpoint trackpoint, Entry previous, Entry next) {
			this.trackpoint = trackpoint;
			this.previous = previous;
			this.next = next;
		}
		
	}

	private class TrackpointSequenceIterator implements ListIterator<Trackpoint> {

		private Entry next;
		
		private Entry lastReturned;
		
		private int nextIndex;
		
		private int expectedModCount = modCount;
		
		private TrackpointSequenceIterator(int index) {
		    if (index < 0 || index > size) {
				throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
		    }
		    
		    if (index < (size >> 1)) {
		    	next = head.next;
		    	for (nextIndex = 0; nextIndex < index; nextIndex++) {
		    		next = next.next;
		    	}
		    } else {
		    	next = tail;
		    	for (nextIndex=size; nextIndex>index; nextIndex--) {
		    		next = next.previous;
		    	}
		    }
		}

		@Override
		public boolean hasNext() {
		    return nextIndex != size;
		}
		
		@Override
		public Trackpoint next() {
		    checkForComodification();
		    if (nextIndex == size) {
		    	throw new NoSuchElementException();
		    }

		    lastReturned = next;
		    next = next.next;
		    nextIndex += 1;
		    return lastReturned.trackpoint;
		}
		
		@Override
		public int nextIndex() {
			return nextIndex;
		}

		@Override
		public boolean hasPrevious() {
			return nextIndex != 0;
		}

		@Override
		public Trackpoint previous() {
		    if (nextIndex == 0) {
				throw new NoSuchElementException();
		    }
		    
		    lastReturned = next = next.previous;
			nextIndex -= 1;
			checkForComodification();
			return lastReturned.trackpoint;
		}

		@Override
		public int previousIndex() {
			return nextIndex - 1;
		}
		
		@Override
		public void add(Trackpoint trackpoint) {
		    checkForComodification();
		    lastReturned = head;
		    addBefore(trackpoint, next);
		    nextIndex += 1;
		    expectedModCount += 1;
		}

		@Override
		public void remove() {
			checkForComodification();
			Entry lastNext = lastReturned.next;
			try {
				LinkedTrackpointSequence.this.remove(lastReturned.trackpoint);
			} catch (NoSuchElementException e) {
				throw new IllegalStateException();
			}
			if (next == lastReturned) {
				next = lastNext;
			} else {
				nextIndex--;
			}
			lastReturned = head;
			expectedModCount += 1;
		}

		@Override
		public void set(Trackpoint trackpoint) {
			if (lastReturned == head) {
				throw new IllegalStateException();
			}
			checkForComodification();
			lastReturned.trackpoint = trackpoint;
		}

		final void checkForComodification() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
		
	}

}
