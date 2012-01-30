package com.github.rasifix.trainings.model;

import java.util.List;

public interface TrackpointSequence extends List<Trackpoint> {
	
	boolean isFirst(Trackpoint trackpoint);
	
	Trackpoint getFirst();
	
	void addFirst(Trackpoint trackpoint);
	
	Trackpoint removeFirst();
	
	boolean isLast(Trackpoint trackpoint);
	
	Trackpoint getLast();
	
	void addLast(Trackpoint trackpoint);
	
	Trackpoint removeLast();
	
	boolean hasNext(Trackpoint trackpoint);
	
	Trackpoint next(Trackpoint trackpoint);
	
	boolean hasPrevious(Trackpoint trackpoint);
	
	Trackpoint previous(Trackpoint trackpoint);
	
	boolean removeAllAfter(Trackpoint trackpoint);
	
	boolean removeAllBefore(Trackpoint trackpoint);
	
	/**
	 * Select all {@link Trackpoint}s from this TrackpointSequence that contain 
	 * the given attribute.
	 * 
	 * TODO: does it create an independent sequence?
	 * 
	 * @param attribute the attribute of interest
	 * @return a new TrackpointSequence that is guaranteed to contain only Trackpoints
	 *        with the specified attribute
	 */
	TrackpointSequence select(Class<? extends TrackpointAttribute> attribute);
	
	// 1 2 3 4 5 6 7 8  elapsed time
	// x x - - - x - x  (x = attribute present, - = attribute missing)
	//           '
	//           for how long is this heart rate relevant?
}
